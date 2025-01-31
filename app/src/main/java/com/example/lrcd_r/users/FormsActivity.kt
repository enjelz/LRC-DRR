package com.example.lrcd_r.users

import android.app.Dialog
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.compose.ui.semantics.text
import com.example.lrcd_r.R
import com.example.lrcd_r.User
import com.example.lrcd_r.databinding.ActivityFormsBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class FormsActivity : DrawerBaseActivity() {

    lateinit var activityFormsBinding: ActivityFormsBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var databaseReference: DatabaseReference
    private lateinit var user: User
    private lateinit var uid: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forms) // Provide layout resource ID
        activityFormsBinding = ActivityFormsBinding.inflate(layoutInflater)

        enableEdgeToEdge()

        auth = FirebaseAuth.getInstance()
        uid = auth.currentUser?.uid.toString()

        databaseReference = FirebaseDatabase.getInstance().getReference("Users")
        if (uid.isNotEmpty()) {
            getUserData()
        }
    }

    private fun getUserData() {

        val currentUser = auth.currentUser  // Get the currently logged-in user
        val currentUserEmail = currentUser?.email  // Retrieve email

        if (currentUserEmail.isNullOrEmpty()) {
            Toast.makeText(this@FormsActivity, "User not logged in", Toast.LENGTH_SHORT).show()
            return
        }

        databaseReference.orderByChild("email").equalTo(currentUserEmail)
            .addListenerForSingleValueEvent(object : ValueEventListener  {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    for (userSnapshot in snapshot.children) { // Iterate over results
                        user = userSnapshot.getValue(User::class.java)!!
                        // Find the views once and check for null

                        val txtName = findViewById<TextView>(R.id.txtName)
                        val txtDept = findViewById<TextView>(R.id.dispDept)
                        val txtUserType = findViewById<TextView>(R.id.txt_usertype)
                        val txtID = findViewById<TextView>(R.id.dispID)

                        if (txtName != null) {
                            val lastName = user.lname ?: "N/A"
                            val givenName = user.gname ?: "N/A"
                            val middleName = user.mname ?: "N/A"
                            val formattedName = "$lastName, $givenName $middleName"
                            txtName.text = formattedName
                        }
                        if (txtUserType != null) txtUserType.text = user.userType
                        if (txtDept != null) txtDept.text = user.dept
                        if (txtID != null) txtID.text = user.id
                        break // Exit loop after first match
                    }
                } else {
                    Toast.makeText(this@FormsActivity, "User not found", Toast.LENGTH_SHORT).show()
                }
            }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(this@FormsActivity, "Something went wrong", Toast.LENGTH_SHORT)
                        .show()
                }
        })
    }

    fun btn_forms_submit(view: View){
        val intent = Intent(this, ConfirmationActivity::class.java)
        startActivity(intent)
    }
}