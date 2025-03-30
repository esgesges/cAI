package com.example.app

import RetrofitClient
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.CalendarView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.marginStart
import androidx.core.view.marginTop
import com.example.app.db.CaiDatabase
import com.example.app.db.Event
import com.example.app.db.EventDao
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.ResponseBody
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject

class MainActivity : AppCompatActivity(), View.OnClickListener {

    private val coroutineScope = CoroutineScope(Dispatchers.Main)
    private lateinit var database: CaiDatabase
    private lateinit var dao: EventDao
    private lateinit var eventsContainer: LinearLayout
    val firebase = FirebaseDatabase.getInstance("https://clndrai-default-rtdb.europe-west1.firebasedatabase.app/")
    val eventsRef = firebase.getReference("event/")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val chatbtn = findViewById<Button>(R.id.chatbtn)
        val optbtn = findViewById<Button>(R.id.optbtn)
        val calendario = findViewById<CalendarView>(R.id.calendario)
        val eventsContainer = findViewById<LinearLayout>(R.id.eventsContainer)
        database = CaiDatabase.getInstance(this)
        dao = database.dao

        // Clear any events shown on startup
        eventsContainer.removeAllViews()

        calendario.setOnDateChangeListener { _, year, month, dayOfMonth ->
            eventsContainer.removeAllViews() // Clear previous events
            getEventsByDate(year, month + 1, dayOfMonth) // Load events for the selected date
//            getEventsByDate(year, month + 1, dayOfMonth) // Load events for the selected date
        }

        chatbtn.setOnClickListener {
            val nextPage = Intent(this, ChatActivity::class.java)
            startActivity(nextPage)
        }
        optbtn.setOnClickListener {
            val nextPage = Intent(this, OptActivity::class.java)
            startActivity(nextPage)
        }
    }

    private fun getEventsByDate(year: Int, month: Int, day: Int) {
        val formattedDate = String.format("%04d-%02d-%02d", year, month, day) // Ensure correct format YYYY-MM-DD

        RetrofitClient.instance.getEventsByDate(formattedDate)?.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (!response.isSuccessful) {
                    val errorBody = response.errorBody()?.string()
                    Log.e("API Error", "Failed request. Status: ${response.code()} | Response: $errorBody")
                    addEventToView("Error", "Failed to load events\nStatus: ${response.code()}", "")
                    return
                }

                val responseText = response.body()?.string()
                Log.d("API Response", "Received: $responseText")

                if (responseText.isNullOrEmpty()) {
                    addEventToView("No events", "", "")
                    return
                }

                try {
                    val eventsArray = JSONArray(responseText)

                    if (eventsArray.length() == 0) {
                        addEventToView("No events", "", "")
                    } else {
                        for (i in 0 until eventsArray.length()) {
                            val event = eventsArray.getJSONObject(i)
                            val title = event.optString("Titolo", "No Title")
                            val description = event.optString("Descrizione", "No Description")
                            val eventTime = event.optString("Ora", "Unknown Time")
                            addEventToView(title, "$description alle $eventTime", "")
                        }
                    }
                } catch (e: JSONException) {
                    Log.e("Parsing Error", "Failed to parse events: ${e.message}")
                    addEventToView("Error", "Failed to parse events", "")
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                val errorMsg = "Network error: ${t.message}"
                Log.e("Failure", errorMsg)
                addEventToView("Error", errorMsg, "")
            }
        })
    }

    private fun addEventToView(title: String, description: String, date: String) {
        // Create a new TextView for each event
        val eventTextView = TextView(this).apply {
            text = if (title == "No events") {
                "No events"
            } else {
                "Titolo: $title\nDescrizione: $description\nData: $date"
            }
            textSize = 16f
            setPadding(32, 16, 32, 16)
            setBackgroundResource(R.drawable.rounded_textview) // Rounded corners
            setTextColor(Color.BLACK) // Change text color
        }

        // Set margin using LayoutParams
        val layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        ).apply {
            setMargins(20, 20, 20, 20) // Left, Top, Right, Bottom margin
        }

        eventTextView.layoutParams = layoutParams

        // Add the TextView to the container
        val eventsContainer = findViewById<LinearLayout>(R.id.eventsContainer)
        eventsContainer.addView(eventTextView)
    }

    private fun readAllEvents() {
        eventsRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                eventsContainer.removeAllViews() // Clear previous views
                for (eventSnapshot in snapshot.children) {
                    val eventId = eventSnapshot.key
                    val event = eventSnapshot.getValue(Event::class.java)

                    if (event != null) {
                        val date = "${event.year}-${event.month}-${event.day} ${event.hour}:${event.minute}"
                        addEventToView(event.title, event.description, date)
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("Firebase", "Failed to read value.", error.toException())
                addEventToView("Error", "Failed to load events.", "")
            }
        })
    }

    private fun fetchAndDisplayEvents() {
        coroutineScope.launch(Dispatchers.IO) {
            dao.getEvents().collect { eventList ->
                withContext(Dispatchers.Main) {
                    eventsContainer.removeAllViews() // Clear previous views
                    for (event in eventList) {
                        val date = "${event.year}-${event.month}-${event.day} ${event.hour}:${event.minute}"
                        addEventToView(event.title, event.description, date)
                    }
                }
            }
        }
    }

    override fun onClick(v: View?) {
        // Handle button clicks if needed
    }
}
