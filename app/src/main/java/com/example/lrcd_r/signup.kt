package com.example.lrcd_r

import android.content.Intent
import android.os.Bundle
import android.text.SpannableString
import android.text.style.ClickableSpan
import android.text.style.UnderlineSpan
import android.view.View
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.lrcd_r.databinding.ActivitySignupBinding

class signup : AppCompatActivity() {

    lateinit var signupBinding: ActivitySignupBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup) // Provide layout resource ID
        signupBinding = ActivitySignupBinding.inflate(layoutInflater)
        enableEdgeToEdge()

        //go back to login span
        val spanLogin = findViewById<TextView>(R.id.spanLogin)
        val spanToString = spanLogin.text.toString()
        val spannableString = SpannableString(spanToString)

        val clickableSpan = object : ClickableSpan() {
            override fun onClick(widget: View) {
                // Start the LoginActivity
                val intent = Intent(this@signup, Login::class.java)
                startActivity(intent)
            }
        }
        spannableString.setSpan(clickableSpan, 0, spanToString.length, 0)
        spannableString.setSpan(UnderlineSpan(), 0, spanToString.length, 0)

        spanLogin.text = spannableString
        spanLogin.movementMethod = android.text.method.LinkMovementMethod.getInstance()
    }
}