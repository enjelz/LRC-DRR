package com.example.lrcd_r.admin

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import com.example.lrcd_r.R
import com.example.lrcd_r.databinding.ActivityAdminReservationsBinding
import com.example.lrcd_r.users.ReservationDetailsActivity
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

        // Add to parent layout
        reservationsLayout.addView(cardView)
    }

    fun History(view: View) {
        startActivity(Intent(this, AdminReservationHistoryActivity::class.java))
        overridePendingTransition(0, 0)
    }

    fun reservations(view: View) {
        // Get the parent CardView
        val parentCardView = view.parent as? ViewGroup
        if (parentCardView != null) {
            // Find the TextView inside the card
            val refNumTextView = parentCardView.findViewById<TextView>(R.id.dispRefNum)
            val refNumText = refNumTextView?.text?.toString()?.replace("Reference Number: ", "")

            if (!refNumText.isNullOrEmpty()) {
                val intent = Intent(this, AdminUpdateStatusActivity::class.java)
                intent.putExtra("REF_NUM", refNumText) // Pass Reference Number
                startActivity(intent)
            } else {
                Toast.makeText(this, "Reference number not found!", Toast.LENGTH_SHORT).show()
            }
        }
    }
}