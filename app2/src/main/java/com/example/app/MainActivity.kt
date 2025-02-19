package com.example.app

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.CalendarView
import android.widget.EditText
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.app.db.CaiDatabase
import com.example.app.db.EventDao
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.w3c.dom.Text

class MainActivity : AppCompatActivity() , View.OnClickListener {

    private val coroutineScope = CoroutineScope(Dispatchers.Main)
    private lateinit var database: CaiDatabase
    private lateinit var dao: EventDao
    private lateinit var output: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val chatbtn = findViewById<Button>(R.id.chatbtn)
        val optbtn = findViewById<Button>(R.id.optbtn)
        val calendario = findViewById<CalendarView>(R.id.calendario)
        output = findViewById(R.id.eventList)
        database = CaiDatabase.getInstance(this)
        dao = database.dao

        fetchAndDisplayEvents()

        calendario.setOnClickListener({
            val data = calendario.date
        })

        chatbtn.setOnClickListener({
            val nextPage = Intent(this, ChatActivity::class.java)
            startActivity(nextPage)
        })
        optbtn.setOnClickListener({
            val nextPage = Intent(this, OptActivity::class.java)
            startActivity(nextPage)
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