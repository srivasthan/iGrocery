package com.task.igrocery.ui

import android.animation.Animator
import android.animation.ObjectAnimator
import android.content.Intent
import android.content.res.Resources
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateInterpolator
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.task.igrocery.Adapter.CartAdapter
import com.task.igrocery.R
import com.task.igrocery.RoomDB.SqliteDatabase
import kotlinx.android.synthetic.main.activity_cart.*


class CartActivity : AppCompatActivity() {

    private lateinit var dataBase: SqliteDatabase
    var totalPrice = 0
    var finalAmount = 0
    var taxAmount = 0
    private lateinit var grandTotal: TextView
    private lateinit var gdTotal: TextView
    private lateinit var subTotal: TextView
    private lateinit var taxTotal: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cart)

        gdTotal = findViewById(R.id.gd_total)
        grandTotal = findViewById(R.id.txt_grandtotal)
        subTotal = findViewById(R.id.txt_subtotal_price)
        taxTotal = findViewById(R.id.txt_tax_price)
        val btnCanceled: ImageView = findViewById(R.id.isCanceled)
        val home: ImageView = findViewById(R.id.ic_home)
        val rtlUp: RelativeLayout = findViewById(R.id.rtl_up)
        val rtlDown: RelativeLayout = findViewById(R.id.rtl_down)
        val upArrow = findViewById<ImageView>(R.id.img_up)
        val downArrow = findViewById<ImageView>(R.id.img_down)
        val itemCount = findViewById<TextView>(R.id.item_count)
        val purchase: Button = findViewById(R.id.btnPurchase)
        val checkout: Button = findViewById(R.id.btncheckout)

        val contactView: RecyclerView = findViewById(R.id.cart_recyclerview)
        val linearLayoutManager = LinearLayoutManager(this)
        contactView.layoutManager = linearLayoutManager
        contactView.setHasFixedSize(true)

        dataBase = SqliteDatabase(this)

        if (dataBase.listProducts().size > 0) {
            itemCount.setText("My Cart (${dataBase.listProducts().size})")

            val mAdapter = CartAdapter(this, dataBase.listProducts())
            contactView.adapter = mAdapter
        } else {
            Toast.makeText(
                this,
                "There is no contact in the cart. Start adding now",
                Toast.LENGTH_LONG
            ).show()
        }

        purchase.setOnClickListener {
            val intent = Intent(this, OrderActivity::class.java)
            startActivity(intent)
        }


        for (element in dataBase.listProducts()) {
            var priceAmount: String = element.price.substring(1)
            priceAmount = priceAmount.replace(",", "")
            val productPrice: Int = priceAmount.toInt()
            totalPrice += element.quantity.toInt() * productPrice
        }

        upArrow.setOnClickListener {
            rtlUp.visibility = View.GONE
            rtlDown.visibility = View.VISIBLE
            slideUp(rtlDown)
        }

        downArrow.setOnClickListener {
            slideDown(rtlDown)
            rtlUp.visibility = View.VISIBLE
        }

        subTotal.text = "₹ " + totalPrice.toString()
        taxAmount = totalPrice / 100 * 5
        taxTotal.text = "₹ " + taxAmount.toString()
        finalAmount = totalPrice + taxAmount + 170

        grandTotal.text = "₹ " + finalAmount.toString()
        gdTotal.text = "₹ " + finalAmount.toString()

        isCanceled.setOnClickListener {
            val intent = Intent(this, HomeActivity::class.java)
            startActivity(intent)
            finish()
        }

        home.setOnClickListener {
            val intent = Intent(this, HomeActivity::class.java)
            startActivity(intent)
            finish()
        }

        checkout.setOnClickListener {
            val intent = Intent(this, OrderActivity::class.java)
            startActivity(intent)
        }

    }

    fun productIncrement(price: String, newquanity: String, quantity: String) {
        val changeQuantity: Int = newquanity.toInt() - quantity.toInt()
        val calculateChangedPrice: Int = price.toInt() * changeQuantity
        totalPrice = totalPrice + calculateChangedPrice
        subTotal.text = "₹ " + totalPrice.toString()
        taxAmount = totalPrice / 100 * 5
        taxTotal.text = "₹ " + taxAmount.toString()
        finalAmount = totalPrice + taxAmount + 170

        gdTotal.text = "₹ " + finalAmount.toString()
        grandTotal.text = "₹ " + finalAmount.toString()
    }

    fun productDecrement(price: String, newquanity: String, quantity: String) {
        val changeQuantity: Int = quantity.toInt() - newquanity.toInt()
        val calculateChangedPrice: Int = price.toInt() * changeQuantity
        totalPrice = totalPrice - calculateChangedPrice
        subTotal.text = "₹ " + totalPrice.toString()
        taxAmount = totalPrice / 100 * 5
        taxTotal.text = "₹ " + taxAmount.toString()
        finalAmount = totalPrice + taxAmount + 170

        gdTotal.text = "₹ " + finalAmount.toString()
        grandTotal.text = "₹ " + finalAmount.toString()
    }

    private fun slideUp(view: View) {
        view.visibility = View.VISIBLE
        val layoutParams = view.layoutParams
        layoutParams.height = 1
        view.layoutParams = layoutParams
        view.measure(
            View.MeasureSpec.makeMeasureSpec(
                Resources.getSystem().getDisplayMetrics().widthPixels,
                View.MeasureSpec.EXACTLY
            ),
            View.MeasureSpec.makeMeasureSpec(
                0,
                View.MeasureSpec.UNSPECIFIED
            )
        )
        val height = view.measuredHeight
        val valueAnimator = ObjectAnimator.ofInt(1, height)
        valueAnimator.addUpdateListener { animation ->
            val value = animation.animatedValue as Int
            if (height > value) {
                val layoutParams = view.layoutParams
                layoutParams.height = value
                view.layoutParams = layoutParams
            } else {
                val layoutParams = view.layoutParams
                layoutParams.height = ViewGroup.LayoutParams.WRAP_CONTENT
                view.layoutParams = layoutParams
            }
        }
        valueAnimator.start()
    }

    private fun slideDown(view: View) {
        view.post {
            val height = view.height
            val valueAnimator = ObjectAnimator.ofInt(height, 0)
            valueAnimator.addUpdateListener { animation ->
                val value = animation.animatedValue as Int
                if (value > 0) {
                    val layoutParams = view.layoutParams
                    layoutParams.height = value
                    view.layoutParams = layoutParams
                } else {
                    view.visibility = View.GONE
                }
            }
            valueAnimator.start()
        }
    }


    override fun onBackPressed() {
        val intent = Intent(this, HomeActivity::class.java)
        startActivity(intent)
        finish()
    }

}