package com.example.app

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.app.db.Event
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.*

class DebugActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var output: TextView
    private val coroutineScope = CoroutineScope(Dispatchers.Main)

    // Initialize Firebase reference
    val firebase = FirebaseDatabase.getInstance("https://clndrai-default-rtdb.europe-west1.firebasedatabase.app/")
    val eventsRef = firebase.getReference("event/")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_debug)

        // Initialize Output TextView
        output = findViewById(R.id.output)

        // Lower menu to switch screens
        findViewById<Button>(R.id.homebtn).setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
        }
        findViewById<Button>(R.id.chatbtn).setOnClickListener {
            startActivity(Intent(this, ChatActivity::class.java))
        }
        findViewById<Button>(R.id.optbtn).setOnClickListener {
            startActivity(Intent(this, OptActivity::class.java))
        }

        // Inputs
        val inTitle = findViewById<EditText>(R.id.title)
        val inDesc = findViewById<EditText>(R.id.desc)
        val inDel = findViewById<EditText>(R.id.remTxt)
        val inYear = findViewById<EditText>(R.id.inYear)
        val inMonth = findViewById<EditText>(R.id.inMonth)
        val inDay = findViewById<EditText>(R.id.inDay)
        val inHour = findViewById<EditText>(R.id.inHour)
        val inMin = findViewById<EditText>(R.id.inMin)

        // Buttons
        findViewById<Button>(R.id.addbtn).setOnClickListener {
            val title = inTitle.text.toString()
            val description = inDesc.text.toString()
            val year = inYear.text.toString().toIntOrNull() ?: 0
            val month = inMonth.text.toString().toIntOrNull() ?: 0
            val day = inDay.text.toString().toIntOrNull() ?: 0
            val hour = inHour.text.toString().toIntOrNull() ?: 0
            val minute = inMin.text.toString().toIntOrNull() ?: 0

            addEvent(title, description, year, month, day, hour, minute)
        }

        findViewById<Button>(R.id.reloadbtn).setOnClickListener({
            readAllEvents()
        })

        findViewById<Button>(R.id.delbtn).setOnClickListener({
            deleteEvent(inDel.text.toString())
        })
        // Fetch data on startup
        fetchAndDisplayEvents()


    }

    private fun addEvent(title: String, description: String, year: Int, month: Int, day: Int, hour: Int, minute: Int) {
        val eventId = eventsRef.push().key // Generate a unique key
        if (eventId != null) {
            val event = mapOf(
                "title" to title,
                "description" to description,
                "year" to year,
                "month" to month,
                "day" to day,
                "hour" to hour,
                "minute" to minute
            )
            eventsRef.child(eventId).setValue(event)
        }
    }

/*
    private fun fetchEventTime() {
        // Corrected reference to eventsRef
        eventsRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val time = snapshot.getValue(String::class.java)
                Log.d("Firebase", "Event 1 Time: $time")
                output.text = time
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("Firebase", "Failed to read value.", error.toException())
            }
        })
    }
*/

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

    private fun deleteEvent(eventId: String) {
        // Reference to the specific event using its unique ID
        val eventRef = eventsRef.child(eventId)

        // Remove the event from the database
        eventRef.removeValue()
            .addOnSuccessListener {
                // Successfully deleted
                Log.d("Firebase", "Event deleted successfully!")
                output.append("Event with ID: $eventId has been deleted.\n")
            }
            .addOnFailureListener {
                // Failed to delete
                Log.e("Firebase", "Failed to delete event.", it)
                output.append("Failed to delete event with ID: $eventId.\n")
            }
    }


    private fun fetchAndDisplayEvents() {
        coroutineScope.launch(Dispatchers.IO) {
            // Assuming you have a Room database (using DAO) to display events
            // The code here is for displaying data fetched from a local database
            // If you want to display Firebase data, you can adapt it.
            // Example of fetching from local database (Room)
            // dao.getEvents().collect { eventList ->
            //    val eventText = eventList.joinToString("\n") {
            //        "${it.year}-${it.month}-${it.day} ${it.hour}:${it.minute} - ${it.title}: ${it.description}"
            //    }
            //    withContext(Dispatchers.Main) {
            //        output.text = eventText
            //    }
            //}
        }
    }

    override fun onClick(v: View?) {
        // No action needed
    }
}
