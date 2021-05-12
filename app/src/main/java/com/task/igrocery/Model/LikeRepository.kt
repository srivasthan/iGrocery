package com.task.igrocery.Model

import android.app.Application
import androidx.lifecycle.LiveData
import com.task.igrocery.LikeDao
import com.task.igrocery.RoomDB.LikeDatabase
import com.task.igrocery.utils.subscribeOnBackground

class NoteRepository(application: Application) {

    private var noteDao: LikeDao
    private var allNotes: List<Like>

    private val database = LikeDatabase.getInstance(application)

    init {
        noteDao = database.likeDao()
        allNotes = noteDao.getAllNotes()
    }

    fun insert(like: Like) {
        subscribeOnBackground {
            noteDao.insert(like)
        }
    }

    fun update(like: Like) {
        subscribeOnBackground {
            noteDao.update(like)
        }
    }

    fun delete(like: Like) {
        subscribeOnBackground {
            noteDao.delete(like)
        }
    }

    fun deleteAllNotes() {
        subscribeOnBackground {
            noteDao.deleteAllNotes()
        }
    }

    fun getAllNotes(): List<Like> {
        return allNotes
    }

}