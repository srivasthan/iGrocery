package com.task.igrocery.Adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.task.igrocery.Model.CartModel
import com.task.igrocery.Model.LikeModel
import com.task.igrocery.R
import com.task.igrocery.RoomDB.SqliteDatabase
import com.task.igrocery.ui.CartActivity
import java.util.*

internal class LikeAdapter(
    private val context: Context,
    private var likeList: ArrayList<LikeModel>
) :
    RecyclerView.Adapter<LiketViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LiketViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.favourites_item, parent, false)
        return LiketViewHolder(view)
    }

    override fun onBindViewHolder(holder: LiketViewHolder, position: Int) {
        val product = likeList[position]

        Glide.with(context).load(product.image).into(holder.image)
        holder.tvName.text = product.name
        holder.price.text = product.price

    }

    override fun getItemCount(): Int {
        return likeList.size
    }
}

class LiketViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    var image: ImageView = itemView.findViewById(R.id.product_image)
    var tvName: TextView = itemView.findViewById(R.id.product_title)
    var price: TextView = itemView.findViewById(R.id.product_attribute)
}