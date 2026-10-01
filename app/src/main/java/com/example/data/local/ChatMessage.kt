package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * ChatMessage Entity - Har message ka record database mein save karne ke liye
 * User aur Nayab AI dono ke messages yahan store hote hain.
 */
@Entity(tableName = "chat_messages")
data class ChatMessage(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val conversationId: String = "default",
    val sender: String, // "user" ya "ai"
    val text: String,
    val timestamp: Long = System.currentTimeMillis(),
    val language: String = "auto", // "sd" (Sindhi), "ur" (Urdu), "en" (English)
    val isAudioAvailable: Boolean = true
)
