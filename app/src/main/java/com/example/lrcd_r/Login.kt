package com.example.lrcd_r

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.SpannableString
import android.text.style.ClickableSpan
import android.text.style.UnderlineSpan
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.lrcd_r.admin.AdminHomepage
import com.example.lrcd_r.databinding.ActivityLoginBinding
import com.example.lrcd_r.users.ReservationDetailsActivity
import com.example.lrcd_r.users.Reserve
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class Login : AppCompatActivity() {

    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var loginBinding: ActivityLoginBinding
    private lateinit var txtEmail: EditText
    private lateinit var txtPass: EditText
    private lateinit var togglePassword: ImageView
    private var isPasswordVisible = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        loginBinding = ActivityLoginBinding.inflate(layoutInflater)
        enableEdgeToEdge()

        firebaseAuth = FirebaseAuth.getInstance()

        // Initialize views
        txtEmail = findViewById(R.id.txtEmail)
        txtPass = findViewById(R.id.txtPass)
        togglePassword = findViewById(R.id.togglePassword)

        // Setup password visibility toggle
        togglePassword.setOnClickListener {
            isPasswordVisible = !isPasswordVisible
            togglePasswordVisibility()
        }

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

    private fun togglePasswordVisibility() {
        if (isPasswordVisible) {
            // Show password
            txtPass.transformationMethod = null
            togglePassword.setImageResource(R.drawable.ic_eye)
        } else {
            // Hide password
            txtPass.transformationMethod = android.text.method.PasswordTransformationMethod.getInstance()
            togglePassword.setImageResource(R.drawable.ic_eye_off)
        }
        // Maintain cursor position
        txtPass.setSelection(txtPass.text.length)
    }

    //this is for login button
    fun login(view: View) {
        val sharedPreferences = getSharedPreferences("visibility_prefs", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.clear()
        editor.apply()

        val emailFirebase = txtEmail.text.toString().trim()
        val password = txtPass.text.toString().trim()

        if (emailFirebase.isNotEmpty() && password.isNotEmpty()) {
            firebaseAuth.signInWithEmailAndPassword(emailFirebase, password)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        if (emailFirebase == "admin@gmail.com") {
                            Toast.makeText(this, "Welcome, Admin!", Toast.LENGTH_SHORT).show()
                            startActivity(Intent(this, AdminHomepage::class.java))
                            finish()
                        } else {
                            // Retrieve the custom user ID
                            val databaseReference = FirebaseDatabase.getInstance().getReference("Users")
                            databaseReference.addListenerForSingleValueEvent(object :
                                ValueEventListener {
                                override fun onDataChange(snapshot: DataSnapshot) {
                                    var foundUserId: String? = null

                                    for (child in snapshot.children) { // Iterate over "user00001", "user00002", etc.
                                        val userEmail = child.child("email").value?.toString()
                                        if (userEmail == emailFirebase) {
                                            foundUserId = child.key.toString() // "user00001", etc.
                                            break
                                        }
                                    }

                                    if (foundUserId != null) {
                                        // Store userID in SharedPreferences for future use
                                        val userPrefs = getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
                                        userPrefs.edit().putString("USER_ID", foundUserId).apply()

                                        Toast.makeText(this@Login, "Login Successful!", Toast.LENGTH_SHORT).show()

                                        val intent = Intent(this@Login, Reserve::class.java)
                                        intent.putExtra("USER_ID", foundUserId) // Pass userID if needed
                                        startActivity(intent)
                                        finish()
                                    } else {
                                        Toast.makeText(this@Login, "User ID not found!", Toast.LENGTH_SHORT).show()
                                    }
                                }

                                override fun onCancelled(error: DatabaseError) {
                                    Toast.makeText(this@Login, "Database error: ${error.message}", Toast.LENGTH_SHORT).show()
                                }
                            })
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