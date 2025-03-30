package com.example.app

import android.annotation.SuppressLint
import android.app.ActivityOptions
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
        val optBtn = findViewById<Button>(R.id.optbtn)

        homebtn.setOnClickListener({
            val nextPage = Intent(this, MainActivity::class.java)
            val options = ActivityOptions.makeCustomAnimation(this, R.anim.slide_out_right, R.anim.slide_in_left)
            startActivity(nextPage, options.toBundle())
        })
        chatbtn.setOnClickListener({
            val nextPage = Intent(this, ChatActivity::class.java)
            val options = ActivityOptions.makeCustomAnimation(this, R.anim.slide_out_left, R.anim.slide_in_right)
            startActivity(nextPage, options.toBundle())
        })
        optBtn.setOnClickListener({
            val nextPage = Intent(this, DebugEventActivity::class.java)
            val options = ActivityOptions.makeCustomAnimation(this, R.anim.slide_out_left, R.anim.slide_in_right)
            startActivity(nextPage, options.toBundle())

        })

    }

    override fun onClick(v: View?) {

    }
}