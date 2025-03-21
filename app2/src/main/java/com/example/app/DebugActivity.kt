package com.example.app

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.app.db.Event
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.Main

internal class DebugActivity : AppCompatActivity(), View.OnClickListener {
    private val output: TextView? = null
    private val coroutineScope: CoroutineScope = CoroutineScope(Main)

    // Initialize Firebase reference
    private val firebase =
        FirebaseDatabase.getInstance("https://clndrai-default-rtdb.europe-west1.firebasedatabase.app/")
    private val eventsRef = firebase.getReference("event/")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_debug)

        // Lower menu to switch screens
        findViewById<View>(R.id.homebtn).setOnClickListener { v: View? ->
            startActivity(
                Intent(
                    this,
                    MainActivity::class.java
                )
            )
        }
        findViewById<View>(R.id.chatbtn).setOnClickListener { v: View? ->
            startActivity(
                Intent(
                    this,
                    ChatActivity::class.java
                )
            )
        }
        findViewById<View>(R.id.optbtn).setOnClickListener { v: View? ->
            startActivity(
                Intent(
                    this,
                    OptActivity::class.java
                )
            )
        }

        // Inputs
        val inId = findViewById<EditText>(R.id.idIn)
        val inTitle = findViewById<EditText>(R.id.title)
        val inDesc = findViewById<EditText>(R.id.desc)
        val inDel = findViewById<EditText>(R.id.remTxt)
        val inYear = findViewById<EditText>(R.id.inYear)
        val inMonth = findViewById<EditText>(R.id.inMonth)
        val inDay = findViewById<EditText>(R.id.inDay)
        val inHour = findViewById<EditText>(R.id.inHour)
        val inMin = findViewById<EditText>(R.id.inMin)

        // Buttons
        findViewById<View>(R.id.addbtn).setOnClickListener { v: View? ->
            val eventId = inId.text.toString()
            val title = inTitle.text.toString()
            val description = inDesc.text.toString()
            val year = parseIntOrZero(inYear.text.toString())
            val month = parseIntOrZero(inMonth.text.toString())
            val day = parseIntOrZero(inDay.text.toString())
            val hour = parseIntOrZero(inHour.text.toString())
            val minute = parseIntOrZero(inMin.text.toString())
            addEvent(eventId, title, description, year, month, day, hour, minute)
        }

        findViewById<View>(R.id.reloadbtn).setOnClickListener { v: View? -> readAllEvents() }

        findViewById<View>(R.id.delbtn).setOnClickListener { v: View? ->
            deleteEvent(
                inId.text.toString()
            )
        }

        findViewById<View>(R.id.modBtn).setOnClickListener { v: View? ->
            val eventId = inId.text.toString()
            val title = inTitle.text.toString()
            val description = inDesc.text.toString()
            val year = parseIntOrZero(inYear.text.toString())
            val month = parseIntOrZero(inMonth.text.toString())
            val day = parseIntOrZero(inDay.text.toString())
            val hour = parseIntOrZero(inHour.text.toString())
            val minute = parseIntOrZero(inMin.text.toString())
            modifyEvent(eventId, title, description, year, month, day, hour, minute)
        }
    }

    private fun parseIntOrZero(str: String): Int {
        return try {
            str.toInt()
        } catch (e: NumberFormatException) {
            0
        }
    }

    private fun addEvent(
        id: String?,
        title: String,
        description: String,
        year: Int,
        month: Int,
        day: Int,
        hour: Int,
        minute: Int
    ) {
        var id = id
        if (id == null || id.isEmpty()) {
            id = eventsRef.push().key
        }
        if (id != null) {
            val event = Event(title, description, year, month, day, hour, minute)
            eventsRef.child(id).setValue(event)
        }
    }

    private fun modifyEvent(
        eventId: String,
        title: String?,
        description: String?,
        year: Int,
        month: Int,
        day: Int,
        hour: Int,
        minute: Int
    ) {
        val eventRef = eventsRef.child(eventId)
        val updates: MutableMap<String, Any> = HashMap()

        if (title != null && !title.isEmpty()) updates["title"] = title
        if (description != null && !description.isEmpty()) updates["description"] = description
        updates["year"] = year
        updates["month"] = month
        updates["day"] = day
        updates["hour"] = hour
        updates["minute"] = minute

        eventRef.updateChildren(updates)
            .addOnSuccessListener { aVoid: Void? ->
                Log.d(
                    "Firebase",
                    "Event modified successfully!"
                )
            }
            .addOnFailureListener { e: Exception? ->
                Log.e(
                    "Firebase",
                    "Failed to modify event.",
                    e
                )
            }
    }

    private fun readAllEvents() {
        eventsRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                output!!.text = ""
                for (eventSnapshot in snapshot.children) {
                    val eventId = eventSnapshot.key
                    val event = eventSnapshot.getValue(
                        Event::class.java
                    )
                    if (event != null) {
                        val data =
                            event.year.toString() + "-" + event.month + "-" + event.day + " " + event.hour + ":" + event.minute
                        output.append("""$eventId - ${event.title}    $data""")
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("Firebase", "Failed to read value.", error.toException())
            }
        })
    }

    private fun deleteEvent(eventId: String) {
        eventsRef.child(eventId).removeValue()
            .addOnSuccessListener { aVoid: Void? ->
                Log.d(
                    "Firebase",
                    "Event deleted successfully!"
                )
            }
            .addOnFailureListener { e: Exception? ->
                Log.e(
                    "Firebase",
                    "Failed to delete event.",
                    e
                )
            }
    }

    override fun onClick(v: View) {
        // No action needed
    }
}