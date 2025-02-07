package com.example.lrcd_r.users

import android.content.Context
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
import com.example.lrcd_r.databinding.ActivityReservationsHistoryBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class ReservationsHistoryActivity : DrawerBaseActivity() {

    private lateinit var reservationsHistoryBinding: ActivityReservationsHistoryBinding
    private lateinit var reservationsLayout: LinearLayout
    private lateinit var databaseReference: DatabaseReference
    private var userID: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reservations_history) // Provide layout resource ID
        reservationsHistoryBinding = ActivityReservationsHistoryBinding.inflate(layoutInflater)
        enableEdgeToEdge()

        val userPrefs = getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
        userID = userPrefs.getString("USER_ID", null)

        if (userID == null) {
            Toast.makeText(this, "User not found!", Toast.LENGTH_SHORT).show()
            return
        }

        reservationsLayout = findViewById(R.id.reservationsHistoryCard) // Make sure this ID exists in your XML
        databaseReference = FirebaseDatabase.getInstance().getReference("Reservations")

        fetchReservations()
    }

    fun Upcoming(view: View) {
        val intent = Intent(this, ReservationsActivity::class.java)
        overridePendingTransition(0, 0) // Disable animations
        startActivity(intent)
        overridePendingTransition(0, 0) // Disable animations again
    }

    private fun fetchReservations() {
        databaseReference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                reservationsLayout.removeAllViews() // Clear old data

                for (reservationSnapshot in snapshot.children) {
                    val reservationUserID = reservationSnapshot.child("userID").value?.toString()
                    val status = reservationSnapshot.child("status").getValue(String::class.java)

                    // Check if reservation belongs to the logged-in user and status is NOT null/empty
                    if (reservationUserID == userID && !status.isNullOrEmpty()) {
                        val refNum = reservationSnapshot.key.toString()

                        // Create a new reservation card dynamically
                        addReservationCard(refNum)
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@ReservationsHistoryActivity, "Error: ${error.message}", Toast.LENGTH_SHORT).show()
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

    fun reservations (view: View) {
        // Get the parent CardView
        val parentCardView = view.parent as? ViewGroup
        if (parentCardView != null) {
            // Find the TextView inside the card
            val refNumTextView = parentCardView.findViewById<TextView>(R.id.dispRefNum)
            val refNumText = refNumTextView?.text?.toString()?.replace("Reference Number: ", "")

            if (!refNumText.isNullOrEmpty()) {
                val intent = Intent(this, ReservationsHistoryDetailsActivity::class.java)
                overridePendingTransition(0, 0) // Disable animations
                intent.putExtra("REF_NUM", refNumText) // Pass Reference Number
                startActivity(intent)
                overridePendingTransition(0, 0) // Disable animations
            } else {
                Toast.makeText(this, "Reference number not found!", Toast.LENGTH_SHORT).show()
            }
        }
    }
}