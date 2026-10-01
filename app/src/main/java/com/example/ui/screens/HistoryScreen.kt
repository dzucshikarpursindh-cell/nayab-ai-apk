package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.SmartToy
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.ChatMessage
import com.example.ui.theme.AjrakBlue
import com.example.ui.theme.AjrakMaroon
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * HistoryScreen - ناياب AI چيٽ رڪارڊ / گفتگو کی تاریخ
 * User ki tamam guftagu ka record, waqt, aur search/copy/delete options.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoryScreen(
    currentLanguage: String,
    messages: List<ChatMessage>,
    onClearHistory: () -> Unit,
    onNavigateBack: () -> Unit,
    onSelectMessage: (String) -> Unit = {}
) {
    val clipboardManager = LocalClipboardManager.current
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    var showClearDialog by remember { mutableStateOf(false) }

    val titleText = when (currentLanguage) {
        "sd" -> "ڳالهه ٻولهه جو رڪارڊ (Chat History)"
        "ur" -> "گفتگو کی تاریخ (Chat History)"
        else -> "Chat History"
    }

    val clearAllText = when (currentLanguage) {
        "sd" -> "سڀ صاف ڪريو"
        "ur" -> "تمام صاف کریں"
        else -> "Clear All"
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.History,
                            contentDescription = null,
                            tint = AjrakMaroon,
                            modifier = Modifier.size(22.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = titleText,
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                },
                navigationIcon = {
                    IconButton(
                        onClick = onNavigateBack,
                        modifier = Modifier.testTag("history_back_button")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                actions = {
                    if (messages.isNotEmpty()) {
                        IconButton(
                            onClick = { showClearDialog = true },
                            modifier = Modifier.testTag("clear_history_icon_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = "Clear History",
                                tint = MaterialTheme.colorScheme.error
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        },
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(MaterialTheme.colorScheme.background)
        ) {
            if (messages.isEmpty()) {
                // Empty State
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Box(
                        modifier = Modifier
                            .size(80.dp)
                            .clip(CircleShape)
                            .background(AjrakMaroon.copy(alpha = 0.1f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.Chat,
                            contentDescription = null,
                            tint = AjrakMaroon,
                            modifier = Modifier.size(40.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = when (currentLanguage) {
                            "sd" -> "اڃا تائين ڪو رڪارڊ ناهي"
                            "ur" -> "ابھی کوئی چیٹ ریکارڈ موجود نہیں ہے"
                            else -> "No Chat History Yet"
                        },
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = when (currentLanguage) {
                            "sd" -> "جڏهن توهان ناياب AI سان ڳالهائيندا، هتي رڪارڊ محفوظ ٿيندو."
                            "ur" -> "جب آپ نایاب AI سے گفتگو کریں گے، یہاں تمام ریکارڈ محفوظ ہوگا۔"
                            else -> "Your conversations with Nayab AI will appear here."
                        },
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(messages, key = { it.id }) { message ->
                        val isUser = message.sender.lowercase() == "user"
                        val timeStr = SimpleDateFormat("dd MMM, hh:mm a", Locale.getDefault())
                            .format(Date(message.timestamp))

                        Card(
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = if (isUser) {
                                    MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)
                                } else {
                                    MaterialTheme.colorScheme.surface
                                }
                            ),
                            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onSelectMessage(message.text) }
                                .testTag("history_item_${message.id}")
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Box(
                                            modifier = Modifier
                                                .size(28.dp)
                                                .clip(CircleShape)
                                                .background(
                                                    if (isUser) AjrakBlue.copy(alpha = 0.2f)
                                                    else AjrakMaroon.copy(alpha = 0.2f)
                                                ),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Icon(
                                                imageVector = if (isUser) Icons.Default.Person else Icons.Default.SmartToy,
                                                contentDescription = null,
                                                modifier = Modifier.size(16.dp),
                                                tint = if (isUser) AjrakBlue else AjrakMaroon
                                            )
                                        }
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(
                                            text = if (isUser) {
                                                when (currentLanguage) {
                                                    "sd" -> "توهان (User)"
                                                    "ur" -> "آپ (User)"
                                                    else -> "You"
                                                }
                                            } else {
                                                "Nayab AI"
                                            },
                                            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                                            color = if (isUser) AjrakBlue else AjrakMaroon
                                        )
                                    }

                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(
                                            text = timeStr,
                                            fontSize = 11.sp,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                        IconButton(
                                            onClick = {
                                                clipboardManager.setText(AnnotatedString(message.text))
                                                scope.launch {
                                                    snackbarHostState.showSnackbar(
                                                        if (currentLanguage == "sd") "ٽيڪسٽ ڪاپي ٿي ويو"
                                                        else "ٹیکسٹ کاپی ہو گیا"
                                                    )
                                                }
                                            },
                                            modifier = Modifier.size(32.dp)
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.ContentCopy,
                                                contentDescription = "Copy",
                                                modifier = Modifier.size(16.dp),
                                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                        }
                                    }
                                }

                                Spacer(modifier = Modifier.height(8.dp))

                                Text(
                                    text = message.text,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurface,
                                    lineHeight = 22.sp
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    if (showClearDialog) {
        AlertDialog(
            onDismissRequest = { showClearDialog = false },
            title = {
                Text(
                    text = when (currentLanguage) {
                        "sd" -> "رڪارڊ صاف ڪريو؟"
                        "ur" -> "چیٹ ریکارڈ صاف کریں؟"
                        else -> "Clear History?"
                    }
                )
            },
            text = {
                Text(
                    text = when (currentLanguage) {
                        "sd" -> "ڇا توهان پڪ سان سڄو ڳالهه ٻولهه جو رڪارڊ ڊليٽ ڪرڻ چاهيو ٿا؟"
                        "ur" -> "کیا آپ واقعی تمام ریکارڈ ختم کرنا چاہتے ہیں؟"
                        else -> "Are you sure you want to permanently clear all conversation history?"
                    }
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        onClearHistory()
                        showClearDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) {
                    Text(
                        text = when (currentLanguage) {
                            "sd" -> "ها، ڊليٽ ڪريو"
                            "ur" -> "ہاں، ڈیلیٹ کریں"
                            else -> "Yes, Clear"
                        }
                    )
                }
            },
            dismissButton = {
                TextButton(onClick = { showClearDialog = false }) {
                    Text(
                        text = when (currentLanguage) {
                            "sd" -> "منسوخ"
                            "ur" -> "منسوخ"
                            else -> "Cancel"
                        }
                    )
                }
            }
        )
    }
}
