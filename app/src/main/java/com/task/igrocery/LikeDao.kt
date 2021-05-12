package com.task.igrocery

import androidx.lifecycle.LiveData
import androidx.room.*
import com.task.igrocery.Model.Like

@Dao
interface LikeDao {

    @Insert
    fun insert(like: Like)

    @Update
    fun update(like: Like)

    @Delete
    fun delete(like: Like)

    @Query("delete from like_table")
    fun deleteAllNotes()

    @Query("select * from like_table")
    fun getAllNotes(): List<Like>
}