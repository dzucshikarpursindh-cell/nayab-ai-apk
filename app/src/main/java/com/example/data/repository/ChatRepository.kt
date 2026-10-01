package com.example.data.repository

import com.example.data.local.ChatMessage
import com.example.data.local.ChatMessageDao
import com.example.data.remote.GeminiService
import kotlinx.coroutines.flow.Flow

/**
 * ChatRepository - Repository Pattern
 * Room database aur Gemini API ke darmian bridge ka kaam karta hai.
 */
class ChatRepository(
    private val messageDao: ChatMessageDao,
    private val geminiService: GeminiService
) {

    // Tamam messages ka reactive stream
    val conversationMessages: Flow<List<ChatMessage>> =
        messageDao.getMessagesForConversation("default")

    val allMessages: Flow<List<ChatMessage>> =
        messageDao.getAllMessages()

    // Naya message database mein store karna
    suspend fun saveMessage(sender: String, text: String, language: String = "auto"): Long {
        val msg = ChatMessage(
            sender = sender,
            text = text,
            language = language,
            timestamp = System.currentTimeMillis()
        )
        return messageDao.insertMessage(msg)
    }

    // Gemini AI se jawab hasil karna
    suspend fun requestAiResponse(
        prompt: String,
        recentHistory: List<Pair<String, String>>,
        apiKey: String,
        language: String
    ): String {
        return geminiService.generateResponse(
            userPrompt = prompt,
            conversationHistory = recentHistory,
            userApiKey = apiKey,
            targetLanguage = language
        )
    }

    // Chat history delete karna
    suspend fun clearHistory() {
        messageDao.clearConversation("default")
    }
}
