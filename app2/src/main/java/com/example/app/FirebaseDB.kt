package com.example.app

import android.util.Log
import android.widget.TextView
import com.example.app.db.Event
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

public class FirebaseDB {
    // Initialize Firebase reference
    private val firebase = FirebaseDatabase.getInstance("https://clndrai-default-rtdb.europe-west1.firebasedatabase.app/")
    private val eventsRef = firebase.getReference("event/")

    private val output: TextView? = null

    fun onCreate(){
        output?.findViewById<TextView>(R.id.aiOutput)
    }

    fun addEvent(title: String, description: String, year: Int, month: Int, day: Int, hour: Int, minute: Int) {
        val id = eventsRef.push().key
        if (id != null) {
            val event = Event(title, description, year, month, day, hour, minute)
            eventsRef.child(id).setValue(event)
        }
    }

    fun modifyEvent(eventId: String, title: String?, description: String?, year: Int, month: Int, day: Int, hour: Int, minute: Int) {
        val eventRef = eventsRef.child(eventId)
        val updates: MutableMap<String, Any> = HashMap()

        if (title != null && !title.isEmpty() && title != "undefined") updates["title"] = title
        if (description != null && !description.isEmpty() && description != "undefined") updates["description"] = description
        if (year != 0) updates["year"] = year
        if (month != 0) updates["month"] = month
        if (day != 0) updates["day"] = day
        if (hour != 0) updates["hour"] = hour
        if (minute != 0) updates["minute"] = minute

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

    fun readAllEvents() {
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

    fun deleteEvent(eventId: String) {
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

}

