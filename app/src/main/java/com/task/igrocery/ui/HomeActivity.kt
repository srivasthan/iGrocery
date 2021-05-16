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
import android.text.TextUtils
import android.text.TextWatcher
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.Nullable
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.mancj.materialsearchbar.MaterialSearchBar
import com.task.igrocery.Adapter.GroceryAdapter
import com.task.igrocery.Model.GroceryList
import com.task.igrocery.R
import com.task.igrocery.Retrofit.ApiInterface
import com.task.igrocery.Retrofit.ApiService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*

class HomeActivity : AppCompatActivity(), MaterialSearchBar.OnSearchActionListener {
    private lateinit var searchView: MaterialSearchBar
    private lateinit var groceryAdapter: GroceryAdapter
    private lateinit var imgMic: ImageView
    private lateinit var imgCart: ImageView
    private val RECORD_REQUEST_CODE = 101
    private val REQUEST_CODE_SPEECH_INPUT = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        val itemList: RecyclerView = findViewById(R.id.recyclerView)
        val likeList: ImageView = findViewById(R.id.img_like)
        searchView = findViewById(R.id.img_search)
        searchView.setOnSearchActionListener(this)
        imgMic = findViewById(R.id.img_mic)
        imgCart = findViewById(R.id.img_cart)

        val progressBar = ProgressDialog(this)
        progressBar.setCancelable(false)
        progressBar.setMessage("Loading ...")
        progressBar.show()

        searchView.setOnClickListener {
            searchView.openSearch()
        }

        searchView.addTextChangeListener(object : TextWatcher {
            override fun beforeTextChanged(
                charSequence: CharSequence?,
                i: Int,
                i1: Int,
                i2: Int
            ) {
            }

            override fun onTextChanged(
                charSequence: CharSequence?,
                i: Int,
                i1: Int,
                i2: Int
            ) {

            }

            override fun afterTextChanged(editable: Editable?) {
                val query: String = editable.toString().trim()
                groceryAdapter.filter.filter(query)
            }
        })

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

        imgMic.setOnClickListener {
            searchView.openSearch()
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

        imgCart.setOnClickListener {
            val intent = Intent(this, CartActivity::class.java)
            startActivity(intent)
            finish()
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

    override fun onActivityResult(
        requestCode: Int, resultCode: Int,
        @Nullable data: Intent?
    ) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE_SPEECH_INPUT) {
            if (resultCode == Activity.RESULT_OK && data != null) {
                val matches =
                    data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
                if (matches != null && matches.size > 0) {
                    val searchWrd = matches[0]
                    if (!TextUtils.isEmpty(searchWrd)) {
                        searchView.text = searchWrd
                    }
                }
            }
        }
    }


    override fun onSearchStateChanged(enabled: Boolean) {
        val s = if (enabled) "enabled" else "disabled"
        // Toast.makeText(this@HomeActivity, "Search $s", Toast.LENGTH_SHORT).show()
    }

    override fun onSearchConfirmed(text: CharSequence) {
        startSearch(text.toString(), true, null, true)
    }

    override fun onButtonClicked(buttonCode: Int) {
        when (buttonCode) {
            MaterialSearchBar.BUTTON_BACK -> {
                searchView.closeSearch()
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