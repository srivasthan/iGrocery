package com.task.igrocery.Adapter

import android.app.Activity
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.widget.AppCompatButton
import androidx.core.app.ActivityOptionsCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.like.LikeButton
import com.task.igrocery.Model.CartModel
import com.task.igrocery.Model.LikeModel
import com.task.igrocery.Model.Product
import com.task.igrocery.R
import com.task.igrocery.RoomDB.LikeSqlDb
import com.task.igrocery.RoomDB.SqliteDatabase
import com.task.igrocery.ui.ProductViewActivity
import java.util.*


class GroceryAdapter(private var product: List<Product>) :
    RecyclerView.Adapter<GroceryViewHolder>(), Filterable {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GroceryViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.grocery_list, parent, false)
        return GroceryViewHolder(view)
    }

    override fun getItemCount(): Int {
        return product.size
    }

    override fun onBindViewHolder(holder: GroceryViewHolder, position: Int) {
        return holder.bind(product[position])
    }

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(charSequence: CharSequence): FilterResults {
                val charString = charSequence.toString()
                product = if (charString.isEmpty()) {
                    product
                } else {
                    val filteredList = ArrayList<Product>()
                    for (contacts in product) {
                        if (contacts.name.toLowerCase().contains(charString)) {
                            filteredList.add(contacts)
                        }
                    }
                    filteredList
                }
                val filterResults = FilterResults()
                filterResults.values = product
                return filterResults
            }

            override fun publishResults(
                charSequence: CharSequence,
                filterResults: FilterResults
            ) {
                product =
                    filterResults.values as ArrayList<Product>
                notifyDataSetChanged()
            }
        }
    }
}

class GroceryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    lateinit var list: List<String>
    private lateinit var dataBase: SqliteDatabase
    private lateinit var likeDataBase: LikeSqlDb
    var quantity: String = "1"
    var id: Int = 0
    var likeId: Int = 0
    private val likeImage: LikeButton = itemView.findViewById(R.id.isLoved)
    private val itemImage: ImageView = itemView.findViewById(R.id.previewIcon)
    private val addButton: AppCompatButton = itemView.findViewById(R.id.btnPurchase)
    private val minusButton: Button = itemView.findViewById(R.id.btn_minus)
    private val plusButton: Button = itemView.findViewById(R.id.btn_plus)
    private val itemCount: TextView = itemView.findViewById(R.id.quantity)
    private val itemName: TextView = itemView.findViewById(R.id.itm_name)
    private val itemAmount: TextView = itemView.findViewById(R.id.itm_amount)
    private val rtlQuantity: RelativeLayout = itemView.findViewById(R.id.rtl_quantity)

    fun bind(product: Product) {
        dataBase = SqliteDatabase(itemView.context)
        likeDataBase = LikeSqlDb(itemView.context)
        list = ArrayList()

        (list as ArrayList<String>).clear()

        (list as ArrayList<String>).add(product.name)

        if (likeDataBase.listLike().size > 0) {
            for (element in likeDataBase.listLike()) {
                likeImage.isLiked = element.name == product.name
            }
        }

        if (dataBase.listProducts().size > 0) {
            for (element in dataBase.listProducts()) {
                if (element.name.equals(product.name)) {
                    addButton.visibility = View.GONE
                    rtlQuantity.visibility = View.VISIBLE
                    id = element.id
                    quantity = element.quantity
                    itemCount.text = quantity
                }
            }
        } else {
            addButton.visibility = View.VISIBLE
            rtlQuantity.visibility = View.GONE
        }
        Glide.with(itemView.context).load(product.image).into(itemImage)
        itemName.text = product.name

        itemAmount.text = product.special


        addButton.setOnClickListener {
            addButton.visibility = View.GONE
            rtlQuantity.visibility = View.VISIBLE

            dataBase.addItems(
                CartModel(
                    product.image,
                    product.name,
                    product.special,
                    quantity,
                    product.id
                )
            )

        }

        plusButton.setOnClickListener {
            val quantityChanged: Int = itemCount.text.toString().toInt() + 1
            itemCount.text = quantityChanged.toString()
            dataBase.updateProducts(
                CartModel(
                    Objects.requireNonNull<Any>(id) as Int,
                    product.image,
                    product.name,
                    product.special,
                    quantityChanged.toString(),
                    product.id
                )
            )
        }

        minusButton.setOnClickListener {
            val quantityChanged: Int = itemCount.text.toString().toInt() - 1

            if (quantityChanged < 1) {
                addButton.visibility = View.VISIBLE
                rtlQuantity.visibility = View.GONE
            } else {
                itemCount.text = quantityChanged.toString()
                dataBase.updateProducts(
                    CartModel(
                        Objects.requireNonNull<Any>(id) as Int,
                        product.image,
                        product.name,
                        product.special,
                        quantityChanged.toString(),
                        product.id
                    )
                )
            }
        }

        itemImage.setOnClickListener {

            val intent = Intent(itemView.context, ProductViewActivity::class.java)
            intent.putExtra("item_id", product.id)
            intent.putExtra("image", product.image)
            intent.putExtra("name", product.name)
            intent.putExtra("amount", product.special)
            intent.putExtra("description", product.description)

            itemImage.transitionName = "thumbnailTransition"

            val pair1: androidx.core.util.Pair<View, String> =
                androidx.core.util.Pair.create(itemImage as View, itemImage.transitionName)

            val optionsCompat: ActivityOptionsCompat =
                ActivityOptionsCompat.makeSceneTransitionAnimation(
                    itemView.context as Activity,
                    pair1
                )
            itemView.context.startActivity(intent, optionsCompat.toBundle())
        }

        likeImage.setOnClickListener {

            if (likeDataBase.listLike().size > 0) {
                for (element in likeDataBase.listLike()) {
                    if (element.name.equals(product.name)) {
                        likeId = element.id
                        likeImage.isLiked = false
                        likeDataBase.deleteLikeProduct(likeId)
                    } else {
                        likeImage.isLiked = true
                        likeDataBase.addLikeItems(
                            LikeModel(
                                product.image,
                                product.name,
                                product.special,
                                "liked"
                            )
                        )
                    }
                }
            } else {
                likeImage.isLiked = true
                likeDataBase.addLikeItems(
                    LikeModel(
                        product.image,
                        product.name,
                        product.special,
                        "liked"
                    )
                )
            }
        }
    }

}