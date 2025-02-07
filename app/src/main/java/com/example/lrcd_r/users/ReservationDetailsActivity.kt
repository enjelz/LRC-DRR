package com.example.lrcd_r.users

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
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

class ReservationDetailsActivity : DrawerBaseActivity() {

    private lateinit var activityReservationDetailsBinding: ActivityReservationDetailsBinding
    lateinit var dialog1: Dialog
    private lateinit var reservationCancelledCardView: CardView
    private lateinit var btnCancelReservation: Button
    lateinit var sharedPreferences: SharedPreferences

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

        reservationCancelledCardView = findViewById(R.id.reservationCancelled)
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

        sharedPreferences = getSharedPreferences("visibility_prefs", Context.MODE_PRIVATE)

        val cardViewVisible = sharedPreferences.getBoolean("cardViewVisible", false)
        val buttonVisible = sharedPreferences.getBoolean("buttonVisible", true)

        reservationCancelledCardView.visibility = if (cardViewVisible) View.VISIBLE else View.GONE
        btnCancelReservation.visibility = if (buttonVisible) View.VISIBLE else View.GONE

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
                    val otherMaterials = snapshot.child("otherMaterials").value?.toString()?.takeIf { it.isNotBlank() } ?: "None" // Set "None" if null or empty
                    val purpose = snapshot.child("purpose").value?.toString() ?: "N/A"
                    val reservationDate = snapshot.child("reservationDate").value?.toString() ?: "N/A"
                    val roomNum = snapshot.child("roomNum").value?.toString() ?: "N/A"
                    val stime = snapshot.child("stime").value?.toString() ?: "N/A"
                    val tableCount = snapshot.child("tableCount").value?.toString() ?: "N/A"
                    val userID = snapshot.child("userID").value?.toString()

                    //  Concatenate "Discussion Room " + room number
                    val formattedRoomNum = if (roomNum != "N/A") "Discussion Room $roomNum" else "N/A"

                    dispCNum.text = cnum
                    dispReservationDate.text = reservationDate
                    dispPurpose.text = purpose
                    dispDate.text = date
                    dispDuration.text = "$stime to $etime"
                    dispRooms.text = formattedRoomNum // ✅ Updated
                    dispTable.text = tableCount
                    dispChair.text = chairCount
                    dispOther.text = otherMaterials // ✅ Updated

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
        startActivity(intent)
    }
    fun btn_reservation_cancel(view: View) {
        dialog1.show()
    }

    fun btnCancelYesClicked(view: View) {
        dialog1.dismiss()
        // Save visibility states in SharedPreferences
        val editor = sharedPreferences.edit()
        editor.putBoolean("cardViewVisible", true)
        editor.putBoolean("buttonVisible", false)
        editor.apply()

        reservationCancelledCardView.visibility = View.VISIBLE
        btnCancelReservation.visibility = View.GONE // Hide the button
    }

    fun btnCancelNoClicked(view: View) {
        dialog1.dismiss()
    }
}