package com.example.app

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import kotlinx.coroutines.*
import com.example.app.R
import com.example.app.db.*

class DebugActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var database: CaiDatabase
    private lateinit var dao: EventDao
    private lateinit var output: TextView
    private val coroutineScope = CoroutineScope(Dispatchers.Main)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_debug)

        // Initialize Room database
        database = CaiDatabase.getInstance(this)
        dao = database.dao

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
        val inYear = findViewById<EditText>(R.id.inYear)
        val inMonth = findViewById<EditText>(R.id.inMonth)
        val inDay = findViewById<EditText>(R.id.inDay)
        val inHour = findViewById<EditText>(R.id.inHour)
        val inMin = findViewById<EditText>(R.id.inMin)

        // Output TextView
        output = findViewById(R.id.output)


        // Buttons
        findViewById<Button>(R.id.addbtn).setOnClickListener {
            val title = inTitle.text.toString()
            val description = inDesc.text.toString()
            val year = inYear.text.toString().toIntOrNull() ?: 0
            val month = inMonth.text.toString().toIntOrNull() ?: 0
            val day = inDay.text.toString().toIntOrNull() ?: 0
            val hour = inHour.text.toString().toIntOrNull() ?: 0
            val minute = inMin.text.toString().toIntOrNull() ?: 0

            val event = Events(
                year = year,
                month = month,
                day = day,
                hour = hour,
                minute = minute,
                title = title,
                description = description
            )

            // Insert into database
            coroutineScope.launch(Dispatchers.IO) {
                dao.upsertEvent(event)
                fetchAndDisplayEvents() // Fetch and display after inserting
            }
        }

        // Fetch data on startup
        fetchAndDisplayEvents()
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
        // No action needed
    }
}