package com.example.myapplication

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.squareup.picasso.Picasso
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.*
import org.json.JSONObject
import java.io.IOException

class NetworkActivity : AppCompatActivity() {

    private lateinit var imageView: ImageView
    private val client = OkHttpClient()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_network)
        Log.d("bugsnagNetwork", "startNetworkActivity")
        imageView = findViewById(R.id.imageView)

        // Create a request
        val request = Request.Builder()
            .url("https://dog.ceo/api/breeds/image/random")
            .build()

        // Asynchronously execute the request
        client.newCall(request).enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    Log.e("DogApi", "Network Failure: ${e.message}")
                }

                override fun onResponse(call: Call, response: Response) {
                    if (response.isSuccessful) {

                        CoroutineScope(Dispatchers.IO).launch {

                            try {
                                val responseBody = response.body?.string()
                                if (responseBody != null) {
                                    val jsonObject = JSONObject(responseBody)
                                    val imageUrl = jsonObject.getString("message")
                                    Log.d("bugsnagNetwork", imageUrl)
                                    Log.d("bugsnagNetwork", "BobHere")

                                    withContext(Dispatchers.Main) {
                                        Picasso.get().load(imageUrl).into(imageView)
                                    }
                                }
                            } catch (e: IOException) {
                                Log.e("DogApi", "Error decoding image: ${e.message}")
                            }
                        }

                    } else {
                        Log.e("DogApi", "Error: ${response.code}")
                    }
                }

            })
    }

    override fun onStart() {
        super.onStart()
        Log.d("bugsnagNetwork", "On start of Network activity")
    }

    override fun onResume() {
        super.onResume()
        Log.d("bugsnagNetwork", "On resume of Network activity")
    }
}