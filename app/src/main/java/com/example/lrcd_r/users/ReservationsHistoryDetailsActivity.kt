package com.example.lrcd_r.users

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import com.example.lrcd_r.R
import com.example.lrcd_r.User
import com.example.lrcd_r.databinding.ActivityReservationsHistoryDetailsBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class ReservationsHistoryDetailsActivity : DrawerBaseActivity() {

    private lateinit var reservationsHistoryDetailsBinding: ActivityReservationsHistoryDetailsBinding
    private lateinit var databaseReference: DatabaseReference
    private lateinit var sharedPreferences: SharedPreferences
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
    private lateinit var dispStatus: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reservations_history_details) // Provide layout resource ID
        reservationsHistoryDetailsBinding = ActivityReservationsHistoryDetailsBinding.inflate(layoutInflater)
        enableEdgeToEdge()

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
        dispStatus = findViewById(R.id.disp_status)


        sharedPreferences = getSharedPreferences("visibility_prefs", Context.MODE_PRIVATE)
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
            @RequiresApi(Build.VERSION_CODES.O)
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
                    val status = snapshot.child("status").value?.toString() ?: "N/A"

                    //  Concatenate "Discussion Room " + room number
                    val formattedRoomNum =
                        if (roomNum != "N/A") "Discussion Room $roomNum" else "N/A"

                    dispCNum.text = cnum
                    dispReservationDate.text = reservationDate
                    dispPurpose.text = purpose
                    dispDate.text = date
                    dispDuration.text = "$stime to $etime"
                    dispRooms.text = formattedRoomNum
                    dispTable.text = tableCount
                    dispChair.text = chairCount
                    dispOther.text = otherMaterials
                    dispStatus.text = status

                    if (!userID.isNullOrEmpty()) {
                        fetchUserDetails(userID)  // Pass the userID from reservation to fetch the correct user details
                    }
                } else {
                    Toast.makeText(
                        this@ReservationsHistoryDetailsActivity,
                        "Reservation not found",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(
                    this@ReservationsHistoryDetailsActivity,
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
                            this@ReservationsHistoryDetailsActivity,
                            "User not found",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(
                        this@ReservationsHistoryDetailsActivity,
                        "Something went wrong",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            })
    }

    fun back(view:View){
        val intent = Intent(this, ReservationsHistoryActivity::class.java)
        overridePendingTransition(0, 0) // Disable animations
        startActivity(intent)
        overridePendingTransition(0, 0) // Disable animations again
    }
}