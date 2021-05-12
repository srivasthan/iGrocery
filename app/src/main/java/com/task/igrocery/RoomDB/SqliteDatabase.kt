package com.task.igrocery.RoomDB

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.task.igrocery.Model.CartModel
import java.util.*

class SqliteDatabase internal constructor(context: Context?) :
    SQLiteOpenHelper(
        context,
        DATABASE_NAME,
        null,
        DATABASE_VERSION
    ) {
    override fun onCreate(db: SQLiteDatabase) {
        val createContactTable = ("CREATE TABLE "
                + TABLE_CONTACTS + "(" + COLUMN_ID
                + " INTEGER PRIMARY KEY,"
                + COLUMN_IMAGE + " TEXT,"
                + COLUMN_NAME + " TEXT,"
                + COLUMN_PRICE + " TEXT,"
                + COLUMN_QTY + " TEXT,"
                + COLUMN_TOTAL + " TEXT" + ")")
        db.execSQL(createContactTable)
    }

    override fun onUpgrade(
        db: SQLiteDatabase,
        oldVersion: Int,
        newVersion: Int
    ) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_CONTACTS")
        onCreate(db)
    }

    fun listProducts(): ArrayList<CartModel> {
        val sql = "select * from $TABLE_CONTACTS"
        val db = this.readableDatabase
        val storeContacts =
            ArrayList<CartModel>()
        val cursor = db.rawQuery(sql, null)
        if (cursor.moveToFirst()) {
            do {
                val id = cursor.getString(0).toInt()
                val image = cursor.getString(1)
                val name = cursor.getString(2)
                val price = cursor.getString(3)
                val quantity = cursor.getString(4)
                val total = cursor.getString(5)
                storeContacts.add(CartModel(id, image, name, price, quantity, total))
            } while (cursor.moveToNext())
        }
        cursor.close()
        return storeContacts
    }

    fun addItems(products: CartModel) {
        val values = ContentValues()
        values.put(COLUMN_IMAGE, products.image)
        values.put(COLUMN_NAME, products.name)
        values.put(COLUMN_PRICE, products.price)
        values.put(COLUMN_QTY, products.quantity)
        values.put(COLUMN_TOTAL, products.total)
        val db = this.writableDatabase
        db.insert(TABLE_CONTACTS, null, values)
    }

    fun updateProducts(products: CartModel) {
        val values = ContentValues()
        values.put(COLUMN_IMAGE, products.image)
        values.put(COLUMN_NAME, products.name)
        values.put(COLUMN_PRICE, products.price)
        values.put(COLUMN_QTY, products.quantity)
        values.put(COLUMN_TOTAL, products.total)
        val db = this.writableDatabase
        db.update(
            TABLE_CONTACTS,
            values,
            "$COLUMN_ID = ?",
            arrayOf(products.id.toString())
        )
    }

    fun deleteProduct(id: Int) {
        val db = this.writableDatabase
        db.delete(
            TABLE_CONTACTS,
            "$COLUMN_ID = ?",
            arrayOf(id.toString())
        )
    }

    companion object {
        private const val DATABASE_VERSION = 5
        private const val DATABASE_NAME = "Contacts"
        private const val TABLE_CONTACTS = "Contacts"
        private const val COLUMN_ID = "_id"
        private const val COLUMN_IMAGE = "image"
        private const val COLUMN_NAME = "name"
        private const val COLUMN_PRICE = "price"
        private const val COLUMN_QTY = "qty"
        private const val COLUMN_TOTAL = "total"

    }
}