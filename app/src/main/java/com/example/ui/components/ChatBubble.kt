package com.example.ui.components

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.ChatMessage
import com.example.ui.theme.AjrakMaroon
import com.example.ui.theme.SindhiTeal
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * ChatBubble - User aur Nayab AI ke messages ko display karne ka component
 * Text-to-Speech (TTS) speaker button aur copy actions ke sath.
 */
@Composable
fun ChatBubble(
    message: ChatMessage,
    isSpeakingThis: Boolean,
    onToggleSpeech: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isUser = message.sender == "user"
    val context = LocalContext.current

    val timeFormatted = try {
        val sdf = SimpleDateFormat("hh:mm a", Locale.getDefault())
        sdf.format(Date(message.timestamp))
    } catch (e: Exception) {
        ""
    }

    // Checking if text contains Arabic/Perso-Arabic script for Sindhi/Urdu
    val isArabicScript = message.text.any { it in '\u0600'..'\u06FF' }

    // Audio animation for playing state
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 0.9f,
        targetValue = 1.15f,
        animationSpec = infiniteRepeatable(
            animation = tween(600),
            repeatMode = RepeatMode.Reverse
        ),
        label = "scale"
    )

    val bubbleColor by animateColorAsState(
        targetValue = if (isUser) {
            MaterialTheme.colorScheme.primary
        } else {
            MaterialTheme.colorScheme.surfaceVariant
        },
        label = "bubbleColor"
    )

    val contentColor = if (isUser) {
        MaterialTheme.colorScheme.onPrimary
    } else {
        MaterialTheme.colorScheme.onSurfaceVariant
    }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 5.dp),
        horizontalArrangement = if (isUser) Arrangement.End else Arrangement.Start,
        verticalAlignment = Alignment.Bottom
    ) {
        // AI Avatar
        if (!isUser) {
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(
                        Brush.linearGradient(
                            listOf(AjrakMaroon, SindhiTeal)
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.AutoAwesome,
                    contentDescription = "Nayab AI",
                    tint = Color.White,
                    modifier = Modifier.size(16.dp)
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
        }

        Column(
            modifier = Modifier.widthIn(max = 310.dp),
            horizontalAlignment = if (isUser) Alignment.End else Alignment.Start
        ) {
            Card(
                shape = RoundedCornerShape(
                    topStart = 18.dp,
                    topEnd = 18.dp,
                    bottomStart = if (isUser) 18.dp else 4.dp,
                    bottomEnd = if (isUser) 4.dp else 18.dp
                ),
                colors = CardDefaults.cardColors(containerColor = bubbleColor),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.5.dp),
                modifier = Modifier.testTag(if (isUser) "user_message_bubble" else "ai_message_bubble")
            ) {
                Column(modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp)) {
                    // AI sender label & active indicator
                    if (!isUser) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            modifier = Modifier.padding(bottom = 4.dp)
                        ) {
                            Text(
                                text = "Nayab AI",
                                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.primary
                            )
                            if (isSpeakingThis) {
                                Icon(
                                    imageVector = Icons.Default.GraphicEq,
                                    contentDescription = "Speaking",
                                    tint = SindhiTeal,
                                    modifier = Modifier
                                        .size(16.dp)
                                        .scale(pulseScale)
                                )
                            }
                        }
                    }

                    // Message text
                    Text(
                        text = message.text,
                        style = MaterialTheme.typography.bodyMedium.copy(
                            lineHeight = 22.sp,
                            fontSize = 15.sp
                        ),
                        color = contentColor,
                        textAlign = if (isArabicScript) TextAlign.Right else TextAlign.Left,
                        modifier = Modifier.fillMaxWidth()
                    )

                    // Footer with timestamp and TTS/Copy buttons
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 6.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = timeFormatted,
                            style = MaterialTheme.typography.labelSmall,
                            color = contentColor.copy(alpha = 0.65f),
                            fontSize = 10.sp
                        )

                        // Actions for AI responses: TTS & Copy
                        if (!isUser) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                // Text-to-Speech (TTS) button
                                IconButton(
                                    onClick = onToggleSpeech,
                                    modifier = Modifier
                                        .size(30.dp)
                                        .testTag("tts_button_${message.id}")
                                ) {
                                    Icon(
                                        imageVector = if (isSpeakingThis) Icons.Default.Stop else Icons.AutoMirrored.Filled.VolumeUp,
                                        contentDescription = if (isSpeakingThis) "Stop audio" else "Play speech",
                                        tint = if (isSpeakingThis) AjrakMaroon else contentColor.copy(alpha = 0.75f),
                                        modifier = Modifier.size(18.dp)
                                    )
                                }

                                // Copy button
                                IconButton(
                                    onClick = {
                                        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                        val clip = ClipData.newPlainText("Nayab AI Message", message.text)
                                        clipboard.setPrimaryClip(clip)
                                        Toast.makeText(context, "Text copied!", Toast.LENGTH_SHORT).show()
                                    },
                                    modifier = Modifier
                                        .size(30.dp)
                                        .testTag("copy_button_${message.id}")
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.ContentCopy,
                                        contentDescription = "Copy message",
                                        tint = contentColor.copy(alpha = 0.75f),
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
