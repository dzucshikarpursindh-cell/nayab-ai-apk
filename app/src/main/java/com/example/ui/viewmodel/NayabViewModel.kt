package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.audio.SpeechManager
import com.example.data.local.ChatMessage
import com.example.data.local.NayabDatabase
import com.example.data.preferences.AppSettings
import com.example.data.remote.GeminiService
import com.example.data.repository.ChatRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

/**
 * NayabViewModel - Pori application ka central state manager
 * Chat, Voice, Settings aur Room Database ko control karta hai.
 */
class NayabViewModel(application: Application) : AndroidViewModel(application) {

    private val appSettings = AppSettings(application)
    private val database = NayabDatabase.getDatabase(application)
    private val repository = ChatRepository(database.chatMessageDao(), GeminiService())
    val speechManager = SpeechManager(application)

    // Chat history database se reactive stream ke zariye
    val messages: StateFlow<List<ChatMessage>> = repository.conversationMessages
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val allHistoryMessages: StateFlow<List<ChatMessage>> = repository.allMessages
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    // UI States
    private val _inputText = MutableStateFlow("")
    val inputText: StateFlow<String> = _inputText.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _selectedLanguage = MutableStateFlow(appSettings.language)
    val selectedLanguage: StateFlow<String> = _selectedLanguage.asStateFlow()

    private val _voiceSpeed = MutableStateFlow(appSettings.voiceSpeed)
    val voiceSpeed: StateFlow<Float> = _voiceSpeed.asStateFlow()

    private val _voicePitch = MutableStateFlow(appSettings.voicePitch)
    val voicePitch: StateFlow<Float> = _voicePitch.asStateFlow()

    private val _themeMode = MutableStateFlow(appSettings.themeMode)
    val themeMode: StateFlow<String> = _themeMode.asStateFlow()

    private val _customApiKey = MutableStateFlow(appSettings.customApiKey)
    val customApiKey: StateFlow<String> = _customApiKey.asStateFlow()

    // User Authentication States
    private val _isLoggedIn = MutableStateFlow(appSettings.isLoggedIn)
    val isLoggedIn: StateFlow<Boolean> = _isLoggedIn.asStateFlow()

    private val _userEmail = MutableStateFlow(appSettings.userEmail)
    val userEmail: StateFlow<String> = _userEmail.asStateFlow()

    private val _userName = MutableStateFlow(appSettings.userName)
    val userName: StateFlow<String> = _userName.asStateFlow()

    private val _userPhoto = MutableStateFlow(appSettings.userPhoto)
    val userPhoto: StateFlow<String> = _userPhoto.asStateFlow()

    private val _customBannerUri = MutableStateFlow(appSettings.customBannerUri)
    val customBannerUri: StateFlow<String> = _customBannerUri.asStateFlow()

    // Voice / Audio States
    val isListening: StateFlow<Boolean> = speechManager.isListening
    val rmsLevel: StateFlow<Float> = speechManager.rmsLevel
    val recognizedPartial: StateFlow<String> = speechManager.recognizedPartial
    val isSpeaking: StateFlow<Boolean> = speechManager.isSpeaking
    val currentSpeakingId: StateFlow<Long?> = speechManager.currentSpeakingId
    val speechError: StateFlow<String?> = speechManager.speechError

    fun isRecognitionAvailable(): Boolean = speechManager.isRecognitionAvailable()

    fun clearSpeechError() {
        speechManager.clearSpeechError()
    }

    fun onInputTextChanged(newText: String) {
        _inputText.value = newText
    }

    /**
     * Naya message bhejna:
     * 1. User message ko Room database mein save karein
     * 2. Gemini AI ko request bhej kar jawab hasil karein
     * 3. AI jawab ko Room database mein save karein
     */
    fun sendMessage(promptText: String? = null) {
        val textToSend = (promptText ?: _inputText.value).trim()
        if (textToSend.isEmpty() || _isLoading.value) return

        _inputText.value = ""
        _isLoading.value = true

        viewModelScope.launch {
            try {
                // User ka message Room mein mehfooz karna
                val currentLang = _selectedLanguage.value
                repository.saveMessage(
                    sender = "user",
                    text = textToSend,
                    language = currentLang
                )

                // Pichhli guftagu ka record tayar karna
                val history = messages.value.map { it.sender to it.text }

                // Gemini API se jawab lena
                val aiReply = repository.requestAiResponse(
                    prompt = textToSend,
                    recentHistory = history,
                    apiKey = _customApiKey.value,
                    language = currentLang
                )

                // AI ka jawab Room mein mehfooz karna
                val savedId = repository.saveMessage(
                    sender = "ai",
                    text = aiReply,
                    language = currentLang
                )

                // Automatic speech preview agar user ne voice se poocha ho
                // (Settings ke mutabiq)
            } catch (e: Exception) {
                repository.saveMessage(
                    sender = "ai",
                    text = "معاف ڪجو، رابطي ۾ ڪا رڪاوٽ پيش آئي آهي. مهرباني ڪري ٻيهر ڪوشش ڪريو.",
                    language = _selectedLanguage.value
                )
            } finally {
                _isLoading.value = false
            }
        }
    }

    /**
     * Voice Input (Speech-to-Text) shuru karna
     * Android SpeechRecognizer API ke zariye live partial aur final results dakhil hotay hain.
     */
    fun startVoiceInput(onComplete: ((String) -> Unit)? = null) {
        if (isListening.value) {
            speechManager.stopListening()
            return
        }
        speechManager.startListening(
            language = _selectedLanguage.value,
            onPartialResult = { partialText ->
                _inputText.value = partialText
            },
            onFinalResult = { finalText ->
                _inputText.value = finalText
                onComplete?.invoke(finalText)
            }
        )
    }

    fun toggleVoiceInput() {
        if (isListening.value) {
            speechManager.stopListening()
        } else {
            startVoiceInput()
        }
    }

    fun stopVoiceInput() {
        speechManager.stopListening()
    }

    /**
     * Text-To-Speech (AI ka jawab sunna)
     */
    fun toggleSpeech(message: ChatMessage) {
        speechManager.speak(
            messageId = message.id,
            text = message.text,
            language = _selectedLanguage.value,
            speed = _voiceSpeed.value,
            pitch = _voicePitch.value
        )
    }

    /**
     * Voice Testing function taake user settings mein Sindhi / Urdu voice check kar sake
     */
    fun testVoice(sampleText: String? = null) {
        val text = sampleText ?: when (_selectedLanguage.value) {
            "sd" -> "السلام عليڪم! مان ناياب AI آهيان. سنڌي ٻوليءَ جو پهريون مصنوعي ذهانت جو مددگار."
            "ur" -> "السلام علیکم! میں نایاب AI ہوں۔ آپ کا اپنا سندھی اور اردو اے آئی اسسٹنٹ۔"
            else -> "Hello! I am Nayab AI, your Sindhi AI Assistant."
        }
        speechManager.speak(
            messageId = -1L,
            text = text,
            language = _selectedLanguage.value,
            speed = _voiceSpeed.value,
            pitch = _voicePitch.value
        )
    }

    fun openTtsSettings() {
        speechManager.openTtsSettings(getApplication())
    }

    fun openVoiceInstallData() {
        speechManager.openVoiceInstallData(getApplication())
    }

    // Settings update functions
    fun setLanguage(lang: String) {
        _selectedLanguage.value = lang
        appSettings.language = lang
    }

    fun setVoiceSpeed(speed: Float) {
        _voiceSpeed.value = speed
        appSettings.voiceSpeed = speed
    }

    fun setVoicePitch(pitch: Float) {
        _voicePitch.value = pitch
        appSettings.voicePitch = pitch
    }

    fun setThemeMode(mode: String) {
        _themeMode.value = mode
        appSettings.themeMode = mode
    }

    fun setCustomApiKey(key: String) {
        _customApiKey.value = key
        appSettings.customApiKey = key
    }

    // Google Sign-In / Account Authentication
    fun signInWithGoogle(email: String, name: String, photoUrl: String = "") {
        val cleanEmail = email.trim()
        val cleanName = if (name.isNotBlank()) name.trim() else cleanEmail.substringBefore("@")
        _userEmail.value = cleanEmail
        _userName.value = cleanName
        _userPhoto.value = photoUrl
        _isLoggedIn.value = true

        appSettings.userEmail = cleanEmail
        appSettings.userName = cleanName
        appSettings.userPhoto = photoUrl
        appSettings.isLoggedIn = true
    }

    fun signOut() {
        _userEmail.value = ""
        _userName.value = ""
        _userPhoto.value = ""
        _isLoggedIn.value = false

        appSettings.userEmail = ""
        appSettings.userName = ""
        appSettings.userPhoto = ""
        appSettings.isLoggedIn = false
    }

    fun setCustomBannerUri(uri: String) {
        _customBannerUri.value = uri
        appSettings.customBannerUri = uri
    }

    fun clearChatHistory() {
        viewModelScope.launch {
            speechManager.stopSpeaking()
            repository.clearHistory()
        }
    }

    override fun onCleared() {
        super.onCleared()
        speechManager.release()
    }
}
