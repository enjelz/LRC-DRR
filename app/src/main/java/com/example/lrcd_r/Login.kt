package com.example.lrcd_r

import android.content.Intent
import android.os.Bundle
import android.text.SpannableString
import android.text.style.UnderlineSpan
import android.view.View
import android.widget.EditText
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.lrcd_r.admin.AdminHomepage
import com.example.lrcd_r.databinding.ActivityLoginBinding
import com.example.lrcd_r.users.Reserve

class Login : AppCompatActivity() {

    lateinit var loginBinding: ActivityLoginBinding
    lateinit var txtEmail: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login) // Provide layout resource ID
        loginBinding = ActivityLoginBinding.inflate(layoutInflater)
        enableEdgeToEdge()

        val forgotPasswordTextView = findViewById<TextView>(R.id.forgotPass)
        val forgotPasswordText = forgotPasswordTextView.text.toString()
        val spannableString = SpannableString(forgotPasswordText)
        spannableString.setSpan(UnderlineSpan(), 0, forgotPasswordText.length, 0)
        forgotPasswordTextView.text = spannableString

        txtEmail = findViewById(R.id.txtEmail)
    }
    fun login(view: View) {

        val email = txtEmail.text.toString()

        if (email == "admin") {
            val intent = Intent(this, AdminHomepage::class.java)
            startActivity(intent)
        } else {
            val intent = Intent(this, Reserve::class.java)
            startActivity(intent)
        }
    }
}