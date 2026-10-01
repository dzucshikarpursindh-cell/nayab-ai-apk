package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

/**
 * ChatMessageDao - Database operations ke functions
 * Yahan messages save karna, fetch karna aur clear karna define kiya gaya hai.
 */
@Dao
interface ChatMessageDao {

    // Tamam messages ko waqt ke hisab se hasil karna
    @Query("SELECT * FROM chat_messages WHERE conversationId = :conversationId ORDER BY timestamp ASC")
    fun getMessagesForConversation(conversationId: String = "default"): Flow<List<ChatMessage>>

    // Tamam conversation sessions hasil karna
    @Query("SELECT DISTINCT conversationId FROM chat_messages ORDER BY timestamp DESC")
    fun getAllConversationIds(): Flow<List<String>>

    // Tamam messages hasil karna
    @Query("SELECT * FROM chat_messages ORDER BY timestamp DESC")
    fun getAllMessages(): Flow<List<ChatMessage>>

    // Naya message database mein dakhil karna
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMessage(message: ChatMessage): Long

    // Tamam chat history delete / saaf karna
    @Query("DELETE FROM chat_messages WHERE conversationId = :conversationId")
    suspend fun clearConversation(conversationId: String = "default")

    // Pura database saaf karna
    @Query("DELETE FROM chat_messages")
    suspend fun clearAllMessages()

    // Aakhri message hasil karna
    @Query("SELECT * FROM chat_messages ORDER BY timestamp DESC LIMIT 1")
    suspend fun getLastMessage(): ChatMessage?
}
