package com.example.lrcd_r.admin

import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import com.example.lrcd_r.R
import com.example.lrcd_r.User
import com.example.lrcd_r.databinding.ActivityAdminUpdateStatusBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class AdminUpdateStatusActivity : AdminDrawerBaseActivity() {

    private lateinit var adminUpdateStatusBinding: ActivityAdminUpdateStatusBinding
    private lateinit var reservationStatusSpinner: Spinner
    lateinit var dialog3: Dialog

    private lateinit var databaseReference: DatabaseReference
    private lateinit var dispRefNum: TextView
    private lateinit var dispName: TextView
    private lateinit var dispUserType: TextView
    private lateinit var dispDept: TextView
    private lateinit var dispIdNum: TextView
    private lateinit var dispCNum: TextView
    private lateinit var dispReservationDate: TextView
    private lateinit var dispPurpose: TextView
    private lateinit var dispDate: TextView
    private lateinit var dispDuration: TextView
    private lateinit var dispRooms: TextView
    private lateinit var dispTable: TextView
    private lateinit var dispChair: TextView
    private lateinit var dispOther: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_update_status) // Provide layout resource ID
        adminUpdateStatusBinding = ActivityAdminUpdateStatusBinding.inflate(layoutInflater)
        enableEdgeToEdge()

        // Initialize Spinner
        reservationStatusSpinner = findViewById(R.id.reservation_status_spinner)

        // Set up the adapter with the default spinner item layout
        val adapter = object : ArrayAdapter<String>(
            this,
            android.R.layout.simple_spinner_item,  // Default spinner item layout
            resources.getStringArray(R.array.status_options)  // Array from strings.xml
        ) {
            override fun getView(position: Int, convertView: View?, parent: android.view.ViewGroup): View {
                val view = super.getView(position, convertView, parent)
                val textView = view.findViewById<TextView>(android.R.id.text1)

                textView.setPadding(16, 20, 16, 20) // Adjust top & bottom padding
                textView.textSize = 16f // Adjust text size
                textView.setBackgroundColor(getStatusColor(position)) // Background color
                textView.setTextColor(Color.WHITE) // Text color
                return view
            }

            override fun getDropDownView(position: Int, convertView: View?, parent: android.view.ViewGroup): View {
                val view = super.getDropDownView(position, convertView, parent)
                val textView = view.findViewById<TextView>(android.R.id.text1)

                textView.setPadding(16, 20, 16, 20) // Adjust top & bottom padding
                textView.textSize = 16f // Adjust text size
                textView.setBackgroundColor(getStatusColor(position)) // Background color
                textView.setTextColor(Color.WHITE) // Text color
                return view
            }
        }

        // Set the adapter to the spinner
        reservationStatusSpinner.adapter = adapter

        // Set a listener for Spinner item selection
        reservationStatusSpinner.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    val selectedStatus = parent.getItemAtPosition(position).toString()
                    // Change the background color based on the selected status
                    // (You might want to remove this line if you're setting the background color in the adapter)
                    // view?.setBackgroundColor(getStatusColor(position))
                }

                override fun onNothingSelected(parent: AdapterView<*>) {}
            }

        //inflating dialog box
        dialog3 = Dialog(this)
        dialog3.setContentView(R.layout.dialog_admin_update_status)
        dialog3.getWindow()
            ?.setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        dialog3.getWindow()?.setBackgroundDrawable(getDrawable(R.drawable.dialog_box))
        dialog3.setCancelable(false)

        dispRefNum = findViewById(R.id.disp_refnum)
        dispName = findViewById(R.id.disp_name)
        dispUserType = findViewById(R.id.disp_userType)
        dispDept = findViewById(R.id.disp_dept)
        dispIdNum = findViewById(R.id.disp_idnum)
        dispCNum = findViewById(R.id.disp_cnum)
        dispReservationDate = findViewById(R.id.disp_reservation_date)
        dispPurpose = findViewById(R.id.disp_purpose)
        dispDate = findViewById(R.id.disp_date)
        dispDuration = findViewById(R.id.disp_time)
        dispRooms = findViewById(R.id.disp_rooms)
        dispTable = findViewById(R.id.disp_table)
        dispChair = findViewById(R.id.disp_chair)
        dispOther = findViewById(R.id.disp_other)

        databaseReference = FirebaseDatabase.getInstance().reference

        val refNum = intent.getStringExtra("REF_NUM")

        if (refNum != null) {
            dispRefNum.text = refNum
            fetchReservationDetails(refNum)
        }
    }

    private fun fetchReservationDetails(refNum: String) {
        val reservationRef = databaseReference.child("Reservations").child(refNum)

        reservationRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    val chairCount = snapshot.child("chairCount").value?.toString() ?: "N/A"
                    val cnum = snapshot.child("cnum").value?.toString() ?: "N/A"
                    val date = snapshot.child("date").value?.toString() ?: "N/A"
                    val etime = snapshot.child("etime").value?.toString() ?: "N/A"
                    val otherMaterials = snapshot.child("otherMaterials").value?.toString()
                        ?.takeIf { it.isNotBlank() } ?: "None" // Set "None" if null or empty
                    val purpose = snapshot.child("purpose").value?.toString() ?: "N/A"
                    val reservationDate =
                        snapshot.child("reservationDate").value?.toString() ?: "N/A"
                    val roomNum = snapshot.child("roomNum").value?.toString() ?: "N/A"
                    val stime = snapshot.child("stime").value?.toString() ?: "N/A"
                    val tableCount = snapshot.child("tableCount").value?.toString() ?: "N/A"
                    val userID = snapshot.child("userID").value?.toString()

                    //  Concatenate "Discussion Room " + room number
                    val formattedRoomNum =
                        if (roomNum != "N/A") "Discussion Room $roomNum" else "N/A"

                    dispCNum.text = cnum
                    dispReservationDate.text = reservationDate
                    dispPurpose.text = purpose
                    dispDate.text = date
                    dispDuration.text = "$stime to $etime"
                    dispRooms.text = formattedRoomNum // ✅ Updated
                    dispTable.text = tableCount
                    dispChair.text = chairCount
                    dispOther.text = otherMaterials // ✅ Updated

                    if (!userID.isNullOrEmpty()) {
                        fetchUserDetails(userID)  // Pass the userID from reservation to fetch the correct user details
                    }
                } else {
                    Toast.makeText(
                        this@AdminUpdateStatusActivity,
                        "Reservation not found",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(
                    this@AdminUpdateStatusActivity,
                    "Error: ${error.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        })
    }

    private fun fetchUserDetails(userID: String) {
        // Retrieve user details using the provided userID, not the logged-in user's email
        databaseReference.child("Users").child(userID)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        val user = snapshot.getValue(User::class.java)

                        if (user != null) {
                            val txtName = findViewById<TextView>(R.id.disp_name)
                            val txtDept = findViewById<TextView>(R.id.disp_dept)
                            val txtUserType = findViewById<TextView>(R.id.disp_userType)
                            val txtID = findViewById<TextView>(R.id.disp_idnum)

                            if (txtName != null) {
                                val formattedName =
                                    "${user.lname ?: "N/A"}, ${user.gname ?: "N/A"} ${user.mname ?: "N/A"}"
                                txtName.text = formattedName
                            }
                            if (txtUserType != null) txtUserType.text = user.userType ?: "N/A"
                            if (txtDept != null) txtDept.text = user.dept ?: "N/A"
                            if (txtID != null) txtID.text = user.id ?: "N/A"
                        }
                    } else {
                        Toast.makeText(
                            this@AdminUpdateStatusActivity,
                            "User not found",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(
                        this@AdminUpdateStatusActivity,
                        "Something went wrong",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            })
    }


    // Function to get the background color based on the selected status position
    private fun getStatusColor(position: Int): Int {
        return when (position) {
            0 -> Color.parseColor("#4c4848") // Grey
            1 -> Color.parseColor("#265427") // Green for Confirmed
            2 -> Color.parseColor("#3e0606") // Maroon for Cancelled
            3 -> Color.parseColor("#a06617") // Orange
            else -> Color.TRANSPARENT // Default transparent if position is invalid
        }
    }

    fun R1Back(view: View) {
        startActivity(Intent(this, AdminReservationsActivity::class.java))
        overridePendingTransition(0, 0)
    }

    fun btnReservationUpdate(view: View) {
        val selectedPosition = reservationStatusSpinner.selectedItemPosition
        val selectedStatus = reservationStatusSpinner.selectedItem.toString()

        // Check if the user has selected a status
        if (selectedPosition == 0) { // Assuming position 0 is a default "Select Status"
            Toast.makeText(this, "You have to select a status first!", Toast.LENGTH_SHORT).show()
            return // Stop further execution
        } else {
            dialog3.show()
        }
    }

    fun btnUpdateNo(view: View) {
        dialog3.dismiss()
    }

    fun btnUpdateYes(view: View) {
        dialog3.dismiss()

        val selectedStatus = reservationStatusSpinner.selectedItem.toString()
        val refNum = intent.getStringExtra("REF_NUM") // Get Reference Number from Intent

        if (refNum.isNullOrEmpty()) {
            Toast.makeText(this, "Error: Reference Number not found!", Toast.LENGTH_SHORT).show()
            return
        }

        // Reference to the reservation in Firebase
        val reservationRef = FirebaseDatabase.getInstance().getReference("Reservations").child(refNum)

        // Update the status field
        reservationRef.child("status").setValue(selectedStatus)
            .addOnSuccessListener {
                Toast.makeText(this, "Status Updated!", Toast.LENGTH_SHORT).show()
                startActivity(Intent(this, AdminReservationsActivity::class.java))
                overridePendingTransition(0, 0)
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Failed to update status: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }
}