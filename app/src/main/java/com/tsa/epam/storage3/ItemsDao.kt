package com.tsa.epam.storage3

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
abstract class ItemsDao {

    @Insert
    abstract fun insertItem(item: Item)

    @Query("SELECT * FROM ItemsTable")
    abstract fun getAllItems(): List<Item>
}