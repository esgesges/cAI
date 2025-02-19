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

class OptActivity : AppCompatActivity() , View.OnClickListener {

    val txt:String = "sdfsd\n"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_opt)
        val chatbtn = findViewById<Button>(R.id.chatbtn)
        val homebtn = findViewById<Button>(R.id.homebtn)

        homebtn.setOnClickListener({
            val nextPage = Intent(this, MainActivity::class.java)
            startActivity(nextPage)
        })
        chatbtn.setOnClickListener({
            val nextPage = Intent(this, ChatActivity::class.java)
            startActivity(nextPage)
        })
    }

    override fun onClick(v: View?) {

    }
}