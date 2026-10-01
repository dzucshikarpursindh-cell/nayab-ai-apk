package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

/**
 * NayabDatabase - Room Database for Nayab AI
 * Chat history ko phone mein mehfooz rakhne ke liye.
 */
@Database(entities = [ChatMessage::class], version = 1, exportSchema = false)
abstract class NayabDatabase : RoomDatabase() {

    abstract fun chatMessageDao(): ChatMessageDao

    companion object {
        @Volatile
        private var INSTANCE: NayabDatabase? = null

        fun getDatabase(context: Context): NayabDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    NayabDatabase::class.java,
                    "nayab_ai_database"
                )
                    .fallbackToDestructiveMigration(dropAllTables = true)
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
