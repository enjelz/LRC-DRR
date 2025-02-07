package com.example.lrcd_r.users

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.cardview.widget.CardView
import com.example.lrcd_r.R
import com.example.lrcd_r.User
import com.example.lrcd_r.databinding.ActivityReservationDetailsBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit


class ReservationDetailsActivity : DrawerBaseActivity() {

    private lateinit var activityReservationDetailsBinding: ActivityReservationDetailsBinding
    lateinit var dialog1: Dialog
    private lateinit var reservationStatusCardView: CardView
    private lateinit var btnCancelReservation: Button
    private lateinit var sharedPreferences: SharedPreferences

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
    private lateinit var dispStatus: TextView



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reservation_details) // Provide layout resource ID
        activityReservationDetailsBinding = ActivityReservationDetailsBinding.inflate(layoutInflater)
        enableEdgeToEdge()

        //inflating dialog box
        dialog1 = Dialog(this)
        dialog1.setContentView(R.layout.user_dialog_cancel_reservation)
        dialog1.getWindow()
            ?.setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        dialog1.getWindow()?.setBackgroundDrawable(getDrawable(R.drawable.dialog_box))
        dialog1.setCancelable(false)

        reservationStatusCardView = findViewById(R.id.reservation_details_status_cardview_area)
        btnCancelReservation = findViewById(R.id.btn_cancel_reservation)
        dispRefNum = findViewById(R.id.disp_refnum)
        dispName = findViewById(R.id.disp_name)
        dispUserType = findViewById(R.id.disp_userType)
        dispDept = findViewById(R.id.disp_dept)
        dispIdNum = findViewById(R.id.disp_idnum)
        dispCNum = findViewById(R.id.disp_cnum)
        dispReservationDate = findViewById(R.id.disp_reservation_date)
        dispPurpose = findViewById(R.id.disp_purpose)
        dispDate = findViewById(R.id.disp_date)
        dispDuration = findViewById(R.id.disp_duration)
        dispRooms = findViewById(R.id.disp_rooms)
        dispTable = findViewById(R.id.disp_table)
        dispChair = findViewById(R.id.disp_chair)
        dispOther = findViewById(R.id.disp_other)
        dispStatus = findViewById(R.id.disp_users_status)

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

        reservationRef.addValueEventListener(object : ValueEventListener {
            @RequiresApi(Build.VERSION_CODES.O)
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    val chairCount = snapshot.child("chairCount").value?.toString() ?: "N/A"
                    val cnum = snapshot.child("cnum").value?.toString() ?: "N/A"
                    val date = snapshot.child("date").value?.toString() ?: "N/A"
                    val etime = snapshot.child("etime").value?.toString() ?: "N/A"
                    val otherMaterials = snapshot.child("otherMaterials").value?.toString()?.takeIf { it.isNotBlank() } ?: "None"
                    val purpose = snapshot.child("purpose").value?.toString() ?: "N/A"
                    val reservationDate = snapshot.child("reservationDate").value?.toString() ?: "N/A"
                    val roomNum = snapshot.child("roomNum").value?.toString() ?: "N/A"
                    val stime = snapshot.child("stime").value?.toString() ?: "N/A"
                    val tableCount = snapshot.child("tableCount").value?.toString() ?: "N/A"
                    val userID = snapshot.child("userID").value?.toString()
                    val reservationStatus = snapshot.child("status").value?.toString()?.trim()?.uppercase() ?: ""

                    Log.d("FirebaseData", "Fetched status: $reservationStatus") // Debugging

                    // Update UI
                    dispCNum.text = cnum
                    dispReservationDate.text = reservationDate
                    dispPurpose.text = purpose
                    dispDate.text = date
                    dispDuration.text = "$stime to $etime"
                    dispRooms.text = if (roomNum != "N/A") "Discussion Room $roomNum" else "N/A"
                    dispTable.text = tableCount
                    dispChair.text = chairCount
                    dispOther.text = otherMaterials


                    if (reservationStatus.isNotEmpty()) {
                        btnCancelReservation.visibility = View.GONE
                        dispStatus.text = reservationStatus
                    } else {
                        reservationStatusCardView.visibility = View.GONE
                    }

                    if (userID != null) {
                        fetchUserDetails(userID)
                    }

                } else {
                    Toast.makeText(this@ReservationDetailsActivity, "Reservation not found", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@ReservationDetailsActivity, "Error: ${error.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun fetchUserDetails(userID: String) {
        val auth = FirebaseAuth.getInstance()
        val currentUser = auth.currentUser
        val currentUserEmail = currentUser?.email

        if (currentUserEmail.isNullOrEmpty()) {
            Toast.makeText(this@ReservationDetailsActivity, "User not logged in", Toast.LENGTH_SHORT).show()
            return
        }

        databaseReference.child("Users").orderByChild("email").equalTo(currentUserEmail)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        for (userSnapshot in snapshot.children) {
                            val user = userSnapshot.getValue(User::class.java)

                            if (user != null) {
                                val txtName = findViewById<TextView>(R.id.disp_name)
                                val txtDept = findViewById<TextView>(R.id.disp_dept)
                                val txtUserType = findViewById<TextView>(R.id.disp_userType)
                                val txtID = findViewById<TextView>(R.id.disp_idnum)

                                if (txtName != null) {
                                    val formattedName = "${user.lname ?: "N/A"}, ${user.gname ?: "N/A"} ${user.mname ?: "N/A"}"
                                    txtName.text = formattedName
                                }
                                if (txtUserType != null) txtUserType.text = user.userType ?: "N/A"
                                if (txtDept != null) txtDept.text = user.dept ?: "N/A"
                                if (txtID != null) txtID.text = user.id ?: "N/A"

                                break // Stop loop after first match
                            }
                        }
                    } else {
                        Toast.makeText(this@ReservationDetailsActivity, "User not found", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(this@ReservationDetailsActivity, "Something went wrong", Toast.LENGTH_SHORT).show()
                }
            })
    }


    fun btn_reservation_details_back(view: View) {
        val intent = Intent(this, ReservationsActivity::class.java)
        overridePendingTransition(0, 0) // Disable animations
        startActivity(intent)
        overridePendingTransition(0, 0) // Disable animations again
    }
    fun btn_reservation_cancel(view: View) {
        dialog1.show()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun btnCancelYesClicked(view: View) {
        val refNum = dispRefNum.text.toString()
        val reservationRef = databaseReference.child("Reservations").child(refNum)

        reservationRef.child("date").get().addOnSuccessListener { dataSnapshot ->
            val reservationDateStr = dataSnapshot.value?.toString()
            if (reservationDateStr != null) {
                try {
                    val formatter = DateTimeFormatter.ofPattern("MMMM d, yyyy") // Ensure correct format
                    val reservationDate = LocalDate.parse(reservationDateStr, formatter)
                    val currentDate = LocalDate.now()
                    val daysUntilReservation = ChronoUnit.DAYS.between(currentDate, reservationDate)

                    Log.d("CancelCheck", "Current Date: $currentDate, Reservation Date: $reservationDate, Days Until: $daysUntilReservation")

                    // Prevent cancellation on the same day or a day before
                    if (daysUntilReservation <= 1) {
                        Toast.makeText(this, "You can no longer cancel this reservation.", Toast.LENGTH_SHORT).show()
                        dialog1.dismiss()
                        return@addOnSuccessListener
                    } else {
                        // Proceed with cancellation
                        dialog1.dismiss()
                        reservationRef.child("status").setValue("CANCELLED").addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                Toast.makeText(this, "Reservation Cancelled", Toast.LENGTH_SHORT).show()
                                btnCancelReservation.visibility = View.GONE
                                val intent = intent
                                finish()
                                overridePendingTransition(0, 0) // Disable animations
                                startActivity(intent)
                                overridePendingTransition(0, 0) // Disable animations again
                            } else {
                                Toast.makeText(this, "Failed to cancel reservation", Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                } catch (e: Exception) {
                    Log.e("DateParseError", "Error parsing reservation date: ${e.message}")
                    Toast.makeText(this, "Error processing cancellation", Toast.LENGTH_SHORT).show()
                    dialog1.dismiss()
                }
            } else {
                Toast.makeText(this, "Reservation date not found", Toast.LENGTH_SHORT).show()
                dialog1.dismiss()
            }
        }.addOnFailureListener {
            Toast.makeText(this, "Error fetching reservation date", Toast.LENGTH_SHORT).show()
            dialog1.dismiss()
        }
    }


    fun btnCancelNoClicked(view: View) {
        dialog1.dismiss()
    }
}