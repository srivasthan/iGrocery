package com.task.igrocery.ui

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.transition.TransitionInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.skyhope.showmoretextview.ShowMoreTextView
import com.task.igrocery.Model.CartModel
import com.task.igrocery.Model.LikeModel
import com.task.igrocery.R
import com.task.igrocery.RoomDB.LikeSqlDb
import com.task.igrocery.RoomDB.SqliteDatabase
import java.util.*


class ProductViewActivity : AppCompatActivity() {
    private lateinit var dataBase: SqliteDatabase
    private lateinit var likeDataBase: LikeSqlDb
    var quantity: String = "1"
    var id: Int = 0
    var likeId: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_product_view)

        val itemImage: ImageView = findViewById(R.id.previewIcon)
        val productView: LinearLayout = findViewById(R.id.product_view)

        val description = findViewById<ShowMoreTextView>(R.id.txt_description)
        val itemName: TextView = findViewById(R.id.txt_name)
        val itemPrice: TextView = findViewById(R.id.txt_amount)
        val imgBack: ImageView = findViewById(R.id.img_back)
        val imgCart: ImageView = findViewById(R.id.img_cart)
        val imgLike: ImageView = findViewById(R.id.isLoved)
        val imgUnLike: ImageView = findViewById(R.id.ic_unlike)
        val addButton: Button = findViewById(R.id.btnPurchase)
        val rtlQuantity: RelativeLayout = findViewById(R.id.rtl_quantity)
        val plusButton: Button = findViewById(R.id.btn_plus)
        val txtQuanitty: TextView = findViewById(R.id.quantity)
        val minuButton: Button = findViewById(R.id.btn_minus)

        dataBase = SqliteDatabase(this)

        imgCart.setOnClickListener {
            val intent = Intent(this, CartActivity::class.java)
            startActivity(intent)
            finish()
        }

        val itemId = intent.getStringExtra("item_id")
        val image = intent.getStringExtra("image")
        val name = intent.getStringExtra("name")
        val price = intent.getStringExtra("amount")
        val descr = intent.getStringExtra("description")

        likeDataBase = LikeSqlDb(this)

        getWindow().sharedElementEnterTransition =
            TransitionInflater.from(this).inflateTransition(R.transition.shared_element_transation)
        productView.transitionName = "thumbnailTransition"

        if (likeDataBase.listLike().size > 0) {

            for (element in likeDataBase.listLike()) {
                if (element.name.equals(name)) {
                    imgLike.visibility = View.GONE
                    imgUnLike.visibility = View.VISIBLE
                }
            }
        }

        imgLike.setOnClickListener {

            imgLike.visibility = View.GONE
            imgUnLike.visibility = View.VISIBLE
            likeDataBase.addLikeItems(
                LikeModel(
                    image!!,
                    name!!,
                    price!!,
                    "liked"
                )
            )

        }

        imgUnLike.setOnClickListener {

            if (likeDataBase.listLike().size > 0) {

                for (element in likeDataBase.listLike()) {
                    if (element.name == name) {
                        likeId = element.id
                        imgLike.visibility = View.VISIBLE
                        imgUnLike.visibility = View.GONE
                        likeDataBase.deleteLikeProduct(likeId)
                    }
                }
            }

        }



        if (dataBase.listProducts().size > 0) {
            for (element in dataBase.listProducts()) {
                if (element.name.equals(name)) {
                    addButton.visibility = View.GONE
                    rtlQuantity.visibility = View.VISIBLE
                    txtQuanitty.text = quantity
                    id = element.id
                    quantity = element.quantity
                }
            }
        } else {
            addButton.visibility = View.VISIBLE
            rtlQuantity.visibility = View.GONE
        }


        Glide.with(this).load(image).into(itemImage)
        itemName.text = name
        itemPrice.text = price
        description.text = descr

        description.setShowingLine(2)
        description.addShowMoreText("More")
        description.addShowLessText("Less")
        description.setShowMoreColor(Color.BLUE)
        description.setShowLessTextColor(Color.RED)

        addButton.setOnClickListener {
            addButton.visibility = View.GONE
            rtlQuantity.visibility = View.VISIBLE

            dataBase.addItems(
                CartModel(
                    image!!,
                    name!!,
                    price!!,
                    txtQuanitty.text.toString(),
                    itemId!!
                )
            )
        }

        plusButton.setOnClickListener {
            val quantityChanged: Int = txtQuanitty.text.toString().toInt() + 1
            txtQuanitty.text = quantityChanged.toString()
            dataBase.updateProducts(
                CartModel(
                    Objects.requireNonNull<Any>(id) as Int,
                    image!!,
                    name!!,
                    price!!,
                    quantityChanged.toString(),
                    id.toString()
                )
            )
        }

        minuButton.setOnClickListener {
            val quantityChanged: Int = txtQuanitty.toString().toInt() - 1

            if (quantityChanged < 1) {
                addButton.visibility = View.VISIBLE
                rtlQuantity.visibility = View.GONE
                dataBase.deleteProduct(id)
            } else {
                txtQuanitty.text = quantityChanged.toString()
                dataBase.updateProducts(
                    CartModel(
                        Objects.requireNonNull<Any>(id) as Int,
                        image!!,
                        name!!,
                        price!!,
                        quantityChanged.toString(),
                        id.toString()
                    )
                )
            }
        }

        imgBack.setOnClickListener {
            val intent = Intent(this@ProductViewActivity, HomeActivity::class.java)
            startActivity(intent)
            finish()
        }

    }

    override fun onBackPressed() {
        supportFinishAfterTransition()
        val intent = Intent(this@ProductViewActivity, HomeActivity::class.java)
        startActivity(intent)
        finish()
    }

}