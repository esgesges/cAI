package com.example.app

import android.app.ActivityOptions
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import android.content.SharedPreferences
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.ResponseBody
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ChatActivity : AppCompatActivity() {

    private lateinit var homebtn: Button
    private lateinit var optbtn: Button
    private lateinit var notBtn: Button
    private lateinit var sendBtn: Button
    private lateinit var output: TextView
    private lateinit var input: EditText
    private lateinit var sharedPreferences: SharedPreferences
    private val CHAT_PREFS = "ChatPrefs"
    private val CHAT_KEY = "chatMessages"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)

        // Initialize SharedPreferences
        sharedPreferences = getSharedPreferences(CHAT_PREFS, MODE_PRIVATE)

        // Initialize UI elements
        homebtn = findViewById(R.id.homebtn)
        optbtn = findViewById(R.id.optbtn)
        notBtn = findViewById(R.id.notBtn)
        sendBtn = findViewById(R.id.sendBtn)
        output = findViewById(R.id.aiOutput)
        input = findViewById(R.id.aiInput)

        // Display saved chat messages when the activity is created
        displaySavedChat()

        // Home button click
        homebtn.setOnClickListener {
            val nextPage = Intent(this, MainActivity::class.java)
            val options = ActivityOptions.makeCustomAnimation(this, R.anim.slide_out_right, R.anim.slide_in_left)
            startActivity(nextPage, options.toBundle())
        }

        // Options button click
        optbtn.setOnClickListener {
            val nextPage = Intent(this, DebugEventActivity::class.java)
            val options = ActivityOptions.makeCustomAnimation(this, R.anim.slide_out_left, R.anim.slide_in_right)
            startActivity(nextPage, options.toBundle())
        }

        notBtn.setOnClickListener {
            val nextPage = Intent(this, OptActivity::class.java)
            val options = ActivityOptions.makeCustomAnimation(this, R.anim.slide_out_right, R.anim.slide_in_left)
            startActivity(nextPage, options.toBundle())        }
        // Send button click
        sendBtn.setOnClickListener {
            val userInput = input.text.toString().trim()
            if (userInput.isNotEmpty()) {
                val currentChat = loadChat()
                val updatedChat = "$currentChat\n\nUser: $userInput"
                saveChat(updatedChat) // Save chat

                output.append("\n\nUser: $userInput") // Show user input in chat
                apiCall(userInput) // Send request to the API
            }
        }
    }

    // API call to send user input
    private fun apiCall(userQuery: String) {
        val json = "{\"input\": \"$userQuery\"}"
        val requestBody = json.toRequestBody("application/json".toMediaTypeOrNull())

        RetrofitClient.instance.manage(requestBody)?.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.isSuccessful) {
                    val responseText = response.body()?.string() ?: "No response body"
                    try {
                        Log.d("Response", "\n$responseText")
                        // Append the response text to the chat
                        val currentChat = loadChat()
                        val updatedChat = "$currentChat\n\nWorker: $responseText"
                        saveChat(updatedChat) // Save updated chat

                        output.append("\n\nWorker: $responseText") // Show worker response in chat
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
                output.append("\n$errorMsg")
            }
        })
    }

    // Save the chat string in SharedPreferences
    private fun saveChat(chat: String) {
        val editor = sharedPreferences.edit()
        editor.putString(CHAT_KEY, chat)
        editor.apply()
    }

    // Load the chat string from SharedPreferences
    private fun loadChat(): String {
        return sharedPreferences.getString(CHAT_KEY, "") ?: ""
    }

    // Display the saved chat messages on the screen
    private fun displaySavedChat() {
        val savedChat = loadChat()
        output.text = savedChat
    }
}
