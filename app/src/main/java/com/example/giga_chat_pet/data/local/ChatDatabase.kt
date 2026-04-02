package com.example.giga_chat_pet.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

@Database(
    entities = [LocalMessage::class, LocalConversation::class, UserProfileData::class],
    version = 3,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class ChatDatabase : RoomDatabase() {
    abstract fun messageDao(): MessageDao
    abstract fun conversationDao(): ConversationDao
    abstract fun userProfileDao(): UserProfileDao

    companion object {
        @Volatile
        private var INSTANCE: ChatDatabase? = null

        private val MIGRATION_2_3 = object : Migration(2, 3) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("""
                    CREATE TABLE IF NOT EXISTS `user_profile` (
                        `uid` TEXT PRIMARY KEY NOT NULL,
                        `email` TEXT NOT NULL,
                        `displayName` TEXT,
                        `photoUrl` TEXT,
                        `createdAt` INTEGER NOT NULL
                    )
                """)
            }
        }

        private val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {
                // Create conversations table
                database.execSQL("""
                    CREATE TABLE IF NOT EXISTS `conversations` (
                        `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        `title` TEXT NOT NULL,
                        `createdAt` INTEGER NOT NULL,
                        `lastMessageAt` INTEGER NOT NULL,
                        `lastMessageText` TEXT NOT NULL
                    )
                """)
                
                // Create new messages table with foreign key
                database.execSQL("""
                    CREATE TABLE IF NOT EXISTS `messages_new` (
                        `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        `text` TEXT NOT NULL,
                        `isFromMe` INTEGER NOT NULL,
                        `timestamp` INTEGER NOT NULL,
                        `status` TEXT NOT NULL,
                        `conversationId` INTEGER NOT NULL DEFAULT 0,
                        FOREIGN KEY (`conversationId`) REFERENCES `conversations` (`id`) ON DELETE CASCADE
                    )
                """)
                
                // Copy data from old messages table to new one
                database.execSQL("""
                    INSERT INTO `messages_new` (`id`, `text`, `isFromMe`, `timestamp`, `status`, `conversationId`)
                    SELECT `id`, `text`, `isFromMe`, `timestamp`, `status`, 0 FROM `messages`
                """)
                
                // Drop old messages table
                database.execSQL("DROP TABLE `messages`")
                
                // Rename new table to messages
                database.execSQL("ALTER TABLE `messages_new` RENAME TO `messages`")
                
                // Create index on conversationId
                database.execSQL("""
                    CREATE INDEX IF NOT EXISTS `index_messages_conversationId` ON `messages` (`conversationId`)
                """)
            }
        }

        fun getDatabase(context: Context): ChatDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    ChatDatabase::class.java,
                    "chat_database"
                )
                    .addMigrations(MIGRATION_1_2, MIGRATION_2_3)
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
