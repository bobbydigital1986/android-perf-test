package com.example.myapplication

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.Button
import android.widget.TextView
import com.example.myapplication.BugsnagVars.interactive_start_span
import com.squareup.picasso.Picasso
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.Call
import okhttp3.Callback
import okhttp3.Request
import okhttp3.Response
import org.json.JSONObject
import java.io.IOException

class CounterActivity : AppCompatActivity() {

    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var editor: SharedPreferences.Editor
    private lateinit var countTextView: TextView
    private var count = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_counter)
        Log.i("BugsnagPerf","Warmstart")

        sharedPreferences = getSharedPreferences("myPrefs", Context.MODE_PRIVATE)
        editor = sharedPreferences.edit()

        countTextView = findViewById(R.id.count_text_view)
        val incrementButton = findViewById<Button>(R.id.increment_button)

        // Retrieve the saved count from SharedPreferences
        val savedCount = sharedPreferences.getInt("count", 0)
        count = savedCount
        countTextView.text = count.toString()

        incrementButton.setOnClickListener {
            count++
            countTextView.text = count.toString()
            // Save the updated count to SharedPreferences
            editor.putInt("count", count)
            editor.apply()
        }



        val main_activity_button = findViewById<Button>(R.id.main_activity_button)
        main_activity_button.setOnClickListener{
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }


        val create_anr_button = findViewById<Button>(R.id.anr_error_button)
        create_anr_button.setOnClickListener{
            Handler(Looper.getMainLooper()).post {
                try {
                    Thread.sleep(10000) // Sleep for 10 seconds
                } catch (e: InterruptedException) {
                    e.printStackTrace()
                }
            }
        }

        val make_network_call_btn = findViewById<Button>(R.id.make_network_call_button)
        make_network_call_btn.setOnClickListener{
            val intent = Intent(this, NetworkActivity1::class.java)
            startActivity(intent)
        }

        val httpClient = (application as AFMobileApplication).httpClient


        // Create a request
        httpClient.newCall(
            Request.Builder()
                .url("https://dog.ceo/api/breeds/image/random")
                .build()
        ).enqueue(object : Callback {
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
                                Log.d("bugsnagNetwork", "Request in counter!")

                                withContext(Dispatchers.Main) {
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
        Log.i("BugsnagPerf","Hotstart")
    }

    override fun onResume() {
        super.onResume()
        Log.i("BugsnagPerf","Activity Resumed")
//        interactive_start_span.end()
    }
}