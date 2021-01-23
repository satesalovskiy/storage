package com.tsa.epam.storage3

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "ItemsTable")
data class Item(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    val text: String
)