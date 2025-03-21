package com.example.app

import RetrofitClient
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.CalendarView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
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


class MainActivity : AppCompatActivity() , View.OnClickListener {

    private val coroutineScope = CoroutineScope(Dispatchers.Main)
    private lateinit var database: CaiDatabase
    private lateinit var dao: EventDao
    private lateinit var output: TextView
    val firebase = FirebaseDatabase.getInstance("https://clndrai-default-rtdb.europe-west1.firebasedatabase.app/")
    val eventsRef = firebase.getReference("event/")


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val chatbtn = findViewById<Button>(R.id.chatbtn)
        val optbtn = findViewById<Button>(R.id.optbtn)
        val calendario = findViewById<CalendarView>(R.id.calendario)
        output = findViewById(R.id.eventList)
        database = CaiDatabase.getInstance(this)
        dao = database.dao

//        getEventsByDate(data.year, calendario.month + 1, calendario.dayOfMonth)
        calendario.setOnDateChangeListener { _, year, month, dayOfMonth ->
            getEventsByDate(year, month + 1, dayOfMonth) // CalendarView month starts from 0
        }

        chatbtn.setOnClickListener({
            val nextPage = Intent(this, ChatActivity::class.java)
            startActivity(nextPage)
        })
        optbtn.setOnClickListener({
            val nextPage = Intent(this, OptActivity::class.java)
            startActivity(nextPage)
        })

    }

    private fun readAllEvents(){
        eventsRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                // Iterate through all events under "main/events"
                output.text = ""
                for (eventSnapshot in snapshot.children) {
                    // Get the event ID (unique key) and the event data
                    val eventId = eventSnapshot.key // Unique ID for each event
                    val event = eventSnapshot.getValue(Event::class.java) // Get the Event object

                    if (event != null) {
                        // Log the event's details
                        val data = "${event.year}-${event.month}-${event.day} ${event.hour}:${event.minute}"
                        output.append("\n${eventId} - ${event.title}                                                                    ${data}")
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("Firebase", "Failed to read value.", error.toException())
            }
        })
    }

    private fun getEventsByDate(year: Int, month: Int, day: Int) {
        eventsRef.orderByChild("year") // Start by ordering by "year"
            .equalTo(year.toDouble()) // Firebase stores numbers as Double by default
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    output.text = "" // Clear previous output

                    for (eventSnapshot in snapshot.children) {
                        val eventMonth = eventSnapshot.child("month").getValue(Int::class.java) ?: 0
                        val eventDay = eventSnapshot.child("day").getValue(Int::class.java) ?: 0

                        if (eventMonth == month && eventDay == day) {
                            val title = eventSnapshot.child("title").getValue(String::class.java) ?: "No Title"
                            val description = eventSnapshot.child("description").getValue(String::class.java) ?: "No Description"

                            output.append("Title: $title\n")
                            output.append("Description: $description\n")
                            output.append("Date: $year-$month-$day\n\n")
                        }
                    }

                    if (output.text.isEmpty()) {
                        output.text = "No events found for this date."
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.e("Firebase", "Failed to retrieve events", error.toException())
                    output.text = "Failed to load events."
                }
            })
    }

    private fun fetchAndDisplayEvents() {
        coroutineScope.launch(Dispatchers.IO) {
            dao.getEvents().collect { eventList ->
                val eventText = eventList.joinToString("\n") {
                    "${it.year}-${it.month}-${it.day} ${it.hour}:${it.minute} - ${it.title}: ${it.description}"
                }
                withContext(Dispatchers.Main) {
                    output.text = eventText
                }
            }
        }
    }

    override fun onClick(v: View?) {

    }
}