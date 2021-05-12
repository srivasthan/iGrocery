package com.task.igrocery.RoomDB

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.task.igrocery.LikeDao
import com.task.igrocery.Model.Like
import com.task.igrocery.utils.subscribeOnBackground

@Database(entities = [Like::class], version = 1)
abstract class LikeDatabase : RoomDatabase() {

    abstract fun likeDao(): LikeDao

    companion object {
        private var INSTANCE: LikeDatabase? = null

        fun getInstance(context: Context): LikeDatabase {
            if (INSTANCE == null) {
                INSTANCE = Room.databaseBuilder(
                    context.applicationContext,
                    LikeDatabase::class.java, "item.db"
                )
                    .build()
            }
            return INSTANCE!!
        }

//        private val roomCallback = object : Callback() {
//            override fun onCreate(db: SupportSQLiteDatabase) {
//                super.onCreate(db)
//                populateDatabase(instance!!)
//            }
//        }
//
//        private fun populateDatabase(db: LikeDatabase) {
//            val noteDao = db.likeDao()
//            subscribeOnBackground {
//                noteDao.insert(Like("title 1", "desc 1", "", "", 1))
//                noteDao.insert(Like("title 2", "desc 2", "", "", 2))
//                noteDao.insert(Like("title 3", "desc 3", "", "", 3))
//            }
//        }
    }


}