package com.example.lrcd_r

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.lrcd_r.databinding.ActivityLoginBinding
import com.example.lrcd_r.users.Reserve

class Login : AppCompatActivity() {

    lateinit var loginBinding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login) // Provide layout resource ID
        loginBinding = ActivityLoginBinding.inflate(layoutInflater)
        enableEdgeToEdge()
    }
    fun login(view: View) {
        val intent = Intent(this, Reserve::class.java)
        startActivity(intent)
    }
}