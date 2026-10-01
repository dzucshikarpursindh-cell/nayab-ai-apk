package com.example.ui.screens

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
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
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.filled.AddPhotoAlternate
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.RecordVoiceOver
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.R
import com.example.data.local.ChatMessage
import com.example.ui.components.NayabHeader
import com.example.ui.theme.AjrakBlue
import com.example.ui.theme.AjrakMaroon
import com.example.ui.theme.SindhiTeal

/**
 * HomeScreen - Nayab AI ka Welcome Home Screen
 * Hero banner, Google account sign-in/sign-out, chat history, suggestions aur quick chat.
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun HomeScreen(
    currentLanguage: String,
    recentMessages: List<ChatMessage>,
    onStartChat: (prompt: String?) -> Unit,
    onNavigateToSettings: () -> Unit,
    onNavigateToHistory: () -> Unit,
    onLanguageToggle: () -> Unit,
    isLoggedIn: Boolean = false,
    userEmail: String = "",
    userName: String = "",
    onGoogleSignIn: (email: String, name: String) -> Unit = { _, _ -> },
    onSignOut: () -> Unit = {},
    customBannerUri: String = "",
    onUpdateBannerUri: (String) -> Unit = {},
    modifier: Modifier = Modifier,
    onStartVoiceChat: () -> Unit = { onStartChat(null) }
) {
    var showAuthDialog by remember { mutableStateOf(false) }
    var showBannerDialog by remember { mutableStateOf(false) }
    var inputEmail by remember { mutableStateOf("dzucshikarpursindh@gmail.com") }
    var inputName by remember { mutableStateOf("Sindhi User") }

    // Android Photo Picker for original pristine banner
    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        uri?.let { onUpdateBannerUri(it.toString()) }
    }

    if (showBannerDialog) {
        AlertDialog(
            onDismissRequest = { showBannerDialog = false },
            title = {
                Text(
                    text = when (currentLanguage) {
                        "sd" -> "هيڊر بينر جا اختيار"
                        "ur" -> "ہیڈر بینر کے اختیارات"
                        else -> "Header Banner Options"
                    },
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text(
                        text = when (currentLanguage) {
                            "sd" -> "پنهنجي موبائل گيلري مان نئين تصوير چونڊيو يا اصل بينر بحال ڪريو:"
                            "ur" -> "اپنے موبائل گیلری سے نئی تصویر منتخب کریں یا اصل بینر بحال کریں:"
                            else -> "Choose a new picture from your phone gallery or restore the original banner:"
                        },
                        style = MaterialTheme.typography.bodyMedium
                    )

                    Button(
                        onClick = {
                            showBannerDialog = false
                            photoPickerLauncher.launch(
                                PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                            )
                        },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.AddPhotoAlternate,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = when (currentLanguage) {
                                "sd" -> "گيلري مان تصوير لڳايو"
                                "ur" -> "گیلری سے تصویر لگائیں"
                                else -> "Pick from Gallery"
                            }
                        )
                    }

                    if (customBannerUri.isNotBlank()) {
                        OutlinedButton(
                            onClick = {
                                showBannerDialog = false
                                onUpdateBannerUri("")
                            },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Refresh,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = when (currentLanguage) {
                                    "sd" -> "اصل بينر بحال ڪريو"
                                    "ur" -> "اصل بینر بحال کریں"
                                    else -> "Reset to Original Banner"
                                }
                            )
                        }
                    }
                }
            },
            confirmButton = {},
            dismissButton = {
                TextButton(onClick = { showBannerDialog = false }) {
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

    val welcomeMessage = when (currentLanguage) {
        "sd" -> "السلام عليڪم، مان ناياب AI آهيان. مان توهان جي ڇا مدد ڪري سگهان ٿو؟"
        "ur" -> "السلام علیکم، میں نایاب AI ہوں۔ آپ کی کیا مدد کر سکتا ہوں؟"
        else -> "Assalamualaikum, I am Nayab AI. How can I help you today?"
    }

    val samplePrompts = when (currentLanguage) {
        "sd" -> listOf(
            "سنڌ جي تاريخ ۽ موهن جو دڙو بابت ٻڌايو",
            "شاهه عبداللطيف ڀٽائي جو ڪو بيت ٻڌايو",
            "سنڌي اجرڪ جي روايت ڇا آهي؟",
            "جديد AI اسان جي روزمرهه زندگي ۾ ڪيئن ڪم ڪري ٿي؟"
        )
        "ur" -> listOf(
            "سندھ کی تاریخی ثقافت اور اجرک کی اہمیت بتائیں",
            "شاہ عبداللطیف بھٹائی کا خوبصورت کلام سنائیں",
            "موہنجودڑو کے آثار کے بارے میں معلومات",
            "مصنوعی ذہانت (AI) کیا ہے؟ آسان الفاظ میں سمجھائیں"
        )
        else -> listOf(
            "Tell me about the rich history of Sindh",
            "Share poetry of Shah Abdul Latif Bhittai",
            "What is the significance of the Sindhi Ajrak?",
            "Explain modern AI technology in simple words"
        )
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        NayabHeader(
            currentLanguage = currentLanguage,
            onLanguageClick = onLanguageToggle,
            onSettingsClick = onNavigateToSettings,
            onHistoryClick = onNavigateToHistory,
            isLoggedIn = isLoggedIn,
            userEmail = userEmail,
            onAuthClick = { showAuthDialog = true },
            onClearChatClick = null
        )

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Google Account Status Banner (if not logged in)
            item {
                Card(
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (isLoggedIn) AjrakBlue.copy(alpha = 0.12f)
                        else MaterialTheme.colorScheme.surfaceVariant
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { showAuthDialog = true }
                        .testTag("auth_status_card")
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 14.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(34.dp)
                                    .clip(CircleShape)
                                    .background(if (isLoggedIn) AjrakBlue else MaterialTheme.colorScheme.primary),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = if (isLoggedIn) Icons.Default.Check else Icons.Default.Person,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = if (isLoggedIn) (userName.ifBlank { "گوگل يوزر" })
                                    else when (currentLanguage) {
                                        "sd" -> "گوگل اڪائونٽ سان سائن اِن ڪريو"
                                        "ur" -> "گوگل اکاؤنٹ سے سائن اِن کریں"
                                        else -> "Sign In with Google"
                                    },
                                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = if (isLoggedIn) userEmail
                                    else when (currentLanguage) {
                                        "sd" -> "پنهنجي چيٽ محفوظ رکو"
                                        "ur" -> "اپنی چیٹ اور ریکارڈ محفوظ رکھیں"
                                        else -> "Keep your chats synchronized"
                                    },
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        TextButton(
                            onClick = { showAuthDialog = true },
                            modifier = Modifier.testTag("auth_action_text_button")
                        ) {
                            Text(
                                text = if (isLoggedIn) {
                                    when (currentLanguage) {
                                        "sd" -> "سائن آئوٽ"
                                        "ur" -> "سائن آؤٹ"
                                        else -> "Sign Out"
                                    }
                                } else {
                                    when (currentLanguage) {
                                        "sd" -> "سائن اِن"
                                        "ur" -> "سائن اِن"
                                        else -> "Sign In"
                                    }
                                },
                                fontWeight = FontWeight.Bold,
                                color = if (isLoggedIn) MaterialTheme.colorScheme.error else AjrakBlue
                            )
                        }
                    }
                }
            }

            // Hero Banner Card with Pristine Artwork (Full 16:9 aspect ratio, un-obscured)
            item {
                Card(
                    shape = RoundedCornerShape(18.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(18.dp))
                        .clickable { showBannerDialog = true }
                        .testTag("hero_banner_card")
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .aspectRatio(16f / 9f)
                    ) {
                        if (customBannerUri.isNotBlank()) {
                            AsyncImage(
                                model = customBannerUri,
                                contentDescription = "NaYaB-Ai Your Sindhi Ai Assistant Header Banner",
                                contentScale = ContentScale.FillWidth,
                                modifier = Modifier.fillMaxSize()
                            )
                        } else {
                            Image(
                                painter = painterResource(id = R.drawable.banner_nayab),
                                contentDescription = "NaYaB-Ai Your Sindhi Ai Assistant Header Banner",
                                contentScale = ContentScale.FillWidth,
                                modifier = Modifier.fillMaxSize()
                            )
                        }

                        // Floating "Change Banner" Pill Badge
                        Surface(
                            shape = RoundedCornerShape(20.dp),
                            color = Color.Black.copy(alpha = 0.65f),
                            modifier = Modifier
                                .align(Alignment.BottomEnd)
                                .padding(10.dp)
                                .clickable { showBannerDialog = true }
                                .testTag("change_banner_pill")
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.AddPhotoAlternate,
                                    contentDescription = "Change Banner",
                                    tint = Color.White,
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = when (currentLanguage) {
                                        "sd" -> "بينر تبديل ڪريو"
                                        "ur" -> "بینر تبدیل کریں"
                                        else -> "Change Banner"
                                    },
                                    color = Color.White,
                                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                    fontSize = 11.sp
                                )
                            }
                        }
                    }
                }
            }

            // Welcome Card & Action Buttons
            item {
                Card(
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant
                    ),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(18.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .clip(CircleShape)
                                .background(
                                    Brush.linearGradient(listOf(AjrakMaroon, SindhiTeal))
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.AutoAwesome,
                                contentDescription = "Nayab AI Icon",
                                tint = Color.White,
                                modifier = Modifier.size(26.dp)
                            )
                        }
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = welcomeMessage,
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.SemiBold,
                                lineHeight = 24.sp,
                                fontSize = 16.sp
                            ),
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(14.dp))

                        // Primary Actions: Chat & Voice Prompt
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Button(
                                onClick = { onStartChat(null) },
                                shape = RoundedCornerShape(24.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = MaterialTheme.colorScheme.primary
                                ),
                                modifier = Modifier
                                    .weight(1f)
                                    .height(48.dp)
                                    .testTag("start_chat_button")
                            ) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.Chat,
                                    contentDescription = "Chat",
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = when (currentLanguage) {
                                        "sd" -> "چيٽ"
                                        "ur" -> "چیٹ"
                                        else -> "Chat"
                                    },
                                    fontWeight = FontWeight.Bold
                                )
                            }

                            Button(
                                onClick = onStartVoiceChat,
                                shape = RoundedCornerShape(24.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = AjrakMaroon
                                ),
                                modifier = Modifier
                                    .weight(1.2f)
                                    .height(48.dp)
                                    .testTag("home_voice_prompt_button")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Mic,
                                    contentDescription = "Voice Input",
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = when (currentLanguage) {
                                        "sd" -> "ڳالهايو (Voice)"
                                        "ur" -> "بولیں (Voice)"
                                        else -> "Voice Input"
                                    },
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // History Shortcut Button
                        OutlinedButton(
                            onClick = onNavigateToHistory,
                            shape = RoundedCornerShape(20.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(44.dp)
                                .testTag("home_history_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.History,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp),
                                tint = AjrakBlue
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = when (currentLanguage) {
                                    "sd" -> "ڳالهه ٻولهه جو رڪارڊ (Chat History)"
                                    "ur" -> "گفتگو کا ریکارڈ (Chat History)"
                                    else -> "View Chat History"
                                },
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }
            }

            // Suggested Topics Section
            item {
                Text(
                    text = when (currentLanguage) {
                        "sd" -> "تجويز ڪيل سوال (Suggested Topics)"
                        "ur" -> "تجویز کردہ سوالات (Suggested Topics)"
                        else -> "Suggested Topics"
                    },
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onBackground
                )
            }

            item {
                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    for (prompt in samplePrompts) {
                        Surface(
                            shape = RoundedCornerShape(20.dp),
                            color = MaterialTheme.colorScheme.surface,
                            tonalElevation = 2.dp,
                            shadowElevation = 1.dp,
                            modifier = Modifier
                                .clip(RoundedCornerShape(20.dp))
                                .clickable { onStartChat(prompt) }
                                .testTag("prompt_chip_${prompt.take(10)}")
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.AutoAwesome,
                                    contentDescription = null,
                                    tint = SindhiTeal,
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = prompt,
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Medium
                                    ),
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }
                    }
                }
            }

            // Feature Highlights
            item {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.RecordVoiceOver,
                                contentDescription = null,
                                tint = AjrakMaroon,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = when (currentLanguage) {
                                    "sd" -> "آواز ۽ لکت (Voice & Speech Supported)"
                                    "ur" -> "آواز اور تحریر (Voice & Speech Supported)"
                                    else -> "Voice & Text Supported"
                                },
                                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                        Text(
                            text = when (currentLanguage) {
                                "sd" -> "ناياب AI سنڌي، اردو ۽ انگريزي ۾ پيغام ٻڌي به سگهي ٿو ۽ پنهنجو جواب آواز ۾ پڙهي به ٻڌائي سگهي ٿو."
                                "ur" -> "نایاب AI سندھی، اردو اور انگریزی میں آپ کا پیغام سن بھی سکتا ہے اور ٹیکسٹ ٹو اسپیچ کے ذریعے جواب سنا بھی سکتا ہے۔"
                                else -> "Nayab AI listens via microphone and speaks responses aloud with built-in Text-to-Speech in Sindhi, Urdu, and English."
                            },
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }

    // Google Sign-In & Profile Dialog
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
                                "sd" -> "توهان هن وقت لاگ اِن ٿيل آهيو:"
                                "ur" -> "آپ اس وقت لاگ اِن ہیں:"
                                else -> "You are currently signed in as:"
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
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                    ) {
                        Icon(imageVector = Icons.Default.ExitToApp, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
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
}
