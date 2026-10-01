package com.example.ui.screens

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.OpenInNew
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.AddPhotoAlternate
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.RecordVoiceOver
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Translate
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.BuildConfig
import com.example.R
import com.example.ui.theme.AjrakBlue
import com.example.ui.theme.AjrakMaroon
import com.example.ui.theme.SindhiTeal
import com.example.ui.theme.WarmAmber

/**
 * SettingsScreen - Nayab AI ki tarjehaat aur intizam
 * Language, Voice Speed, Theme, API Key aur Chat Clear karne ki sahulat.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    currentLanguage: String,
    voiceSpeed: Float,
    themeMode: String,
    customApiKey: String,
    onLanguageSelected: (String) -> Unit,
    onVoiceSpeedChanged: (Float) -> Unit,
    onThemeModeChanged: (String) -> Unit,
    onCustomApiKeySaved: (String) -> Unit,
    onClearChatHistory: () -> Unit,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier,
    voicePitch: Float = 1.0f,
    onVoicePitchChanged: (Float) -> Unit = {},
    onTestVoice: () -> Unit = {},
    onOpenTtsSettings: () -> Unit = {},
    onOpenVoiceInstallData: () -> Unit = {},
    isLoggedIn: Boolean = false,
    userEmail: String = "",
    userName: String = "",
    onGoogleSignIn: (String, String) -> Unit = { _, _ -> },
    onSignOut: () -> Unit = {},
    onNavigateToHistory: () -> Unit = {},
    customBannerUri: String = "",
    onUpdateBannerUri: (String) -> Unit = {},
    onResetBanner: () -> Unit = {}
) {
    val context = LocalContext.current
    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        uri?.let { onUpdateBannerUri(it.toString()) }
    }
    var tempApiKey by remember(customApiKey) { mutableStateOf(customApiKey) }
    var tempSpeed by remember(voiceSpeed) { mutableFloatStateOf(voiceSpeed) }
    var tempPitch by remember(voicePitch) { mutableFloatStateOf(voicePitch) }
    var showClearDialog by remember { mutableStateOf(false) }
    var showAuthDialog by remember { mutableStateOf(false) }
    var inputEmail by remember { mutableStateOf(if (userEmail.isNotBlank()) userEmail else "dzucshikarpursindh@gmail.com") }
    var inputName by remember { mutableStateOf(if (userName.isNotBlank()) userName else "Sindhi User") }

    if (showClearDialog) {
        AlertDialog(
            onDismissRequest = { showClearDialog = false },
            title = {
                Text(
                    text = when (currentLanguage) {
                        "sd" -> "سموري چيٽ هسٽري صاف ڪريو؟"
                        "ur" -> "تمام چیٹ ہسٹری صاف کریں؟"
                        else -> "Clear All Chat History?"
                    },
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Text(
                    text = when (currentLanguage) {
                        "sd" -> "توهان جا سمورا پراڻا سوال ۽ جواب مستقل طور ڊليٽ ٿي ويندا."
                        "ur" -> "آپ کے تمام پرانے سوالات اور جوابات مستقل طور پر حذف ہو جائیں گے۔"
                        else -> "All saved messages and responses will be permanently removed."
                    }
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        onClearChatHistory()
                        showClearDialog = false
                        Toast.makeText(context, "Chat history cleared!", Toast.LENGTH_SHORT).show()
                    },
                    modifier = Modifier.testTag("confirm_clear_all_button")
                ) {
                    Text(
                        text = when (currentLanguage) {
                            "sd" -> "صاف ڪريو"
                            "ur" -> "صاف کریں"
                            else -> "Clear History"
                        },
                        color = AjrakMaroon,
                        fontWeight = FontWeight.Bold
                    )
                }
            },
            dismissButton = {
                TextButton(onClick = { showClearDialog = false }) {
                    Text(
                        text = when (currentLanguage) {
                            "sd" -> "رد ڪريو"
                            "ur" -> "منسوخ"
                            else -> "Cancel"
                        }
                    )
                }
            }
        )
    }

    if (showAuthDialog) {
        AlertDialog(
            onDismissRequest = { showAuthDialog = false },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = null,
                        tint = AjrakBlue,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (isLoggedIn) {
                            when (currentLanguage) {
                                "sd" -> "گوگل اڪائونٽ پروفائيل"
                                "ur" -> "گوگل اکاؤنٹ پروفائل"
                                else -> "Google Profile"
                            }
                        } else {
                            when (currentLanguage) {
                                "sd" -> "گوگل اڪائونٽ سان سائن اِن"
                                "ur" -> "گوگل اکاؤنٹ سے سائن اِن"
                                else -> "Sign In with Google"
                            }
                        }
                    )
                }
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    if (isLoggedIn) {
                        Text(
                            text = when (currentLanguage) {
                                "sd" -> "توهان هن وقت لاگ اِن آهيو:"
                                "ur" -> "آپ اس وقت لاگ اِن ہیں:"
                                else -> "You are signed in as:"
                            },
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = userName.ifBlank { "User" },
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = userEmail,
                            style = MaterialTheme.typography.bodyMedium,
                            color = AjrakBlue
                        )
                    } else {
                        Text(
                            text = when (currentLanguage) {
                                "sd" -> "پنهنجو گوگل ايميل ۽ نالو تصديق ڪريو:"
                                "ur" -> "اپنا گوگل ای میل اور نام درج کریں:"
                                else -> "Enter your Google email and name:"
                            },
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        OutlinedTextField(
                            value = inputEmail,
                            onValueChange = { inputEmail = it },
                            label = { Text("Google Email") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )
                        OutlinedTextField(
                            value = inputName,
                            onValueChange = { inputName = it },
                            label = { Text("Display Name") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            },
            confirmButton = {
                if (isLoggedIn) {
                    Button(
                        onClick = {
                            onSignOut()
                            showAuthDialog = false
                            Toast.makeText(context, "Signed out successfully", Toast.LENGTH_SHORT).show()
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                    ) {
                        Text(
                            text = when (currentLanguage) {
                                "sd" -> "سائن آئوٽ"
                                "ur" -> "سائن آؤٹ"
                                else -> "Sign Out"
                            }
                        )
                    }
                } else {
                    Button(
                        onClick = {
                            onGoogleSignIn(inputEmail, inputName)
                            showAuthDialog = false
                            Toast.makeText(context, "Signed in as $inputEmail", Toast.LENGTH_SHORT).show()
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = AjrakBlue)
                    ) {
                        Text(
                            text = when (currentLanguage) {
                                "sd" -> "سائن اِن ٿيو"
                                "ur" -> "سائن اِن ہوں"
                                else -> "Sign In"
                            }
                        )
                    }
                }
            },
            dismissButton = {
                TextButton(onClick = { showAuthDialog = false }) {
                    Text(
                        text = when (currentLanguage) {
                            "sd" -> "بند ڪريو"
                            "ur" -> "بند کریں"
                            else -> "Close"
                        }
                    )
                }
            }
        )
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                navigationIcon = {
                    IconButton(
                        onClick = onNavigateBack,
                        modifier = Modifier.testTag("settings_back_button")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                    }
                },
                title = {
                    Text(
                        text = when (currentLanguage) {
                            "sd" -> "سيٽنگس (Settings)"
                            "ur" -> "سیٹنگز (Settings)"
                            else -> "Settings"
                        },
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                    )
                }
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(MaterialTheme.colorScheme.background),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Google Account & Profile Section
            item {
                SettingsCard(
                    title = when (currentLanguage) {
                        "sd" -> "گوگل اڪائونٽ ۽ پروفائيل (Google Account)"
                        "ur" -> "گوگل اکاؤنٹ اور پروفائل (Google Account)"
                        else -> "Google Account & Profile"
                    },
                    icon = Icons.Default.Person
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        if (isLoggedIn) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column {
                                    Text(
                                        text = userName.ifBlank { "Google User" },
                                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    Text(
                                        text = userEmail,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = AjrakBlue
                                    )
                                }
                                Button(
                                    onClick = onSignOut,
                                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                                ) {
                                    Text(
                                        text = when (currentLanguage) {
                                            "sd" -> "سائن آئوٽ"
                                            "ur" -> "سائن آؤٹ"
                                            else -> "Sign Out"
                                        }
                                    )
                                }
                            }
                        } else {
                            Text(
                                text = when (currentLanguage) {
                                    "sd" -> "پنهنجي چيٽ ۽ سوال جواب گوگل اڪائونٽ سان ڳنڍڻ لاءِ لاگ اِن ٿيو."
                                    "ur" -> "اپنی چیٹ اور سوال و جواب گوگل اکاؤنٹ کے ساتھ محفوظ رکھنے کے لیے سائن اِن کریں۔"
                                    else -> "Sign in with your Google account to sync your chats."
                                },
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Button(
                                onClick = { showAuthDialog = true },
                                colors = ButtonDefaults.buttonColors(containerColor = AjrakBlue),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(
                                    text = when (currentLanguage) {
                                        "sd" -> "گوگل سان سائن اِن ڪريو (Sign In)"
                                        "ur" -> "گوگل سے سائن اِن کریں (Sign In)"
                                        else -> "Sign In with Google"
                                    },
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }

            // Quick Chat History Navigation Section
            item {
                SettingsCard(
                    title = when (currentLanguage) {
                        "sd" -> "ڳالهه ٻولهه جو رڪارڊ (Chat History)"
                        "ur" -> "گفتگو کا ریکارڈ (Chat History)"
                        else -> "Chat History"
                    },
                    icon = Icons.Default.History
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(
                            text = when (currentLanguage) {
                                "sd" -> "پنهنجا سمورا اڳوڻا سوال، جواب ۽ گفتگو ڏسو يا محفوظ ڪريو."
                                "ur" -> "اپنے تمام سابقہ سوالات، جوابات اور گفتگو دیکھیں یا کاپی کریں۔"
                                else -> "Browse your previous questions and AI responses."
                            },
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        OutlinedButton(
                            onClick = onNavigateToHistory,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(imageVector = Icons.Default.History, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = when (currentLanguage) {
                                    "sd" -> "هسٽري کوليو (Open History)"
                                    "ur" -> "ہسٹری کھولیں (Open History)"
                                    else -> "Open Chat History"
                                },
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }

            // Language Selection Section
            item {
                SettingsCard(
                    title = when (currentLanguage) {
                        "sd" -> "ٻولي جي چونڊ (Language)"
                        "ur" -> "زبان کا انتخاب (Language)"
                        else -> "Language"
                    },
                    icon = Icons.Default.Translate
                ) {
                    val languages = listOf(
                        Triple("sd", "سنڌي (Sindhi)", "Aap Ka Apna Sindhi Assistant"),
                        Triple("ur", "اردو (Urdu)", "قومی زبان اور مکمل سپورٹ"),
                        Triple("en", "English", "Global language support")
                    )

                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        for ((code, name, subtitle) in languages) {
                            val isSelected = currentLanguage == code
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = if (isSelected) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f) else Color.Transparent,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(12.dp))
                                    .clickable { onLanguageSelected(code) }
                                    .padding(vertical = 4.dp, horizontal = 6.dp)
                                    .testTag("language_option_$code")
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(8.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    RadioButton(
                                        selected = isSelected,
                                        onClick = { onLanguageSelected(code) }
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = name,
                                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                        Text(
                                            text = subtitle,
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Voice Settings Section (Text-To-Speech Speed & Pitch)
            item {
                SettingsCard(
                    title = when (currentLanguage) {
                        "sd" -> "آواز جي رفتار ۽ ترنم (Voice Tuning)"
                        "ur" -> "آواز کی رفتار اور پچ (Voice Tuning)"
                        else -> "Voice Tuning (Speed & Pitch)"
                    },
                    icon = Icons.Default.Speed
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        // Speed Slider
                        Text(
                            text = when (currentLanguage) {
                                "sd" -> "رفتار (Speed: ${String.format("%.2fx", tempSpeed)})"
                                "ur" -> "رفتار (Speed: ${String.format("%.2fx", tempSpeed)})"
                                else -> "Speed: ${String.format("%.2fx", tempSpeed)}"
                            },
                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Slider(
                            value = tempSpeed,
                            onValueChange = {
                                tempSpeed = it
                                onVoiceSpeedChanged(it)
                            },
                            valueRange = 0.6f..1.8f,
                            steps = 5,
                            modifier = Modifier.testTag("voice_speed_slider")
                        )

                        // Speed preset chips
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            val speedPresets = listOf(
                                0.8f to "0.8x",
                                0.95f to if (currentLanguage == "sd") "0.95x (قدرتي سنڌي)" else "0.95x (Natural)",
                                1.15f to "1.15x",
                                1.35f to "1.35x"
                            )
                            for ((preset, label) in speedPresets) {
                                val isSelected = Math.abs(tempSpeed - preset) < 0.05f
                                Surface(
                                    shape = RoundedCornerShape(16.dp),
                                    color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant,
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(16.dp))
                                        .clickable {
                                            tempSpeed = preset
                                            onVoiceSpeedChanged(preset)
                                        }
                                        .padding(horizontal = 10.dp, vertical = 6.dp)
                                ) {
                                    Text(
                                        text = label,
                                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                        color = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(4.dp))

                        // Pitch Slider
                        Text(
                            text = when (currentLanguage) {
                                "sd" -> "آواز جو ترنم (Pitch: ${String.format("%.2fx", tempPitch)})"
                                "ur" -> "آواز کا ترنم (Pitch: ${String.format("%.2fx", tempPitch)})"
                                else -> "Pitch: ${String.format("%.2fx", tempPitch)}"
                            },
                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Slider(
                            value = tempPitch,
                            onValueChange = {
                                tempPitch = it
                                onVoicePitchChanged(it)
                            },
                            valueRange = 0.7f..1.3f,
                            steps = 5,
                            modifier = Modifier.testTag("voice_pitch_slider")
                        )

                        // Pitch preset chips
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            val pitchPresets = listOf(
                                0.85f to if (currentLanguage == "sd") "ڳرو (Deep)" else "بھاری (Deep)",
                                1.0f to if (currentLanguage == "sd") "معمول (Normal)" else "معمول (Normal)",
                                1.15f to if (currentLanguage == "sd") "صاف (Light)" else "ہلکا (Light)"
                            )
                            for ((preset, label) in pitchPresets) {
                                val isSelected = Math.abs(tempPitch - preset) < 0.05f
                                Surface(
                                    shape = RoundedCornerShape(16.dp),
                                    color = if (isSelected) SindhiTeal else MaterialTheme.colorScheme.surfaceVariant,
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(16.dp))
                                        .clickable {
                                            tempPitch = preset
                                            onVoicePitchChanged(preset)
                                        }
                                        .padding(horizontal = 10.dp, vertical = 6.dp)
                                ) {
                                    Text(
                                        text = label,
                                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                        color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(4.dp))

                        // Test Voice Button
                        Button(
                            onClick = onTestVoice,
                            colors = ButtonDefaults.buttonColors(containerColor = SindhiTeal),
                            shape = RoundedCornerShape(20.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("test_voice_button")
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.VolumeUp,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = when (currentLanguage) {
                                    "sd" -> "سنڌي آواز چيڪ ڪريو (Test Voice)"
                                    "ur" -> "سندھی آواز چیک کریں (Test Voice)"
                                    else -> "Test Voice Playback"
                                },
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }

            // Fix Sindhi Voice Guide (سنڌي آواز کي ڪيئن درست ڪجي؟)
            item {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = AjrakMaroon.copy(alpha = 0.08f)
                    ),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(AjrakMaroon),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Build,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = when (currentLanguage) {
                                        "sd" -> "سنڌي آواز جا مسئلا ۽ حل"
                                        "ur" -> "سندھی آواز کے مسائل اور حل"
                                        else -> "Fix Sindhi Voice Guide"
                                    },
                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                    color = AjrakMaroon
                                )
                                Text(
                                    text = "How to get crystal clear native Sindhi audio",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    fontSize = 11.sp
                                )
                            }
                        }

                        Text(
                            text = when (currentLanguage) {
                                "sd" -> "جيڪڏهن سنڌي آواز واضح ناهي يا روبوٽڪ لڳي رهيو آهي، ته ان جو سبب اينڊرائيڊ جي ڊفالٽ اسپيچ سيٽنگس آهي. هيٺ ڏنل 3 آسان مرحلن تي عمل ڪريو:"
                                "ur" -> "اگر سندھی آواز صاف نہیں آ رہی یا روبوٹک لگ رہی ہے، تو اس کی وجہ اینڈرائیڈ کی ڈیفالٹ اسپیچ سیٹنگز ہے۔ نیچے دیے گئے 3 آسان مراحل پر عمل کریں:"
                                else -> "If the Sindhi voice sounds unclear, robotic, or unnatural, follow these 3 quick steps in Android settings:"
                            },
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurface,
                            fontSize = 13.sp,
                            lineHeight = 18.sp
                        )

                        // 3 Steps
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(10.dp))
                                .background(MaterialTheme.colorScheme.surface)
                                .padding(12.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(
                                text = "1. Google Speech Services (Google Text-to-Speech) کو Preferred Engine منتخب کریں۔",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurface,
                                fontWeight = FontWeight.Medium
                            )
                            Text(
                                text = "2. 'Install Voice Data' میں جا کر Sindhi یا Pakistani Urdu پيڪ ڊائون لوڊ ڪريو.",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurface,
                                fontWeight = FontWeight.Medium
                            )
                            Text(
                                text = "3. Voice Speed کو 0.95x پر اور Pitch کو 1.0x پر رکھیں تاکہ تلفظ قدرتی ہو۔",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurface,
                                fontWeight = FontWeight.Medium
                            )
                        }

                        // Direct Action Buttons
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Button(
                                onClick = onOpenTtsSettings,
                                colors = ButtonDefaults.buttonColors(containerColor = AjrakMaroon),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("open_tts_settings_button")
                            ) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.OpenInNew,
                                    contentDescription = null,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = when (currentLanguage) {
                                        "sd" -> "آواز سيٽنگس"
                                        "ur" -> "اسپیچ سیٹنگز"
                                        else -> "TTS Settings"
                                    },
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }

                            OutlinedButton(
                                onClick = onOpenVoiceInstallData,
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("download_voice_data_button")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Download,
                                    contentDescription = null,
                                    modifier = Modifier.size(16.dp),
                                    tint = AjrakMaroon
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = when (currentLanguage) {
                                        "sd" -> "وائيس ڊيٽا"
                                        "ur" -> "وائس ڈیٹا"
                                        else -> "Voice Data"
                                    },
                                    color = AjrakMaroon,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }

            // Speech-to-Text (STT) Section - Android SpeechRecognizer
            item {
                SettingsCard(
                    title = when (currentLanguage) {
                        "sd" -> "آواز جي سڃاڻپ (Speech-to-Text)"
                        "ur" -> "آواز کی شناخت (Speech-to-Text)"
                        else -> "Speech Recognition (STT)"
                    },
                    icon = Icons.Default.Mic
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                                .padding(10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(32.dp)
                                    .clip(CircleShape)
                                    .background(SindhiTeal.copy(alpha = 0.2f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.GraphicEq,
                                    contentDescription = null,
                                    tint = SindhiTeal,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "Android SpeechRecognizer API",
                                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = "Real-time voice-to-text with audio waveform detection",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    fontSize = 11.sp
                                )
                            }
                        }

                        // Supported Locales description
                        Column(
                            modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp),
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Text(
                                text = when (currentLanguage) {
                                    "sd" -> "سپورٽ ٿيل ٻوليون (Supported Locales):"
                                    "ur" -> "معاون زبانیں (Supported Locales):"
                                    else -> "Supported Speech Locales:"
                                },
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.primary
                            )
                            Text(
                                text = "• سنڌي (Sindhi): sd-PK (پاڪستان) / sd",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                fontSize = 12.sp
                            )
                            Text(
                                text = "• اردو (Urdu): ur-PK (پاکستان) / ur",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                fontSize = 12.sp
                            )
                            Text(
                                text = "• English: en-US (United States) / en",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                fontSize = 12.sp
                            )
                        }
                    }
                }
            }

            // Header Banner / Picture Section
            item {
                SettingsCard(
                    title = when (currentLanguage) {
                        "sd" -> "هيڊر تصوير (Header Banner)"
                        "ur" -> "ہیڈر تصویر (Header Banner)"
                        else -> "Header Banner Picture"
                    },
                    icon = Icons.Default.AddPhotoAlternate
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Text(
                            text = when (currentLanguage) {
                                "sd" -> "هيڊر لاءِ پنهنجي موبائل مان تصوير لڳايو يا تبديل ڪريو:"
                                "ur" -> "ہیڈر کے لیے اپنے موبائل سے تصویر لگائیں یا تبدیل کریں:"
                                else -> "Select or update the header banner from your phone:"
                            },
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        Card(
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .aspectRatio(16f / 9f)
                        ) {
                            if (customBannerUri.isNotBlank()) {
                                AsyncImage(
                                    model = customBannerUri,
                                    contentDescription = "Current Header Banner",
                                    contentScale = ContentScale.FillWidth,
                                    modifier = Modifier.fillMaxSize()
                                )
                            } else {
                                Image(
                                    painter = painterResource(id = R.drawable.banner_nayab),
                                    contentDescription = "Default Header Banner",
                                    contentScale = ContentScale.FillWidth,
                                    modifier = Modifier.fillMaxSize()
                                )
                            }
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Button(
                                onClick = {
                                    photoPickerLauncher.launch(
                                        PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                                    )
                                },
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.AddPhotoAlternate,
                                    contentDescription = null,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = when (currentLanguage) {
                                        "sd" -> "تصوير چونڊيو"
                                        "ur" -> "تصویر لگائیں"
                                        else -> "Select Photo"
                                    },
                                    fontSize = 12.sp
                                )
                            }

                            if (customBannerUri.isNotBlank()) {
                                OutlinedButton(
                                    onClick = onResetBanner,
                                    shape = RoundedCornerShape(10.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Refresh,
                                        contentDescription = null,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = when (currentLanguage) {
                                            "sd" -> "اصل حالت"
                                            "ur" -> "اصل حالت"
                                            else -> "Reset"
                                        },
                                        fontSize = 12.sp
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Theme Mode Section
            item {
                SettingsCard(
                    title = when (currentLanguage) {
                        "sd" -> "ٿيم موڊ (Theme)"
                        "ur" -> "تھیم موڈ (Theme)"
                        else -> "Theme Mode"
                    },
                    icon = Icons.Default.Tune
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        val themes = listOf(
                            Triple("system", "System", Icons.Default.Tune),
                            Triple("light", "Light", Icons.Default.LightMode),
                            Triple("dark", "Dark", Icons.Default.DarkMode)
                        )

                        for ((mode, label, icon) in themes) {
                            val isSelected = themeMode == mode
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant,
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(12.dp))
                                    .clickable { onThemeModeChanged(mode) }
                                    .testTag("theme_button_$mode")
                            ) {
                                Row(
                                    modifier = Modifier.padding(vertical = 10.dp),
                                    horizontalArrangement = Arrangement.Center,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = icon,
                                        contentDescription = label,
                                        modifier = Modifier.size(16.dp),
                                        tint = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = label,
                                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                                        color = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Gemini API Key Configuration
            item {
                SettingsCard(
                    title = when (currentLanguage) {
                        "sd" -> "گوگل جيمينائي API Key"
                        "ur" -> "گوگل جیمینائی API Key"
                        else -> "Google Gemini API Key"
                    },
                    icon = Icons.Default.Key
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Text(
                            text = when {
                                tempApiKey.isNotBlank() -> "Custom API Key is configured."
                                BuildConfig.GEMINI_API_KEY.isNotBlank() && BuildConfig.GEMINI_API_KEY != "MY_GEMINI_API_KEY" ->
                                    "Connected automatically via AI Studio environment."
                                else -> "Enter your Gemini API key to enable unlimited generative responses."
                            },
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        OutlinedTextField(
                            value = tempApiKey,
                            onValueChange = { tempApiKey = it },
                            label = { Text("Gemini API Key (AIza...)") },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("api_key_input"),
                            shape = RoundedCornerShape(12.dp),
                            singleLine = true
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.End
                        ) {
                            Button(
                                onClick = {
                                    onCustomApiKeySaved(tempApiKey)
                                    Toast.makeText(context, "API Key Saved!", Toast.LENGTH_SHORT).show()
                                },
                                shape = RoundedCornerShape(20.dp),
                                modifier = Modifier.testTag("save_api_key_button")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = "Save",
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Save Key")
                            }
                        }
                    }
                }
            }

            // Clear Chat History Section
            item {
                SettingsCard(
                    title = when (currentLanguage) {
                        "sd" -> "ڊيٽا ۽ هسٽري (Data)"
                        "ur" -> "ڈیٹا اور ہسٹری (Data)"
                        else -> "Data & History"
                    },
                    icon = Icons.Default.DeleteSweep
                ) {
                    OutlinedButton(
                        onClick = { showClearDialog = true },
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = AjrakMaroon),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("settings_clear_history_btn")
                    ) {
                        Icon(
                            imageVector = Icons.Default.DeleteSweep,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = when (currentLanguage) {
                                "sd" -> "سموري چيٽ هسٽري صاف ڪريو"
                                "ur" -> "تمام چیٹ ہسٹری صاف کریں"
                                else -> "Clear Chat History"
                            },
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            // About Nayab AI Section
            item {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .clip(CircleShape)
                                .background(Brush.linearGradient(listOf(AjrakMaroon, SindhiTeal))),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.AutoAwesome,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "(c) NaYaB-Ai | 2026",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "In the name of Late Father Haq Nawaz Naqsh Nayab Mangi",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                            fontSize = 12.sp
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Developed by : hafeeezmangi",
                            style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Medium),
                            color = MaterialTheme.colorScheme.primary,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                            fontSize = 12.sp
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun SettingsCard(
    title: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    content: @Composable () -> Unit
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = SindhiTeal,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
            content()
        }
    }
}
