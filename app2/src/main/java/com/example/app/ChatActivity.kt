package com.example.app

import com.example.app.*
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.json.JSONException
import org.json.JSONObject
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ChatActivity : AppCompatActivity() {

    private lateinit var homebtn: Button
    private lateinit var optbtn: Button
    private lateinit var sendBtn: Button
    private lateinit var output: TextView
    private lateinit var input: EditText
    val firebaseDB = FirebaseDB()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)

        // Initialize the views after setContentView
        homebtn = findViewById(R.id.homebtn)
        optbtn = findViewById(R.id.optbtn)
        sendBtn = findViewById(R.id.sendBtn)
        output = findViewById(R.id.aiOutput)
        input = findViewById(R.id.aiInput)



        homebtn.setOnClickListener {
            val nextPage = Intent(this, MainActivity::class.java)
            startActivity(nextPage)
        }

        optbtn.setOnClickListener {
            val nextPage = Intent(this, DebugActivity::class.java)
            startActivity(nextPage)
        }

        sendBtn.setOnClickListener {
            output.append("\n\n" + input.text.toString())
            apiCall()
        }
    }

    private fun apiCall() {
        val query = input.text.toString()
        val json = "{\"input\": \"$query\"}"
        val requestBody = json.toRequestBody("application/json".toMediaTypeOrNull())

        RetrofitClient.instance.sendQuery(requestBody)?.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.isSuccessful) {
                    val responseText = response.body()?.string() ?: "No response body"
                    try {
                        // Parse the JSON response
                        val jsonObject = JSONObject(responseText)
                        val action = jsonObject.optString("action", "N/A")
                        val id = jsonObject.optString("id", "N/A")
                        val title = jsonObject.optString("title", "N/A")
                        val description = jsonObject.optString("description", "N/A")
                        val year = jsonObject.optInt("year", 0)
                        val month = jsonObject.optInt("month", 0)
                        val day = jsonObject.optInt("day", 0)
                        val hour = jsonObject.optInt("hour", 0)
                        val minutes = jsonObject.optInt("minutes", 0)

                        val result = """
                                        Input: $query
                                        Id: $id
                                        Title: $title
                                        Description: $description
                                        Date: $year-$month-$day
                                        Time: $hour:$minutes
                                    """.trimIndent()

                        Log.d("Response", result + "\n$responseText")
                        output.append("\n" + result + "\n$responseText")
                        when (action) {
                            "addEvent" -> CoroutineScope(Dispatchers.IO).launch {
                                val result = firebaseDB.addEvent(title, description, year, month, day, hour, minutes)
                                runOnUiThread {
                                }
                            }
                            "modifyEvent" -> CoroutineScope(Dispatchers.IO).launch {
                                val result = firebaseDB.modifyEvent(id, title, description, year, month, day, hour, minutes)
                                runOnUiThread {
                                }
                            }
                            "deleteEvent" -> CoroutineScope(Dispatchers.IO).launch {
                                val result = firebaseDB.deleteEvent(id)
                                runOnUiThread {
                                }
                            }
                        }
                    } catch (e: JSONException) {
                        Log.e("Parsing Error", "Failed to parse response: ${e.message}")
                        output.append("\nParsing Error: ${e.message}")
                    }
                } else {
                    val errorText = response.errorBody()?.string().orEmpty()
                    Log.e("Error", "Request failed: $errorText")
                    output.append("\nError: $errorText")
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                val errorMsg = "Network error: ${t.message}"
                Log.e("Failure", errorMsg)
                output.append("\n" + errorMsg)
            }
        })
    }
}
