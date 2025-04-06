package com.example.safetyapp

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [TrustedContact::class], version = 1)
abstract class TrustedContactDatabase : RoomDatabase() {
    abstract fun contactDao(): TrustedContactDao

    companion object {
        @Volatile
        private var INSTANCE: TrustedContactDatabase? = null

        fun getDatabase(context: Context): TrustedContactDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    TrustedContactDatabase::class.java,
                    "trusted_contact_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}