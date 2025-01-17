package com.example.lrcd_r

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
import com.example.lrcd_r.databinding.ActivitySignupBinding
import com.google.firebase.auth.FirebaseAuth

class signup : AppCompatActivity() {

    lateinit var signupBinding: ActivitySignupBinding
    private lateinit var firebaseAuth: FirebaseAuth
    lateinit var txtEmail: EditText
    lateinit var txtPass: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup) // Provide layout resource ID
        signupBinding = ActivitySignupBinding.inflate(layoutInflater)
        enableEdgeToEdge()

        firebaseAuth = FirebaseAuth.getInstance()

        txtEmail = findViewById(R.id.txtEmail)
        txtPass = findViewById(R.id.txtPass)

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
    fun signup(view: View) {
        val email = txtEmail.text.toString()
        val password = txtPass.text.toString()

        if (email.isNotEmpty() && password.isNotEmpty()) {
            firebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        Toast.makeText(this, "Sign Up Successful", Toast.LENGTH_SHORT)
                            .show()
                        val intent = Intent(this, Login::class.java)
                        startActivity(intent)
                        finish()
                    } else {
                        Toast.makeText(
                            this,
                            "Sign Up Unsuccessful: ${task.exception?.message}",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
        } else {
            Toast.makeText(this, "Fields cannot be empty", Toast.LENGTH_SHORT).show()
        }
    }
}
        