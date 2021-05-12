package com.task.igrocery.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import android.widget.Toast
import androidx.recyclerview.widget.*
import com.task.igrocery.Adapter.CartAdapter
import com.task.igrocery.Adapter.LikeAdapter
import com.task.igrocery.R
import com.task.igrocery.RoomDB.LikeDatabase
import com.task.igrocery.RoomDB.LikeSqlDb
import com.task.igrocery.RoomDB.SqliteDatabase

class LikeViewActivity : AppCompatActivity() {
    private lateinit var dataBase: LikeSqlDb

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_like_view)
        val imgBack: ImageView = findViewById(R.id.img_back)
        val imgCart: ImageView = findViewById(R.id.img_cart)

        dataBase = LikeSqlDb(this)
        val itemList: RecyclerView = findViewById(R.id.like_recyclerview)
        val linearLayoutManager = LinearLayoutManager(this)
        itemList.layoutManager = linearLayoutManager
        itemList.setHasFixedSize(true)

        dataBase = LikeSqlDb(this)

        if (dataBase.listLike().size > 0) {
            val mAdapter = LikeAdapter(this, dataBase.listLike())
            itemList.adapter = mAdapter
        } else {
            Toast.makeText(
                this,
                "There is no contact in the cart. Start adding now",
                Toast.LENGTH_LONG
            ).show()
        }

        imgBack.setOnClickListener {
            val intent = Intent(this, HomeActivity::class.java)
            startActivity(intent)
            finish()
        }

        imgCart.setOnClickListener {
            val intent = Intent(this, CartActivity::class.java)
            startActivity(intent)
            finish()
        }

        // initRecyclerViewFromDB()
    }

//    private fun initRecyclerViewFromDB() {
//        Thread { getItemsAndRunOnUiThread() }.start()
//    }
//
//    private fun getItemsAndRunOnUiThread() {
//        runOnUiThread {
//            val listItems = LikeDatabase.getInstance(this@LikeViewActivity).likeDao().getAllNotes()
//
//            itemList.apply {
//                setHasFixedSize(true)
//                itemList.layoutManager = LinearLayoutManager(context)
//                itemList.adapter = LikeAdapter(listItems)
//            }
//        }
//    }
}