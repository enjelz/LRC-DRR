package com.example.lrcd_r

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.SpannableString
import android.text.style.ClickableSpan
import android.text.style.UnderlineSpan
import android.view.View
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.lrcd_r.admin.AdminHomepage
import com.example.lrcd_r.databinding.ActivityLoginBinding
import com.example.lrcd_r.users.ReservationDetailsActivity
import com.example.lrcd_r.users.Reserve
import com.google.firebase.auth.FirebaseAuth

class Login : AppCompatActivity() {


    private lateinit var firebaseAuth: FirebaseAuth
    lateinit var loginBinding: ActivityLoginBinding
    lateinit var txtEmail: EditText
    lateinit var txtPass: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login) // Provide layout resource ID
        loginBinding = ActivityLoginBinding.inflate(layoutInflater)
        enableEdgeToEdge()


        firebaseAuth = FirebaseAuth.getInstance()


        //getting txt from email input field
        txtEmail = findViewById(R.id.txtEmail)
        txtPass = findViewById(R.id.txtPass)

        //signup span "Don't have an account?"
        val spanSignup = findViewById<TextView>(R.id.spanSignup)
        val spanToString = spanSignup.text.toString()
        val spannableStringSignup = SpannableString(spanToString)

        val clickableSpan = object : ClickableSpan() {
            override fun onClick(widget: View) {
                // Start the LoginActivity
                val intent = Intent(this@Login, signup::class.java)
                startActivity(intent)
            }
        }
        spannableStringSignup.setSpan(clickableSpan, 0, spanToString.length, 0)
        spannableStringSignup.setSpan(UnderlineSpan(), 0, spanToString.length, 0)

        spanSignup.text = spannableStringSignup
        spanSignup.movementMethod = android.text.method.LinkMovementMethod.getInstance()
    }

    //this is for login button
    fun login(view: View) {
//        val email = txtEmail.text.toString()
//
        // Clear SharedPreferences here
        val sharedPreferences = getSharedPreferences("visibility_prefs", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.clear()
        editor.apply()
//
//        if (email == "admin") {
//            val intent = Intent(this, AdminHomepage::class.java)
//            startActivity(intent)
//        } else {
//            val intent = Intent(this, Reserve::class.java)
//            startActivity(intent)
//        }

        //firebase
        val emailFirebase = txtEmail.text.toString()
        val password = txtPass.text.toString()

        if (emailFirebase.isNotEmpty() && password.isNotEmpty()) {
            firebaseAuth.signInWithEmailAndPassword(emailFirebase, password)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        if(emailFirebase == "admin@gmail.com"){
                            Toast.makeText(this, "Welcome, Admin!", Toast.LENGTH_SHORT).show()
                            val intent = Intent(this, AdminHomepage::class.java)
                            startActivity(intent)
                            finish()
                        } else {
                            Toast.makeText(this, "Login Successful!", Toast.LENGTH_SHORT).show()
                            val intent = Intent(this, Reserve::class.java)
                            startActivity(intent)
                            finish()
                        }
                    } else {
                        Toast.makeText(this, "Login Failed", Toast.LENGTH_SHORT).show()
                    }
                }
        } else {
            Toast.makeText(this, "Fields cannot be empty", Toast.LENGTH_SHORT).show()
        }

    }
}