package com.example.lrcd_r.admin

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import com.example.lrcd_r.R
import com.example.lrcd_r.databinding.ActivityAdminReservationsBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class AdminReservationsActivity : AdminDrawerBaseActivity() {

    private lateinit var adminReservationsBinding: ActivityAdminReservationsBinding
    private lateinit var reservationsLayout: LinearLayout
    private lateinit var databaseReference: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_reservations) // Provide layout resource ID
        adminReservationsBinding = ActivityAdminReservationsBinding.inflate(layoutInflater)
        enableEdgeToEdge()

        reservationsLayout = findViewById(R.id.adminReservationCard) // Make sure this ID exists in your XML
        databaseReference = FirebaseDatabase.getInstance().getReference("Reservations")

        fetchAllReservations()
    }

    private fun fetchAllReservations() {
        databaseReference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                reservationsLayout.removeAllViews() // Clear old data

                for (reservationSnapshot in snapshot.children) {
                    val refNum = reservationSnapshot.key.toString()

                    // Create and add a reservation card dynamically
                    addReservationCard(refNum)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@AdminReservationsActivity, "Error: ${error.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun addReservationCard(refNum: String) {
        val inflater = LayoutInflater.from(this)
        val cardView = inflater.inflate(R.layout.reservation_card, reservationsLayout, false)

        val refNumTextView = cardView.findViewById<TextView>(R.id.dispRefNum)
        refNumTextView.text = "Reference Number: $refNum"

        cardView.setOnClickListener {
            val intent = Intent(this, AdminUpdateStatusActivity::class.java)
            intent.putExtra("REF_NUM", refNum) // Pass reference number to next activity
            startActivity(intent)
        }

        // Add to parent layout
        reservationsLayout.addView(cardView)
    }

    fun History(view: View) {
        startActivity(Intent(this, AdminReservationHistoryActivity::class.java))
        overridePendingTransition(0, 0)
    }

    fun R1(view: View) {
        startActivity(Intent(this, AdminUpdateStatusActivity::class.java))
        overridePendingTransition(0, 0)
    }
}