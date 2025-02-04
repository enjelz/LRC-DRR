package com.example.lrcd_r.users

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.compose.ui.semantics.text
import com.example.lrcd_r.R
import com.example.lrcd_r.databinding.ActivityConfirmationBinding

class ConfirmationActivity : DrawerBaseActivity() {

    lateinit var activityConfirmationBinding: ActivityConfirmationBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_confirmation) // Provide layout resource ID
        activityConfirmationBinding = ActivityConfirmationBinding.inflate(layoutInflater)
        enableEdgeToEdge()

        val contact = intent.getStringExtra("CONTACT")
        val tables = intent.getStringExtra("TABLES")
        val chairs = intent.getStringExtra("CHAIRS")
        val purpose = intent.getStringExtra("PURPOSE")
        val materials = intent.getStringExtra("MATERIALS")

        //get the user data
        val userName = intent.getStringExtra("USER_NAME")
        val userType = intent.getStringExtra("USER_TYPE")
        val userDept = intent.getStringExtra("USER_DEPT")
        val userId = intent.getStringExtra("USER_ID")
        val userEmail = intent.getStringExtra("USER_EMAIL")

        //find the user data textview
        val valName = findViewById<TextView>(R.id.val_name)
        val valUserType = findViewById<TextView>(R.id.lbl_usertype)
        val valDept = findViewById<TextView>(R.id.val_college)
        val valId = findViewById<TextView>(R.id.val_idNum)
        val valEmail = findViewById<TextView>(R.id.val_email)

        // Find the TextViews in the layout
        val valContact = findViewById<TextView>(R.id.val_contact)
        val valTables = findViewById<TextView>(R.id.val_tables)
        val valChairs = findViewById<TextView>(R.id.val_chairs)
        val valPurpose = findViewById<TextView>(R.id.val_purpose)
        val valMaterials = findViewById<TextView>(R.id.val_othermaterials)


        // Set the text of the TextViews
        valContact.text = contact
        valTables.text = tables
        valChairs.text = chairs
        valPurpose.text = purpose
        valMaterials.text = materials

        //set the user data textview
        valName.text = userName
        valUserType.text = userType
        valDept.text = userDept
        valId.text = userId
        valEmail.text = userEmail





    }
    fun btn_confirmation_confirm(view: View){
        val intent = Intent(this, RemindersActivity::class.java)
        startActivity(intent)
    }
    fun edit_confirmation_details(view: View){
        val intent = Intent(this, FormsActivity::class.java)
        startActivity(intent)
    }

}