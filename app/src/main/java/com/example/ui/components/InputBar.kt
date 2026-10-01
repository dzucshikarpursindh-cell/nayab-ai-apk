package com.example.ui.components

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.MicOff
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.example.ui.theme.AjrakMaroon
import com.example.ui.theme.SindhiTeal

/**
 * InputBar - Chat screen bottom input component
 * Android SpeechRecognizer API voice input aur text message dispatch.
 */
@Composable
fun InputBar(
    text: String,
    onTextChanged: (String) -> Unit,
    onSendClicked: () -> Unit,
    onMicClicked: () -> Unit,
    isListening: Boolean,
    isLoading: Boolean,
    currentLanguage: String,
    modifier: Modifier = Modifier,
    rmsLevel: Float = 0f,
    speechError: String? = null,
    onDismissSpeechError: () -> Unit = {},
    onLanguageToggle: (() -> Unit)? = null
) {
    val context = LocalContext.current

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            onMicClicked()
        }
    }

    val placeholderText = when (currentLanguage) {
        "sd" -> "سنڌي ۾ لکو يا مائڪ دٻائي ڳالهايو..."
        "ur" -> "اردو میں لکھیں یا بولیں..."
        else -> "Type in English or tap mic to speak..."
    }

    val infiniteTransition = rememberInfiniteTransition(label = "micPulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1.0f,
        targetValue = 1.22f,
        animationSpec = infiniteRepeatable(
            animation = tween(500),
            repeatMode = RepeatMode.Reverse
        ),
        label = "micScale"
    )

    Surface(
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 6.dp,
        modifier = modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)) {

            // Speech error banner
            AnimatedVisibility(
                visible = speechError != null,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                if (speechError != null) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 6.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(MaterialTheme.colorScheme.errorContainer)
                            .padding(horizontal = 12.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Warning,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.error,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = speechError,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onErrorContainer,
                            modifier = Modifier.weight(1f),
                            fontSize = 12.sp
                        )
                        IconButton(
                            onClick = onDismissSpeechError,
                            modifier = Modifier.size(24.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Dismiss error",
                                tint = MaterialTheme.colorScheme.error,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                }
            }

            // Active Voice Recognition Banner with Sound Waves
            AnimatedVisibility(
                visible = isListening,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(AjrakMaroon.copy(alpha = 0.1f))
                        .padding(horizontal = 12.dp, vertical = 8.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(10.dp)
                                    .clip(CircleShape)
                                    .background(AjrakMaroon)
                                    .scale(pulseScale)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = when (currentLanguage) {
                                    "sd" -> "آواز ٻڌي رهيو آهي (سنڌي)"
                                    "ur" -> "آواز سنی جا رہی ہے (اردو)"
                                    else -> "Listening via SpeechRecognizer (English)"
                                },
                                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                                color = AjrakMaroon,
                                fontSize = 12.sp
                            )
                        }

                        // Audio Waveform Bar Visualizer
                        SoundWaveVisualizer(rmsLevel = rmsLevel)
                    }

                    // Instruction sub-text
                    Text(
                        text = when (currentLanguage) {
                            "sd" -> "واضح آواز ۾ ڳالهايو، لفظ پاڻمرادو لکبا رهندا..."
                            "ur" -> "صاف آواز میں بولیں، الفاظ اسکرین پر ظاہر ہوں گے..."
                            else -> "Speak clearly, speech will transcribe automatically..."
                        },
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 11.sp,
                        modifier = Modifier.padding(top = 2.dp)
                    )
                }
            }

            // Input Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Microphone button
                Box(contentAlignment = Alignment.Center) {
                    if (isListening) {
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .scale(pulseScale)
                                .clip(CircleShape)
                                .background(AjrakMaroon.copy(alpha = 0.35f))
                        )
                    }

                    IconButton(
                        onClick = {
                            val hasPermission = ContextCompat.checkSelfPermission(
                                context,
                                Manifest.permission.RECORD_AUDIO
                            ) == PackageManager.PERMISSION_GRANTED

                            if (hasPermission) {
                                onMicClicked()
                            } else {
                                permissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
                            }
                        },
                        modifier = Modifier
                            .size(48.dp)
                            .clip(CircleShape)
                            .background(
                                if (isListening) AjrakMaroon else MaterialTheme.colorScheme.surfaceVariant
                            )
                            .testTag("microphone_button")
                    ) {
                        Icon(
                            imageVector = if (isListening) Icons.Default.MicOff else Icons.Default.Mic,
                            contentDescription = if (isListening) "Stop Voice Input" else "Start Voice Input in ${currentLanguage.uppercase()}",
                            tint = if (isListening) Color.White else MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }

                // Text field
                OutlinedTextField(
                    value = text,
                    onValueChange = onTextChanged,
                    placeholder = {
                        Text(
                            text = placeholderText,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                            maxLines = 1
                        )
                    },
                    modifier = Modifier
                        .weight(1f)
                        .testTag("chat_input_field"),
                    shape = RoundedCornerShape(24.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f),
                        focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
                        unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.2f)
                    ),
                    maxLines = 4,
                    textStyle = MaterialTheme.typography.bodyMedium.copy(fontSize = 15.sp)
                )

                // Send button
                val canSend = text.isNotBlank() && !isLoading
                IconButton(
                    onClick = onSendClicked,
                    enabled = canSend,
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(
                            if (canSend) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant
                        )
                        .testTag("send_button")
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(20.dp),
                            color = MaterialTheme.colorScheme.onPrimary,
                            strokeWidth = 2.dp
                        )
                    } else {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.Send,
                            contentDescription = "Send Message",
                            tint = if (canSend) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f),
                            modifier = Modifier.size(22.dp)
                        )
                    }
                }
            }
        }
    }
}

/**
 * Animated Sound Wave Visualizer based on live RMS volume levels
 */
@Composable
fun SoundWaveVisualizer(rmsLevel: Float) {
    val barCount = 5
    val baseLevel = rmsLevel.coerceIn(0.1f, 1.0f)

    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(3.dp),
        modifier = Modifier.padding(horizontal = 4.dp)
    ) {
        for (i in 0 until barCount) {
            val factor = when (i) {
                0 -> 0.4f
                1 -> 0.8f
                2 -> 1.0f
                3 -> 0.7f
                else -> 0.5f
            }
            val targetHeight = (8.dp + (18.dp * baseLevel * factor)).coerceIn(6.dp, 24.dp)

            Box(
                modifier = Modifier
                    .width(3.5.dp)
                    .height(targetHeight)
                    .clip(RoundedCornerShape(2.dp))
                    .background(if (i % 2 == 0) AjrakMaroon else SindhiTeal)
            )
        }
    }
}
