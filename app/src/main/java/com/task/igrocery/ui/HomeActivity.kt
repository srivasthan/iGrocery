package com.task.igrocery.ui

import android.Manifest.permission.RECORD_AUDIO
import android.app.Activity
import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.os.Bundle
import android.speech.RecognizerIntent
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.*
import androidx.annotation.Nullable
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.task.igrocery.Adapter.GroceryAdapter
import com.task.igrocery.Model.GroceryList
import com.task.igrocery.R
import com.task.igrocery.Retrofit.ApiInterface
import com.task.igrocery.Retrofit.ApiService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*


class HomeActivity : AppCompatActivity() {
    private lateinit var search: EditText
    private lateinit var imgMic: ImageView
    private lateinit var groceryAdapter: GroceryAdapter
    private val RECORD_REQUEST_CODE = 101
    private val REQUEST_CODE_SPEECH_INPUT = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        val itemList: RecyclerView = findViewById(R.id.recyclerView)
        val likeList: ImageView = findViewById(R.id.img_like)
        val imgBack: ImageView = findViewById(R.id.img_back)
        val appTittle: TextView = findViewById(R.id.txt_app_name)
        val imgSearch: ImageView = findViewById(R.id.img_search)
        val rtlSearch: RelativeLayout = findViewById(R.id.ly_search)
        search = findViewById(R.id.searchEditText)
        val imgCart: ImageView = findViewById(R.id.img_cart)
        imgMic = findViewById(R.id.ic_mic)

        val progressBar = ProgressDialog(this)
        progressBar.setCancelable(false)
        progressBar.setMessage("Loading ...")
        progressBar.show()


        imgSearch.setOnClickListener {
            imgSearch.visibility = View.GONE
            appTittle.visibility = View.GONE
            val animation = AnimationUtils.loadAnimation(this, R.anim.slide_left_right)
            rtlSearch.visibility = View.VISIBLE
            rtlSearch.startAnimation(animation)
        }

        val cm = this.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetwork: NetworkInfo? = cm.activeNetworkInfo
        val isConnected: Boolean = activeNetwork?.isConnectedOrConnecting == true

        if (isConnected) {
            val request = ApiService.buildService(ApiInterface::class.java)
            val call = request.getGrocery()

            call.enqueue(object : Callback<GroceryList> {
                override fun onResponse(call: Call<GroceryList>, response: Response<GroceryList>) {
                    if (response.isSuccessful) {
                        itemList.apply {
                            setHasFixedSize(true)
                            itemList.layoutManager = GridLayoutManager(context, 2)
                            groceryAdapter =
                                GroceryAdapter(response.body()!!.products)
                            itemList.adapter = groceryAdapter
                            progressBar.dismiss()
                        }
                    }
                }

                override fun onFailure(call: Call<GroceryList>, t: Throwable) {
                    Toast.makeText(this@HomeActivity, "${t.message}", Toast.LENGTH_SHORT).show()
                }
            })
        } else {
            progressBar.dismiss()
            Toast.makeText(this, "Network connection is not available", Toast.LENGTH_SHORT).show()
        }

        likeList.setOnClickListener { view ->
            val intent = Intent(view.context, LikeViewActivity::class.java)
            startActivity(intent)
        }

        imgCart.setOnClickListener {
            val intent = Intent(this, CartActivity::class.java)
            startActivity(intent)
            finish()
        }

        search.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val query: String = s.toString().trim()
                groceryAdapter.filter.filter(query)
            }
        })

        imgBack.setOnClickListener {
            rtlSearch.visibility = View.GONE
            val animation = AnimationUtils.loadAnimation(this, R.anim.slide_right_left)
            appTittle.visibility = View.VISIBLE
            imgSearch.visibility = View.VISIBLE
            rtlSearch.startAnimation(animation)
        }

        imgMic.setOnClickListener {

            val permission = ContextCompat.checkSelfPermission(this, RECORD_AUDIO)

            if (permission != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(RECORD_AUDIO), RECORD_REQUEST_CODE
                )
            } else {
                val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
                intent.putExtra(
                    RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                    RecognizerIntent.LANGUAGE_MODEL_FREE_FORM
                )
                intent.putExtra(
                    RecognizerIntent.EXTRA_LANGUAGE,
                    Locale.getDefault()
                )
                intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Speak to text")

                try {
                    startActivityForResult(intent, REQUEST_CODE_SPEECH_INPUT)
                } catch (e: Exception) {
                    Toast
                        .makeText(
                            this@HomeActivity, " " + e.message,
                            Toast.LENGTH_SHORT
                        )
                        .show()
                }
            }
        }
    }

    override fun onActivityResult(
        requestCode: Int, resultCode: Int,
        @Nullable data: Intent?
    ) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE_SPEECH_INPUT) {
            if (resultCode == Activity.RESULT_OK && data != null) {
                val result = data.getStringArrayListExtra(
                    RecognizerIntent.EXTRA_RESULTS
                )
                search.setText(Objects.requireNonNull(result)?.get(0))
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            RECORD_REQUEST_CODE -> {
                if (grantResults.isNotEmpty() && grantResults[0] ==
                    PackageManager.PERMISSION_GRANTED
                ) {
                    if ((ContextCompat.checkSelfPermission(
                            this,
                            RECORD_AUDIO
                        ) == PackageManager.PERMISSION_GRANTED)
                    ) {
                        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
                        intent.putExtra(
                            RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                            RecognizerIntent.LANGUAGE_MODEL_FREE_FORM
                        )
                        intent.putExtra(
                            RecognizerIntent.EXTRA_LANGUAGE,
                            Locale.getDefault()
                        )
                        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Speak to text")

                        try {
                            startActivityForResult(intent, REQUEST_CODE_SPEECH_INPUT)
                        } catch (e: Exception) {
                            Toast
                                .makeText(
                                    this@HomeActivity, " " + e.message,
                                    Toast.LENGTH_SHORT
                                )
                                .show()
                        }
                    }
                } else {
                    Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show()
                }
                return
            }
        }
    }

    override fun onBackPressed() {
        val builder: AlertDialog.Builder = AlertDialog.Builder(this@HomeActivity)
        builder.setTitle(R.string.app_name)
        builder.setIcon(R.mipmap.ic_launcher)
        builder.setMessage("Do you want to exit?")
            .setCancelable(false)
            .setPositiveButton(
                "Yes"
            ) { _, id -> finishAffinity() }
            .setNegativeButton(
                "No"
            ) { dialog, _ -> dialog.cancel() }
        val alert: AlertDialog = builder.create()
        alert.show()
    }

}