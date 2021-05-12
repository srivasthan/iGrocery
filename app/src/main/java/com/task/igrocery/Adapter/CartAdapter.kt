package com.task.igrocery.Adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.task.igrocery.Model.CartModel
import com.task.igrocery.R
import com.task.igrocery.RoomDB.SqliteDatabase
import com.task.igrocery.ui.CartActivity
import java.util.*

internal class CartAdapter(private val context: Context, listContacts: ArrayList<CartModel>) :
    RecyclerView.Adapter<ContactViewHolder>(), Filterable {
    private var listContacts: ArrayList<CartModel>
    private val mArrayList: ArrayList<CartModel>
    private val mDatabase: SqliteDatabase

    init {
        this.listContacts = listContacts
        this.mArrayList = listContacts
        mDatabase = SqliteDatabase(context)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContactViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.cart_items, parent, false)
        return ContactViewHolder(view)
    }

    override fun onBindViewHolder(holder: ContactViewHolder, position: Int) {
        val product = listContacts[position]
        val check = listContacts.size - 1
        if (position == check) {
            holder.plus.visibility = View.INVISIBLE
        }
        if (listContacts.size > 1) {
            holder.plus.visibility = View.VISIBLE
        } else {
            holder.plus.visibility = View.INVISIBLE
        }
        Glide.with(context).load(product.image).into(holder.image)
        holder.tvName.text = product.name
        holder.price.text = product.price
        holder.quantity.text = product.quantity

        var priceAmount: String = product.price.substring(1)
        priceAmount = priceAmount.replace(",", "")
        val productPrice: Int = priceAmount.toInt()

        var productQuantity: Int = product.quantity.toInt()
        var productTotal: Int = productQuantity * productPrice
        holder.totalCount.text = "₹ " + productTotal.toString()

        holder.plusButton.setOnClickListener {

            val quantityChanged: Int = holder.itemCount.text.toString().toInt() + 1
            holder.quantity.text = quantityChanged.toString()

            productQuantity = quantityChanged
            productTotal = productQuantity * productPrice
            holder.totalCount.text = "₹ " + productTotal.toString()

            if (context is CartActivity) {
                (context as CartActivity).productIncrement(
                    productPrice.toString(),
                    productQuantity.toString(),
                    product.quantity
                )
            }

            mDatabase.updateProducts(
                CartModel(
                    Objects.requireNonNull<Any>(product.id) as Int,
                    product.image,
                    product.name,
                    product.price,
                    productQuantity.toString(),
                    productTotal.toString()
                )
            )

        }

        holder.minusButton.setOnClickListener {
            val quantityChanged: Int = holder.itemCount.text.toString().toInt() - 1

            if (quantityChanged < 1) {
                mDatabase.deleteProduct(position)
            } else {
                holder.quantity.text = quantityChanged.toString()
                productQuantity = quantityChanged
                productTotal = productQuantity * productPrice
                holder.totalCount.text = "₹ " + productTotal.toString()

                if (context is CartActivity) {
                    (context as CartActivity).productDecrement(
                        productPrice.toString(),
                        productQuantity.toString(),
                        product.quantity
                    )
                }

                mDatabase.updateProducts(
                    CartModel(
                        Objects.requireNonNull<Any>(product.id) as Int,
                        product.image,
                        product.name,
                        product.price,
                        productQuantity.toString(),
                        productTotal.toString()
                    )
                )

            }
        }

    }

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(charSequence: CharSequence): FilterResults {
                val charString = charSequence.toString()
                listContacts = if (charString.isEmpty()) {
                    mArrayList
                } else {
                    val filteredList = ArrayList<CartModel>()
                    for (contacts in mArrayList) {
                        if (contacts.name.toLowerCase().contains(charString)) {
                            filteredList.add(contacts)
                        }
                    }
                    filteredList
                }
                val filterResults = FilterResults()
                filterResults.values = listContacts
                return filterResults
            }

            override fun publishResults(
                charSequence: CharSequence,
                filterResults: FilterResults
            ) {
                listContacts =
                    filterResults.values as ArrayList<CartModel>
                notifyDataSetChanged()
            }
        }
    }

    override fun getItemCount(): Int {
        return listContacts.size
    }
}

class ContactViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    var image: ImageView = itemView.findViewById(R.id.product_image)
    var tvName: TextView = itemView.findViewById(R.id.product_title)
    var price: TextView = itemView.findViewById(R.id.product_attribute)
    var quantity: TextView = itemView.findViewById(R.id.quantity)
    var minusButton: Button = itemView.findViewById(R.id.btn_minus)
    var plusButton: Button = itemView.findViewById(R.id.btn_plus)
    var itemCount: TextView = itemView.findViewById(R.id.quantity)
    var totalCount: TextView = itemView.findViewById(R.id.txt_total)
    var plus: ImageView = itemView.findViewById(R.id.img_plus)
}