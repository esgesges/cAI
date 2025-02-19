package com.example.app

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import org.w3c.dom.Text

class ChatActivity : AppCompatActivity() , View.OnClickListener {

    val txt:String = "sdfsd\n"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)
        val homebtn = findViewById<Button>(R.id.homebtn)
        val optbtn = findViewById<Button>(R.id.optbtn)

        homebtn.setOnClickListener({
            val nextPage = Intent(this, MainActivity::class.java)
            startActivity(nextPage)
        })
        optbtn.setOnClickListener({
            val nextPage = Intent(this, DebugActivity::class.java)
            startActivity(nextPage)
        })
    }

    override fun onClick(v: View?) {

    }
}