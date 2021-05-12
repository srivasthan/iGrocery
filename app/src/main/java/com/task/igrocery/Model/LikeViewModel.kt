package com.task.igrocery.Model

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData

class LikeViewModel(app: Application) : AndroidViewModel(app) {

    private val repository = NoteRepository(app)
    private val allNotes = repository.getAllNotes()

    fun insert(like: Like) {
        repository.insert(like)
    }

    fun update(like: Like) {
        repository.update(like)
    }

    fun delete(like: Like) {
        repository.delete(like)
    }

    fun deleteAllNotes() {
        repository.deleteAllNotes()
    }

    fun getAllNotes(): List<Like> {
        return allNotes
    }

}