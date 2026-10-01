package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.LayoutDirection
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.ui.screens.ChatScreen
import com.example.ui.screens.HistoryScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.SettingsScreen
import com.example.ui.screens.SplashScreen
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.viewmodel.NayabViewModel

/**
 * MainActivity - Nayab AI Application Entry Point
 * Routing, Layout Direction (RTL for Sindhi & Urdu), aur Theme control karta hai.
 */
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            val viewModel: NayabViewModel = viewModel()
            val themeMode by viewModel.themeMode.collectAsStateWithLifecycle()
            val currentLanguage by viewModel.selectedLanguage.collectAsStateWithLifecycle()

            val isDark = when (themeMode) {
                "dark" -> true
                "light" -> false
                else -> isSystemInDarkTheme()
            }

            // Sindhi aur Urdu ke liye RTL (Right-to-Left) layout support
            val layoutDirection = if (currentLanguage == "sd" || currentLanguage == "ur") {
                LayoutDirection.Rtl
            } else {
                LayoutDirection.Ltr
            }

            MyApplicationTheme(darkTheme = isDark) {
                CompositionLocalProvider(LocalLayoutDirection provides layoutDirection) {
                    NayabAppNavHost(viewModel = viewModel)
                }
            }
        }
    }
}

/**
 * NayabAppNavHost - App Navigation Controller
 * Home, Chat aur Settings screens ke darmian tabadla.
 */
@Composable
fun NayabAppNavHost(viewModel: NayabViewModel) {
    val navController = rememberNavController()

    val messages by viewModel.messages.collectAsStateWithLifecycle()
    val inputText by viewModel.inputText.collectAsStateWithLifecycle()
    val isLoading by viewModel.isLoading.collectAsStateWithLifecycle()
    val isListening by viewModel.isListening.collectAsStateWithLifecycle()
    val rmsLevel by viewModel.rmsLevel.collectAsStateWithLifecycle()
    val speechError by viewModel.speechError.collectAsStateWithLifecycle()
    val isSpeaking by viewModel.isSpeaking.collectAsStateWithLifecycle()
    val currentSpeakingId by viewModel.currentSpeakingId.collectAsStateWithLifecycle()
    val currentLanguage by viewModel.selectedLanguage.collectAsStateWithLifecycle()
    val voiceSpeed by viewModel.voiceSpeed.collectAsStateWithLifecycle()
    val voicePitch by viewModel.voicePitch.collectAsStateWithLifecycle()
    val themeMode by viewModel.themeMode.collectAsStateWithLifecycle()
    val customApiKey by viewModel.customApiKey.collectAsStateWithLifecycle()
    val allHistoryMessages by viewModel.allHistoryMessages.collectAsStateWithLifecycle()
    val isLoggedIn by viewModel.isLoggedIn.collectAsStateWithLifecycle()
    val userEmail by viewModel.userEmail.collectAsStateWithLifecycle()
    val userName by viewModel.userName.collectAsStateWithLifecycle()
    val customBannerUri by viewModel.customBannerUri.collectAsStateWithLifecycle()

    fun toggleLanguageNext() {
        val nextLang = when (currentLanguage) {
            "sd" -> "ur"
            "ur" -> "en"
            else -> "sd"
        }
        viewModel.setLanguage(nextLang)
    }

    NavHost(
        navController = navController,
        startDestination = "splash"
    ) {
        composable("splash") {
            SplashScreen(
                currentLanguage = currentLanguage,
                onFinishSplash = {
                    navController.navigate("home") {
                        popUpTo("splash") { inclusive = true }
                    }
                }
            )
        }

        composable("home") {
            HomeScreen(
                currentLanguage = currentLanguage,
                recentMessages = messages,
                onStartChat = { prompt ->
                    if (!prompt.isNullOrBlank()) {
                        viewModel.onInputTextChanged(prompt)
                        viewModel.sendMessage(prompt)
                    }
                    navController.navigate("chat")
                },
                onStartVoiceChat = {
                    navController.navigate("chat")
                    viewModel.startVoiceInput()
                },
                onNavigateToSettings = {
                    navController.navigate("settings")
                },
                onNavigateToHistory = {
                    navController.navigate("history")
                },
                onLanguageToggle = {
                    toggleLanguageNext()
                },
                isLoggedIn = isLoggedIn,
                userEmail = userEmail,
                userName = userName,
                onGoogleSignIn = { email, name ->
                    viewModel.signInWithGoogle(email, name)
                },
                onSignOut = {
                    viewModel.signOut()
                },
                customBannerUri = customBannerUri,
                onUpdateBannerUri = { uri ->
                    viewModel.setCustomBannerUri(uri)
                }
            )
        }

        composable("chat") {
            ChatScreen(
                messages = messages,
                inputText = inputText,
                isLoading = isLoading,
                isListening = isListening,
                isSpeaking = isSpeaking,
                currentSpeakingId = currentSpeakingId,
                currentLanguage = currentLanguage,
                rmsLevel = rmsLevel,
                speechError = speechError,
                onInputTextChanged = { viewModel.onInputTextChanged(it) },
                onSendMessage = { viewModel.sendMessage() },
                onStartVoiceInput = { viewModel.startVoiceInput() },
                onToggleSpeech = { viewModel.toggleSpeech(it) },
                onClearChat = { viewModel.clearChatHistory() },
                onLanguageToggle = { toggleLanguageNext() },
                onDismissSpeechError = { viewModel.clearSpeechError() },
                onNavigateToSettings = { navController.navigate("settings") },
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable("history") {
            HistoryScreen(
                currentLanguage = currentLanguage,
                messages = allHistoryMessages,
                onClearHistory = { viewModel.clearChatHistory() },
                onNavigateBack = { navController.popBackStack() },
                onSelectMessage = { text ->
                    viewModel.onInputTextChanged(text)
                    navController.navigate("chat")
                }
            )
        }

        composable("settings") {
            SettingsScreen(
                currentLanguage = currentLanguage,
                voiceSpeed = voiceSpeed,
                voicePitch = voicePitch,
                themeMode = themeMode,
                customApiKey = customApiKey,
                onLanguageSelected = { viewModel.setLanguage(it) },
                onVoiceSpeedChanged = { viewModel.setVoiceSpeed(it) },
                onVoicePitchChanged = { viewModel.setVoicePitch(it) },
                onTestVoice = { viewModel.testVoice() },
                onOpenTtsSettings = { viewModel.openTtsSettings() },
                onOpenVoiceInstallData = { viewModel.openVoiceInstallData() },
                onThemeModeChanged = { viewModel.setThemeMode(it) },
                onCustomApiKeySaved = { viewModel.setCustomApiKey(it) },
                onClearChatHistory = { viewModel.clearChatHistory() },
                onNavigateBack = { navController.popBackStack() },
                isLoggedIn = isLoggedIn,
                userEmail = userEmail,
                userName = userName,
                onGoogleSignIn = { email, name ->
                    viewModel.signInWithGoogle(email, name)
                },
                onSignOut = {
                    viewModel.signOut()
                },
                onNavigateToHistory = {
                    navController.navigate("history")
                },
                customBannerUri = customBannerUri,
                onUpdateBannerUri = { uri ->
                    viewModel.setCustomBannerUri(uri)
                },
                onResetBanner = {
                    viewModel.setCustomBannerUri("")
                }
            )
        }
    }
}

// Greeting function kept for Robolectric test compatibility
@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(text = "Hello $name!", modifier = modifier)
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    MyApplicationTheme { Greeting("Nayab AI") }
}
