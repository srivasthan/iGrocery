package com.task.igrocery.RoomDB

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.task.igrocery.Model.LikeModel
import java.util.*

class LikeSqlDb internal constructor(context: Context?) :
    SQLiteOpenHelper(
        context,
        DB_NAME,
        null,
        DB_VERSION
    ) {
    override fun onCreate(db: SQLiteDatabase) {
        val createContactTable = ("CREATE TABLE "
                + TABLE_LIKE + "(" + COLUMN_ID
                + " INTEGER PRIMARY KEY,"
                + COLUMN_IMAGE + " TEXT,"
                + COLUMN_NAME + " TEXT,"
                + COLUMN_PRICE + " TEXT,"
                + COLUMN_STATUS + " TEXT" + ")")
        db.execSQL(createContactTable)
    }

    override fun onUpgrade(
        db: SQLiteDatabase,
        oldVersion: Int,
        newVersion: Int
    ) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_LIKE")
        onCreate(db)
    }

    fun listLike(): ArrayList<LikeModel> {
        val sql = "select * from $TABLE_LIKE";
        val db = this.readableDatabase
        val storeContacts =
            ArrayList<LikeModel>()
        val cursor = db.rawQuery(sql, null)
        if (cursor.moveToFirst()) {
            do {
                val id = cursor.getString(0).toInt()
                val image = cursor.getString(1)
                val name = cursor.getString(2)
                val price = cursor.getString(3)
                val status = cursor.getString(4)
                storeContacts.add(LikeModel(id, image, name, price, status))
            } while (cursor.moveToNext())
        }
        cursor.close()
        return storeContacts
    }

    fun addLikeItems(products: LikeModel) {
        val values = ContentValues()
        values.put(COLUMN_IMAGE, products.image)
        values.put(COLUMN_NAME, products.name)
        values.put(COLUMN_PRICE, products.price)
        values.put(COLUMN_STATUS, products.status)

        val db = this.writableDatabase
        db.insert(TABLE_LIKE, null, values)
    }

    fun updateLikeProducts(products: LikeModel) {
        val values = ContentValues()
        values.put(COLUMN_IMAGE, products.image)
        values.put(COLUMN_NAME, products.name)
        values.put(COLUMN_PRICE, products.price)
        values.put(COLUMN_STATUS, products.status)

        val db = this.writableDatabase
        db.update(
            TABLE_LIKE,
            values,
            "$COLUMN_ID = ?",
            arrayOf(products.id.toString())
        )
    }

    fun deleteLikeProduct(id: Int) {
        val db = this.writableDatabase
        db.delete(
            TABLE_LIKE,
            "$COLUMN_ID = ?",
            arrayOf(id.toString())
        )
    }

    companion object {
        private const val DB_VERSION = 5
        private const val DB_NAME = "Like"
        private const val TABLE_LIKE = "Like"
        private const val COLUMN_ID = "_id"
        private const val COLUMN_IMAGE = "image"
        private const val COLUMN_NAME = "name"
        private const val COLUMN_PRICE = "price"
        private const val COLUMN_STATUS = "status"
    }
}