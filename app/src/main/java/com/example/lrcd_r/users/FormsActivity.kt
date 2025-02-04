package com.example.lrcd_r.users

import android.app.Dialog
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.compose.ui.semantics.text
import com.example.lrcd_r.R
import com.example.lrcd_r.User
import com.example.lrcd_r.databinding.ActivityFormsBinding
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class FormsActivity : DrawerBaseActivity() {

    private lateinit var activityFormsBinding: ActivityFormsBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var databaseReference: DatabaseReference
    private lateinit var user: User
    private lateinit var uid: String

    lateinit var cNum: EditText
    lateinit var tblCount: EditText
    lateinit var chrCount: EditText
    lateinit var purp: EditText
    lateinit var mats: EditText

    private lateinit var receivedDate: String
    private lateinit var receivedStartTime: String
    private lateinit var receivedEndTime: String
    private lateinit var receivedRooms: String


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

        cNum = findViewById<TextInputLayout>(R.id.inputcontact).editText!!
        tblCount = findViewById<TextInputLayout>(R.id.inputTables).editText!!
        chrCount = findViewById<TextInputLayout>(R.id.inputChairs).editText!!
        purp = findViewById<TextInputLayout>(R.id.inputPurpose).editText!!
        mats = findViewById<TextInputLayout>(R.id.inputMaterials).editText!!

        // Retrieve data from Intent
        receivedDate = intent.getStringExtra("DATE") ?: "No Date Selected"
        receivedStartTime = intent.getStringExtra("START_TIME") ?: "No Start Time Selected"
        receivedEndTime = intent.getStringExtra("END_TIME") ?: "No End Time Selected"
        receivedRooms = intent.getStringExtra("ROOMS") ?: "No Rooms Selected"

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
                        val txtEmail = findViewById<TextView>(R.id.dispEmail)

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
                        if (txtEmail != null) txtEmail.text = user.email
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

        val contact = cNum.text.toString()
        val tables = tblCount.text.toString()
        val chairs = chrCount.text.toString()
        val purpose = purp.text.toString()
        val materials = mats.text.toString()

        // Check if any of the required fields are empty
        if (contact.isEmpty() || tables.isEmpty() || chairs.isEmpty() || purpose.isEmpty()) {
            // Display an error message to the user
            Toast.makeText(this, "Please fill in all required fields.", Toast.LENGTH_SHORT).show()
            return // Exit the function without proceeding
        } else {
            val intent = Intent(this, ConfirmationActivity::class.java).apply {
                // Pass the data to ConfirmationActivity
                putExtra("DATE", receivedDate)
                putExtra("START_TIME", receivedStartTime)
                putExtra("END_TIME", receivedEndTime)
                putExtra("ROOMS", receivedRooms)

                putExtra("CONTACT", contact)
                putExtra("TABLES", tables)
                putExtra("CHAIRS", chairs)
                putExtra("PURPOSE", purpose)
                putExtra("MATERIALS", materials)

                //pass the user data
                putExtra("USER_NAME", findViewById<TextView>(R.id.txtName).text.toString())
                putExtra("USER_TYPE", findViewById<TextView>(R.id.txt_usertype).text.toString())
                putExtra("USER_DEPT", findViewById<TextView>(R.id.dispDept).text.toString())
                putExtra("USER_ID", findViewById<TextView>(R.id.dispID).text.toString())
                putExtra("USER_EMAIL", findViewById<TextView>(R.id.dispEmail).text.toString())
            }
            startActivity(intent)
        }
    }

    fun btn_forms_back(view: View){
        val intent = Intent(this,Reserve::class.java)
        startActivity(intent)
    }
}