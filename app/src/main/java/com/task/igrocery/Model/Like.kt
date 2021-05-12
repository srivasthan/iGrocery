package com.task.igrocery.Model

import androidx.room.Entity
import androidx.room.PrimaryKey

data class LikeList(
    val products: List<Like>
)

@Entity(tableName = "like_table")
data class Like(
    val itemId: String,
    val title: String,
    val image: String,
    val price: String,
    @PrimaryKey(autoGenerate = false) var id: Int? = null
)