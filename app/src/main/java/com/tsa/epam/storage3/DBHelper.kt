package com.tsa.epam.storage3

import android.content.Context
import androidx.room.Room

object DBHelper {
    private var dbInstance: ItemsDatabase? = null
    private const val name = "db"

    fun getDBInstance(context: Context): ItemsDatabase {
        if (dbInstance == null) {
            dbInstance = Room.databaseBuilder(context, ItemsDatabase::class.java, name)
                .fallbackToDestructiveMigration()
                .build()
        }

        return dbInstance!!
    }
}