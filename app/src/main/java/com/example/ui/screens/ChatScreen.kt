package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Translate
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.ChatMessage
import com.example.ui.components.ChatBubble
import com.example.ui.components.InputBar
import com.example.ui.theme.AjrakMaroon
import com.example.ui.theme.SindhiTeal

/**
 * ChatScreen - Nayab AI Main Conversation Screen
 * User aur AI ke darmiyan baatcheet, Voice input aur Text-to-Speech playback.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatScreen(
    messages: List<ChatMessage>,
    inputText: String,
    isLoading: Boolean,
    isListening: Boolean,
    isSpeaking: Boolean,
    currentSpeakingId: Long?,
    currentLanguage: String,
    rmsLevel: Float = 0f,
    speechError: String? = null,
    onInputTextChanged: (String) -> Unit,
    onSendMessage: () -> Unit,
    onStartVoiceInput: () -> Unit,
    onToggleSpeech: (ChatMessage) -> Unit,
    onClearChat: () -> Unit,
    onLanguageToggle: () -> Unit,
    onDismissSpeechError: () -> Unit = {},
    onNavigateToSettings: () -> Unit,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val listState = rememberLazyListState()
    var showClearDialog by remember { mutableStateOf(false) }

    // Auto-scroll jab naya message dakhil ho
    LaunchedEffect(messages.size, isLoading) {
        if (messages.isNotEmpty()) {
            listState.animateScrollToItem(messages.size - 1)
        }
    }

    if (showClearDialog) {
        AlertDialog(
            onDismissRequest = { showClearDialog = false },
            title = {
                Text(
                    text = when (currentLanguage) {
                        "sd" -> "ڇا توهان چيٽ هسٽري صاف ڪرڻ چاهيو ٿا؟"
                        "ur" -> "کیا آپ چیٹ ہسٹری صاف کرنا چاہتے ہیں؟"
                        else -> "Clear Chat History?"
                    },
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Text(
                    text = when (currentLanguage) {
                        "sd" -> "هن عمل سان سڀ پراڻا پيغام ختم ٿي ويندا."
                        "ur" -> "اس عمل سے تمام پیغامات حذف ہو جائیں گے۔"
                        else -> "This will remove all conversation history from your device."
                    }
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        onClearChat()
                        showClearDialog = false
                    },
                    modifier = Modifier.testTag("confirm_clear_button")
                ) {
                    Text(
                        text = when (currentLanguage) {
                            "sd" -> "صاف ڪريو"
                            "ur" -> "صاف کریں"
                            else -> "Clear"
                        },
                        color = AjrakMaroon
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
                        modifier = Modifier.testTag("back_button")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back to Home",
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                    }
                },
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(34.dp)
                                .clip(CircleShape)
                                .background(
                                    Brush.linearGradient(listOf(AjrakMaroon, SindhiTeal))
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.AutoAwesome,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                        Column {
                            Text(
                                text = "Nayab AI",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = when (currentLanguage) {
                                    "sd" -> "سنڌي AI مددگار"
                                    "ur" -> "سندھی AI اسسٹنٹ"
                                    else -> "Sindhi AI Assistant"
                                },
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.primary,
                                fontSize = 11.sp
                            )
                        }
                    }
                },
                actions = {
                    IconButton(
                        onClick = onLanguageToggle,
                        modifier = Modifier.testTag("chat_lang_toggle")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Translate,
                            contentDescription = "Toggle Language",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    if (messages.isNotEmpty()) {
                        IconButton(
                            onClick = { showClearDialog = true },
                            modifier = Modifier.testTag("chat_clear_action")
                        ) {
                            Icon(
                                imageVector = Icons.Default.DeleteSweep,
                                contentDescription = "Clear Chat",
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                    IconButton(
                        onClick = onNavigateToSettings,
                        modifier = Modifier.testTag("chat_settings_action")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = "Settings",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            )
        },
        bottomBar = {
            InputBar(
                text = inputText,
                onTextChanged = onInputTextChanged,
                onSendClicked = onSendMessage,
                onMicClicked = onStartVoiceInput,
                isListening = isListening,
                isLoading = isLoading,
                currentLanguage = currentLanguage,
                rmsLevel = rmsLevel,
                speechError = speechError,
                onDismissSpeechError = onDismissSpeechError,
                onLanguageToggle = onLanguageToggle
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(MaterialTheme.colorScheme.background)
        ) {
            if (messages.isEmpty()) {
                // Empty state
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(64.dp)
                                .clip(CircleShape)
                                .background(SindhiTeal.copy(alpha = 0.15f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.AutoAwesome,
                                contentDescription = null,
                                tint = SindhiTeal,
                                modifier = Modifier.size(32.dp)
                            )
                        }
                        Text(
                            text = when (currentLanguage) {
                                "sd" -> "پنهنجي ٻولي ۾ ڪجهه به پڇو"
                                "ur" -> "اپنی زبان میں کوئی بھی سوال پوچھیں"
                                else -> "Ask anything in your language"
                            },
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onBackground
                        )
                        Text(
                            text = when (currentLanguage) {
                                "sd" -> "توهان لکي به سگهو ٿا يا مائڪ واري بٽڻ کي دٻائي ڳالهائي به سگهو ٿا."
                                "ur" -> "آپ لکھ بھی سکتے ہیں اور مائیک کا بٹن دبا کر بول بھی سکتے ہیں۔"
                                else -> "You can type or tap the microphone to speak."
                            },
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontSize = 13.sp
                        )
                    }
                }
            } else {
                LazyColumn(
                    state = listState,
                    modifier = Modifier
                        .fillMaxSize()
                        .weight(1f),
                    contentPadding = PaddingValues(vertical = 12.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    items(
                        items = messages,
                        key = { it.id }
                    ) { message ->
                        val isSpeakingThis = isSpeaking && currentSpeakingId == message.id
                        ChatBubble(
                            message = message,
                            isSpeakingThis = isSpeakingThis,
                            onToggleSpeech = { onToggleSpeech(message) }
                        )
                    }

                    // AI Generating / Thinking indicator
                    if (isLoading) {
                        item {
                            AiThinkingIndicator(currentLanguage = currentLanguage)
                        }
                    }
                }
            }
        }
    }
}

/**
 * AI Thinking / Typing animation
 */
@Composable
fun AiThinkingIndicator(currentLanguage: String) {
    val infiniteTransition = rememberInfiniteTransition(label = "dots")
    val dotScale1 by infiniteTransition.animateFloat(
        initialValue = 0.6f, targetValue = 1.3f,
        animationSpec = infiniteRepeatable(tween(500, delayMillis = 0), RepeatMode.Reverse),
        label = "dot1"
    )
    val dotScale2 by infiniteTransition.animateFloat(
        initialValue = 0.6f, targetValue = 1.3f,
        animationSpec = infiniteRepeatable(tween(500, delayMillis = 150), RepeatMode.Reverse),
        label = "dot2"
    )
    val dotScale3 by infiniteTransition.animateFloat(
        initialValue = 0.6f, targetValue = 1.3f,
        animationSpec = infiniteRepeatable(tween(500, delayMillis = 300), RepeatMode.Reverse),
        label = "dot3"
    )

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(30.dp)
                .clip(CircleShape)
                .background(Brush.linearGradient(listOf(AjrakMaroon, SindhiTeal))),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.AutoAwesome,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(15.dp)
            )
        }
        Spacer(modifier = Modifier.width(8.dp))
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .scale(dotScale1)
                        .clip(CircleShape)
                        .background(SindhiTeal)
                )
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .scale(dotScale2)
                        .clip(CircleShape)
                        .background(AjrakMaroon)
                )
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .scale(dotScale3)
                        .clip(CircleShape)
                        .background(SindhiTeal)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = when (currentLanguage) {
                        "sd" -> "ناياب AI سوچي رهيو آهي..."
                        "ur" -> "نایاب AI سوچ رہا ہے..."
                        else -> "Nayab AI is thinking..."
                    },
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f),
                    fontSize = 12.sp
                )
            }
        }
    }
}
