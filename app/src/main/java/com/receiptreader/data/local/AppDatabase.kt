package com.receiptreader.data.local

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import android.content.Context

/**
 * Room Database for the Receipt Scanner app.
 * 
 * PRIVACY BY DESIGN - DATABASE SECURITY:
 * - Data is stored in the app's private internal storage
 * - SQLite encryption is handled by the Android OS sandbox
 * - No external access via Content Provider
 * - android:allowBackup="false" prevents cloud backup
 * 
 * Note: Database version bumped to 2 for new schema with category/imagePath fields.
 */
@Database(entities = [Receipt::class], version = 2, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    
    abstract fun receiptDao(): ReceiptDao
    
    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null
        
        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "receipt_database"
                )
                .fallbackToDestructiveMigration() // Recreate DB on schema change
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
