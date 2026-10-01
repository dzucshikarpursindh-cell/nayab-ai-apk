package com.example.audio

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.Settings
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import android.speech.tts.Voice
import android.util.Log
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.Locale

/**
 * SpeechManager - Text-to-Speech (TTS) aur Speech-to-Text (STT) ka mukammal manager
 * Sindhi, Urdu aur English awaz ki behtareen rawani aur safai ke liye tayar kiya gaya hai.
 */
class SpeechManager(private val context: Context) {

    private val mainHandler = Handler(Looper.getMainLooper())

    private var tts: TextToSpeech? = null
    private var speechRecognizer: SpeechRecognizer? = null
    private var isTtsReady = false

    // TTS states
    private val _isSpeaking = MutableStateFlow(false)
    val isSpeaking: StateFlow<Boolean> = _isSpeaking.asStateFlow()

    private val _currentSpeakingId = MutableStateFlow<Long?>(null)
    val currentSpeakingId: StateFlow<Long?> = _currentSpeakingId.asStateFlow()

    private val _ttsEngineName = MutableStateFlow<String>("Android TTS")
    val ttsEngineName: StateFlow<String> = _ttsEngineName.asStateFlow()

    // STT (Speech-to-Text) states
    private val _isListening = MutableStateFlow(false)
    val isListening: StateFlow<Boolean> = _isListening.asStateFlow()

    private val _rmsLevel = MutableStateFlow(0.0f)
    val rmsLevel: StateFlow<Float> = _rmsLevel.asStateFlow()

    private val _recognizedPartial = MutableStateFlow("")
    val recognizedPartial: StateFlow<String> = _recognizedPartial.asStateFlow()

    private val _speechError = MutableStateFlow<String?>(null)
    val speechError: StateFlow<String?> = _speechError.asStateFlow()

    init {
        initTts()
    }

    private fun initTts() {
        tts = TextToSpeech(context) { status ->
            if (status == TextToSpeech.SUCCESS) {
                isTtsReady = true
                val defaultEngine = tts?.defaultEngine ?: "Google TTS"
                _ttsEngineName.value = defaultEngine

                tts?.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
                    override fun onStart(utteranceId: String?) {
                        _isSpeaking.value = true
                    }

                    override fun onDone(utteranceId: String?) {
                        _isSpeaking.value = false
                        _currentSpeakingId.value = null
                    }

                    @Deprecated("Deprecated in Java")
                    override fun onError(utteranceId: String?) {
                        _isSpeaking.value = false
                        _currentSpeakingId.value = null
                    }
                })
            } else {
                Log.e("SpeechManager", "TTS initialization failed!")
            }
        }
    }

    /**
     * Text ko saaf karna taake TTS engine asterisks, markdown syntax ya emojis na parhe
     */
    fun cleanTextForSpeech(rawText: String): String {
        return rawText
            // Code blocks hatana
            .replace(Regex("```[a-zA-Z]*\\n[\\s\\S]*?```"), "")
            .replace(Regex("`[^`]+`"), "")
            // Markdown headers hatana (###, ##, #)
            .replace(Regex("^#{1,6}\\s+", RegexOption.MULTILINE), "")
            // Markdown bold/italic symbols hatana (**text**, *text*, __text__, _text_)
            .replace(Regex("\\*\\*([^*]+)\\*\\*"), "$1")
            .replace(Regex("\\*([^*]+)\\*"), "$1")
            .replace(Regex("__([^_]+)__"), "$1")
            .replace(Regex("_([^_]+)_"), "$1")
            .replace("*", "")
            // Bullets aur blockquotes hatana
            .replace(Regex("^[\\s]*[-*+]\\s+", RegexOption.MULTILINE), "")
            .replace(Regex("^[\\s]*>\\s*", RegexOption.MULTILINE), "")
            // URLs hatana
            .replace(Regex("https?://\\S+"), "")
            // Emojis hatana jo awaz mein khalal dalti hain
            .replace(Regex("[\\p{So}\\p{Cn}]"), "")
            // Extra white spaces ko theek karna
            .replace(Regex("\\n{2,}"), "\n")
            .trim()
    }

    /**
     * Text-To-Speech (TTS) ke zariye AI ka jawab sunana
     * Sindhi, Urdu aur English ke liye behtareen voice aur phonetics select karta hai.
     *
     * @param messageId Message ki unique ID
     * @param text Bolne wala text
     * @param language "sd", "ur", "en"
     * @param speed Bolne ki raftar (0.5f to 2.0f, default 0.95f)
     * @param pitch Awaz ka tarannum (0.7f to 1.3f, default 1.0f)
     */
    fun speak(
        messageId: Long,
        text: String,
        language: String,
        speed: Float = 0.95f,
        pitch: Float = 1.0f
    ) {
        if (!isTtsReady || tts == null) return

        if (_isSpeaking.value && _currentSpeakingId.value == messageId) {
            stopSpeaking()
            return
        }

        stopSpeaking()

        val cleanedText = cleanTextForSpeech(text)
        if (cleanedText.isBlank()) return

        applyBestVoiceConfiguration(language)

        try {
            tts?.setSpeechRate(speed.coerceIn(0.6f, 1.8f))
            tts?.setPitch(pitch.coerceIn(0.7f, 1.3f))
            _currentSpeakingId.value = messageId
            _isSpeaking.value = true
            val utteranceId = "msg_$messageId"
            tts?.speak(cleanedText, TextToSpeech.QUEUE_FLUSH, null, utteranceId)
        } catch (e: Exception) {
            Log.e("SpeechManager", "TTS Speak error", e)
            _isSpeaking.value = false
            _currentSpeakingId.value = null
        }
    }

    /**
     * Sindhi aur Urdu ke liye behtareen Native Voice talaash karna
     * Agar Sindhi ki direct voice na miley to Pakistani Urdu voice use karta hai jo
     * Arabic/Sindhi script aur talaffuz (letters ڄ, ڃ, ڳ, ڱ, ح, خ, ص, ق وغیرہ) ko
     * English se 100 guna behtar aur qudrati andaz mein ada karti hai.
     */
    private fun applyBestVoiceConfiguration(language: String) {
        val currentTts = tts ?: return

        when (language) {
            "sd" -> {
                // 1. Check direct Sindhi voice in available voices
                val sindhiVoice = currentTts.voices?.firstOrNull { voice ->
                    val tag = voice.locale.toLanguageTag().lowercase()
                    tag.startsWith("sd") || voice.locale.language.lowercase() == "sd"
                }

                if (sindhiVoice != null) {
                    currentTts.voice = sindhiVoice
                    return
                }

                // 2. Check direct Sindhi locale
                val sindhiPkLocale = Locale.forLanguageTag("sd-PK")
                val sindhiAvailable = currentTts.isLanguageAvailable(sindhiPkLocale)
                if (sindhiAvailable >= TextToSpeech.LANG_AVAILABLE) {
                    currentTts.language = sindhiPkLocale
                    return
                }

                val sindhiGenericLocale = Locale.forLanguageTag("sd")
                if (currentTts.isLanguageAvailable(sindhiGenericLocale) >= TextToSpeech.LANG_AVAILABLE) {
                    currentTts.language = sindhiGenericLocale
                    return
                }

                // 3. Fallback to Pakistani Urdu voice (shares regional Arabic-Sindhi phonetics)
                val urduPkVoice = currentTts.voices?.firstOrNull { voice ->
                    val tag = voice.locale.toLanguageTag().lowercase()
                    tag == "ur-pk" || (voice.locale.language == "ur" && voice.locale.country.equals("PK", ignoreCase = true))
                }

                if (urduPkVoice != null) {
                    currentTts.voice = urduPkVoice
                    return
                }

                val urduLocale = Locale.forLanguageTag("ur-PK")
                if (currentTts.isLanguageAvailable(urduLocale) >= TextToSpeech.LANG_AVAILABLE) {
                    currentTts.language = urduLocale
                    return
                }

                val genericUrdu = Locale.forLanguageTag("ur")
                if (currentTts.isLanguageAvailable(genericUrdu) >= TextToSpeech.LANG_AVAILABLE) {
                    currentTts.language = genericUrdu
                    return
                }

                // 4. Regional fallback (South Asian / Hindi / Arabic)
                val hiLocale = Locale.forLanguageTag("hi-IN")
                if (currentTts.isLanguageAvailable(hiLocale) >= TextToSpeech.LANG_AVAILABLE) {
                    currentTts.language = hiLocale
                    return
                }

                // Only as an absolute last resort
                currentTts.language = Locale.getDefault()
            }
            "ur" -> {
                val urduVoice = currentTts.voices?.firstOrNull { voice ->
                    val tag = voice.locale.toLanguageTag().lowercase()
                    tag.startsWith("ur")
                }
                if (urduVoice != null) {
                    currentTts.voice = urduVoice
                    return
                }

                val urduLocale = Locale.forLanguageTag("ur-PK")
                if (currentTts.isLanguageAvailable(urduLocale) >= TextToSpeech.LANG_AVAILABLE) {
                    currentTts.language = urduLocale
                } else {
                    currentTts.language = Locale.forLanguageTag("ur")
                }
            }
            else -> {
                currentTts.language = Locale.US
            }
        }
    }

    fun stopSpeaking() {
        try {
            tts?.stop()
        } catch (e: Exception) {
            Log.e("SpeechManager", "TTS stop error", e)
        }
        _isSpeaking.value = false
        _currentSpeakingId.value = null
    }

    /**
     * Check if Android SpeechRecognizer is available on this system
     */
    fun isRecognitionAvailable(): Boolean {
        return SpeechRecognizer.isRecognitionAvailable(context)
    }

    /**
     * Speech-to-Text (STT) shuru karna using Android SpeechRecognizer API
     */
    fun startListening(
        language: String,
        onPartialResult: (String) -> Unit = {},
        onFinalResult: (String) -> Unit
    ) {
        stopSpeaking()
        _speechError.value = null
        _recognizedPartial.value = ""

        mainHandler.post {
            try {
                if (!SpeechRecognizer.isRecognitionAvailable(context)) {
                    _speechError.value = when (language) {
                        "sd" -> "هن ڊوائيس تي آواز جي سڃاڻپ دستياب ناهي. گوگل اسٽوڊيو يا اسپيچ سروسز سيٽنگس چيڪ ڪريو."
                        "ur" -> "اس ڈیوائس پر آواز کی پہچان دستیاب نہیں ہے۔ گوگل اسپیچ سروسز چیک کریں۔"
                        else -> "Speech recognition is not available on this device."
                    }
                    _isListening.value = false
                    return@post
                }

                destroyRecognizer()

                speechRecognizer = SpeechRecognizer.createSpeechRecognizer(context).apply {
                    setRecognitionListener(object : RecognitionListener {
                        override fun onReadyForSpeech(params: Bundle?) {
                            _isListening.value = true
                            _speechError.value = null
                            _rmsLevel.value = 0f
                        }

                        override fun onBeginningOfSpeech() {
                            _isListening.value = true
                        }

                        override fun onRmsChanged(rmsdB: Float) {
                            val normalized = ((rmsdB + 2f) / 12f).coerceIn(0.05f, 1.0f)
                            _rmsLevel.value = normalized
                        }

                        override fun onBufferReceived(buffer: ByteArray?) {}

                        override fun onEndOfSpeech() {
                            _rmsLevel.value = 0f
                        }

                        override fun onError(error: Int) {
                            _isListening.value = false
                            _rmsLevel.value = 0f

                            val errorMsg = when (error) {
                                SpeechRecognizer.ERROR_NO_MATCH -> when (language) {
                                    "sd" -> "ڪوبه آواز واضح نه مليو. مهرباني ڪري صاف ۽ ويجهو ڳالهايو."
                                    "ur" -> "کوئی آواز موصول نہیں ہوئی۔ دوبارہ قریب آ کر بولیں۔"
                                    else -> "No speech recognized. Please speak clearly into the microphone."
                                }
                                SpeechRecognizer.ERROR_SPEECH_TIMEOUT -> when (language) {
                                    "sd" -> "ڳالهائڻ جو وقت پورو ٿيو. ٻيهر مائڪ دٻائي ڳالهايو."
                                    "ur" -> "بولنے کا وقت ختم ہو گیا۔ دوبارہ مائیک دبائیں۔"
                                    else -> "Speech timeout. Please tap mic and speak again."
                                }
                                SpeechRecognizer.ERROR_AUDIO -> "Audio microphone error. Check permissions."
                                SpeechRecognizer.ERROR_NETWORK, SpeechRecognizer.ERROR_NETWORK_TIMEOUT ->
                                    "Network connection required for online voice recognition."
                                SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS ->
                                    "Microphone permission is required."
                                SpeechRecognizer.ERROR_RECOGNIZER_BUSY ->
                                    "Voice recognition is busy. Please try again in a second."
                                else -> "Speech recognition error ($error)"
                            }
                            _speechError.value = errorMsg
                        }

                        override fun onResults(results: Bundle?) {
                            _isListening.value = false
                            _rmsLevel.value = 0f
                            val matches = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                            if (!matches.isNullOrEmpty()) {
                                val spokenText = matches[0]
                                _recognizedPartial.value = spokenText
                                onFinalResult(spokenText)
                            }
                        }

                        override fun onPartialResults(partialResults: Bundle?) {
                            val matches = partialResults?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                            if (!matches.isNullOrEmpty()) {
                                val spokenText = matches[0]
                                _recognizedPartial.value = spokenText
                                onPartialResult(spokenText)
                            }
                        }

                        override fun onEvent(eventType: Int, params: Bundle?) {}
                    })
                }

                val intent = createSpeechIntent(language)
                speechRecognizer?.startListening(intent)
                _isListening.value = true

            } catch (e: Exception) {
                Log.e("SpeechManager", "startListening exception", e)
                _isListening.value = false
                _speechError.value = e.message ?: "Failed to start voice recognition"
            }
        }
    }

    fun createSpeechIntent(language: String): Intent {
        return Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, context.packageName)
            putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true)
            putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 3)

            val primaryLang = when (language) {
                "sd" -> "sd-PK"
                "ur" -> "ur-PK"
                else -> "en-US"
            }
            putExtra(RecognizerIntent.EXTRA_LANGUAGE, primaryLang)
            putExtra(RecognizerIntent.EXTRA_LANGUAGE_PREFERENCE, primaryLang)
            putExtra(RecognizerIntent.EXTRA_ONLY_RETURN_LANGUAGE_PREFERENCE, false)

            putExtra(
                "android.speech.extra.EXTRA_ADDITIONAL_LANGUAGES",
                arrayOf("sd-PK", "sd", "ur-PK", "en-US")
            )

            val promptText = when (language) {
                "sd" -> "سنڌي ۾ ڳالهايو (Speak in Sindhi)"
                "ur" -> "اردو میں بولیں (Speak in Urdu)"
                else -> "Speak in English"
            }
            putExtra(RecognizerIntent.EXTRA_PROMPT, promptText)
        }
    }

    fun stopListening() {
        mainHandler.post {
            try {
                speechRecognizer?.stopListening()
            } catch (e: Exception) {
                Log.e("SpeechManager", "stopListening exception", e)
            }
            _isListening.value = false
            _rmsLevel.value = 0f
        }
    }

    fun clearSpeechError() {
        _speechError.value = null
    }

    /**
     * User ki device par Android Text-To-Speech Settings kholna
     * taake user Google Speech Services aur Sindhi/Urdu voice packs install ya adjust kar sake.
     */
    fun openTtsSettings(ctx: Context) {
        try {
            val intent = Intent("com.android.settings.TTS_SETTINGS")
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            ctx.startActivity(intent)
        } catch (e: Exception) {
            try {
                val intent = Intent(Settings.ACTION_SETTINGS)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                ctx.startActivity(intent)
            } catch (_: Exception) {}
        }
    }

    /**
     * Voice data download page kholna
     */
    fun openVoiceInstallData(ctx: Context) {
        try {
            val intent = Intent(TextToSpeech.Engine.ACTION_INSTALL_TTS_DATA)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            ctx.startActivity(intent)
        } catch (e: Exception) {
            openTtsSettings(ctx)
        }
    }

    private fun destroyRecognizer() {
        try {
            speechRecognizer?.destroy()
        } catch (e: Exception) {
            Log.e("SpeechManager", "destroyRecognizer error", e)
        }
        speechRecognizer = null
    }

    fun release() {
        stopSpeaking()
        tts?.shutdown()
        tts = null
        destroyRecognizer()
    }
}
