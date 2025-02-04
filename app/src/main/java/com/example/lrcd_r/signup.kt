package com.example.lrcd_r

import android.content.Intent
import android.os.Bundle
import android.text.SpannableString
import android.text.style.ClickableSpan
import android.text.style.UnderlineSpan
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.lrcd_r.databinding.ActivitySignupBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference

class signup : AppCompatActivity() {

    lateinit var signupBinding: ActivitySignupBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var databaseReferences: DatabaseReference
    private lateinit var storageReference: StorageReference

    lateinit var txtGname: EditText
    lateinit var txtLname: EditText
    lateinit var txtMname: EditText
    lateinit var txtDept: EditText
    lateinit var txtID: EditText
    lateinit var txtEmail: EditText
    lateinit var txtPass: EditText
    private var selectedUserType: String = "" // Variable to store the selected user type

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup) // Provide layout resource ID
        signupBinding = ActivitySignupBinding.inflate(layoutInflater)
        enableEdgeToEdge()

        //firebase
        firebaseAuth = FirebaseAuth.getInstance()
        databaseReferences = FirebaseDatabase.getInstance().getReference("Users")

        txtGname = findViewById(R.id.txtGname)
        txtLname = findViewById(R.id.txtLname)
        txtMname = findViewById(R.id.txtMname)
        txtDept = findViewById(R.id.txtDept)
        txtID = findViewById(R.id.txtID)
        txtEmail = findViewById(R.id.txtEmail)
        txtPass = findViewById(R.id.txtPass)

        // Get the spinner
        val userTypeSpinner: Spinner = findViewById(R.id.userTypeSpinner)

        // Create the list of user types, including the hint
        val userTypes = resources.getStringArray(R.array.user_types).toMutableList()
        userTypes.add(0, "Select User Type ⌵") // Add the hint at the beginning

        // Create an ArrayAdapter using the modified list and a default spinner layout
        val adapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_item,
            userTypes
        )

        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        // Apply the adapter to the spinner
        userTypeSpinner.adapter = adapter

        // Set the hint as the initially selected item
        userTypeSpinner.setSelection(0, false) // Set the hint as selected, but don't trigger the listener

        // Disable the hint item
        // Set listener to capture selected user type
        userTypeSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                if (position == 0) {
                    // Ignore the first item (hint)
                    selectedUserType = ""
                } else {
                    // Store the selected user type
                    selectedUserType = parent.getItemAtPosition(position).toString()
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                selectedUserType = "" // Default to empty if nothing is selected
            }
        }

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
        val uid = firebaseAuth.currentUser?.uid

        val gname = txtGname.text.toString()
        val lname = txtLname.text.toString()
        val mname = txtMname.text.toString()
        val dept = txtDept.text.toString()
        val ID = txtID.text.toString()
        val email = txtEmail.text.toString()
        val password = txtPass.text.toString()

        if (gname.isNotEmpty() && 
            lname.isNotEmpty() &&
            selectedUserType.isNotEmpty()&&
            dept.isNotEmpty() &&
            ID.isNotEmpty() &&
            email.isNotEmpty() && 
            password.isNotEmpty()) {
            firebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        Toast.makeText(this, "Sign Up Successful", Toast.LENGTH_SHORT)
                            .show()

                        val user = User(gname, lname, mname, selectedUserType, dept, ID, email,password)
                        if (uid != null) {
                            databaseReferences.child(uid).setValue(user).addOnCompleteListener{
                                if (it.isSuccessful){

                                    storageReference = FirebaseStorage.getInstance().getReference("Users/"+firebaseAuth.currentUser?.uid)

                                    val intent = Intent(this, Login::class.java)
                                    startActivity(intent)
                                    finish()

                                } else {
                                    Toast.makeText(this, "Data Sign up Unsuccessful: ${it.exception?.message}", Toast.LENGTH_SHORT).show()
                                }
                            }
                        }
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
        