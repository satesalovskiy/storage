package com.tsa.epam.storage3

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [Item::class], version = 2)
abstract class ItemsDatabase : RoomDatabase() {
    abstract fun getItemsDao(): ItemsDao
}