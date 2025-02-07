package com.example.lrcd_r.users

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import com.example.lrcd_r.Login
import com.example.lrcd_r.R
import com.example.lrcd_r.Reservations
import com.example.lrcd_r.databinding.ActivityConfirmationBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import java.text.SimpleDateFormat
import java.util.*

class ConfirmationActivity : DrawerBaseActivity() {

    private lateinit var activityConfirmationBinding: ActivityConfirmationBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var databaseReferences: DatabaseReference
    private lateinit var uid: String

    private lateinit var userName: String
    private lateinit var userType: String
    private lateinit var userDept: String
    private lateinit var userId: String
    private lateinit var userEmail: String

    private lateinit var contact: String
    private lateinit var tables: String
    private lateinit var chairs: String
    private lateinit var purpose: String
    private lateinit var materials: String

    private lateinit var confirmedDate: String
    private lateinit var confirmedStartTime: String
    private lateinit var confirmedEndTime: String
    private lateinit var confirmedRooms: String

    private lateinit var currentDate: String
    lateinit var dialogConfReserve: Dialog


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityConfirmationBinding = ActivityConfirmationBinding.inflate(layoutInflater)
        setContentView(R.layout.activity_confirmation) // Provide layout resource ID
        enableEdgeToEdge()

        //firebase
        firebaseAuth = FirebaseAuth.getInstance()
        databaseReferences = FirebaseDatabase.getInstance().getReference("Users")
        uid = firebaseAuth.currentUser?.uid.toString()


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

        val valDate = findViewById<TextView>(R.id.val_schedDate)
        val valTime = findViewById<TextView>(R.id.val_schedTime)
        val valRoom = findViewById<TextView>(R.id.val_schedRoom)

        // Find the TextView
        val valDateReq: TextView = findViewById(R.id.val_dateReq)

        //get the user data
        userName = intent.getStringExtra("USER_NAME").toString()
        userType = intent.getStringExtra("USER_TYPE").toString()
        userDept = intent.getStringExtra("USER_DEPT").toString()
        userId = intent.getStringExtra("USER_ID").toString()
        userEmail = intent.getStringExtra("USER_EMAIL").toString()

        contact = intent.getStringExtra("CONTACT").toString()
        tables = intent.getStringExtra("TABLES").toString()
        chairs = intent.getStringExtra("CHAIRS").toString()
        purpose = intent.getStringExtra("PURPOSE").toString()
        materials = intent.getStringExtra("MATERIALS").toString()

        // Get current date in the format "MMM dd, yyyy"
        currentDate = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault()).format(Date()).toString()

        confirmedDate = intent.getStringExtra("DATE") ?: "No Date"
        confirmedStartTime = intent.getStringExtra("START_TIME") ?: "No Start Time"
        confirmedEndTime = intent.getStringExtra("END_TIME") ?: "No End Time"
        confirmedRooms = intent.getStringExtra("ROOMS") ?: "No Rooms"


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

        // Set the current date to the TextView
        valDateReq.text = currentDate

        valDate.text = confirmedDate
        valTime.text = "$confirmedStartTime to $confirmedEndTime"
        valRoom.text = "Discussion Room $confirmedRooms"

        //inflating dialog box
        dialogConfReserve = Dialog(this)
        dialogConfReserve.setContentView(R.layout.user_dialog_confirm_reservation)
        dialogConfReserve.getWindow()
            ?.setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        dialogConfReserve.getWindow()?.setBackgroundDrawable(getDrawable(R.drawable.dialog_box))
        dialogConfReserve.setCancelable(false)
    }

    fun btn_confirmation_confirm(view: View) {
        dialogConfReserve.show()
    }

    fun btnYesClicked(view: View) {
        val sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE)
        val userID = sharedPreferences.getString("USER_ID", null) ?: return // Retrieve stored userID

        val reservationRef = FirebaseDatabase.getInstance().getReference("Reservations")

        // Fetch the last reservation ID and generate the next ID
        reservationRef.orderByKey().limitToLast(1).get().addOnSuccessListener { snapshot ->
            var newIdNumber = 1 // Default ID number if no previous entry exists

            if (snapshot.exists()) {
                val lastKey = snapshot.children.first().key
                lastKey?.let {
                    val numericPart = it.substringAfter("LRCDRR-").toIntOrNull()
                    if (numericPart != null) {
                        newIdNumber = numericPart + 1
                    }
                }
            }

            val newReservationId = String.format("LRCDRR-%05d", newIdNumber) // Ensure 5-digit format

            // Create the reservation object
            val reservation = Reservations(
                userID = userID,
                reservationDate = currentDate,
                date = confirmedDate,
                stime = confirmedStartTime,
                etime = confirmedEndTime,
                roomNum = confirmedRooms,
                cnum = contact,
                tableCount = tables,
                chairCount = chairs,
                purpose = purpose,
                otherMaterials = materials,
                status = ""
            )

            // Push data with the new custom ID
            reservationRef.child(newReservationId).setValue(reservation).addOnCompleteListener {
                if (it.isSuccessful) {
                    Toast.makeText(this, "Reservation added successfully", Toast.LENGTH_SHORT).show()
                    val intent = Intent(this, RemindersActivity::class.java).apply {
                        putExtra("REF_NO", newReservationId)
                    }
                    startActivity(intent)
                    finish()
                } else {
                    Toast.makeText(this, "Failed to add reservation: ${it.exception?.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }.addOnFailureListener {
            Toast.makeText(this, "Failed to retrieve last reservation ID", Toast.LENGTH_SHORT).show()
        }
    }


    fun btnNoClicked(view: View) {
        dialogConfReserve.dismiss()
    }

    fun edit_confirmation_details(view: View) {
        val intent = Intent(this, Reserve::class.java)
        startActivity(intent)
    }
}