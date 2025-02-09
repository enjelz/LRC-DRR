package com.example.lrcd_r.users

import android.content.Context
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import com.example.lrcd_r.R
import com.example.lrcd_r.databinding.ActivityReservationsHistoryBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import android.app.Dialog

class ReservationsHistoryActivity : DrawerBaseActivity() {

    private lateinit var reservationsHistoryBinding: ActivityReservationsHistoryBinding
    private lateinit var reservationsLayout: LinearLayout
    private lateinit var databaseReference: DatabaseReference
    private lateinit var filterDialog: Dialog
    private var currentFilter: String? = null
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

        setupFilterDialog()
        
        val filterContainer = findViewById<LinearLayout>(R.id.filterContainer)
        filterContainer.setOnClickListener {
            filterDialog.show()
        }
        
        fetchReservations()
    }

    fun Upcoming(view: View) {
        val intent = Intent(this, ReservationsActivity::class.java)
        overridePendingTransition(0, 0) // Disable animations
        startActivity(intent)
        overridePendingTransition(0, 0) // Disable animations again
    }

    private fun setupFilterDialog() {
        filterDialog = Dialog(this)
        filterDialog.setContentView(R.layout.dialog_filter_status)
        filterDialog.window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )

        // Setup click listeners for filter options
        filterDialog.findViewById<TextView>(R.id.filterShowedUp).setOnClickListener {
            currentFilter = "SHOWED UP"
            fetchReservations()
            filterDialog.dismiss()
        }

        filterDialog.findViewById<TextView>(R.id.filterCancelled).setOnClickListener {
            currentFilter = "CANCELLED"
            fetchReservations()
            filterDialog.dismiss()
        }

        filterDialog.findViewById<TextView>(R.id.filterNoShow).setOnClickListener {
            currentFilter = "NO SHOW/ABSENT"
            fetchReservations()
            filterDialog.dismiss()
        }

        filterDialog.findViewById<TextView>(R.id.filterAll).setOnClickListener {
            currentFilter = null
            fetchReservations()
            filterDialog.dismiss()
        }
    }

    private fun fetchReservations() {
        databaseReference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                reservationsLayout.removeAllViews() // Clear old data

                // First, collect all the data we need
                val reservationsData = mutableListOf<Pair<String, String>>()
                
                for (reservationSnapshot in snapshot.children) {
                    val reservationUserID = reservationSnapshot.child("userID").value?.toString()
                    val status = reservationSnapshot.child("status").getValue(String::class.java)

                    // Check if reservation belongs to the logged-in user and status is NOT null/empty
                    if (reservationUserID == userID && !status.isNullOrEmpty()) {
                        // Apply filter
                        if (currentFilter == null || status.uppercase() == currentFilter) {
                            val refNum = reservationSnapshot.key.toString()
                            reservationsData.add(Pair(refNum, status))
                        }
                    }
                }

                // Then create all cards at once
                reservationsData.forEach { (refNum, status) ->
                    addReservationCard(refNum, status)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@ReservationsHistoryActivity, "Error: ${error.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun addReservationCard(refNum: String, status: String) {
        val inflater = LayoutInflater.from(this)
        val cardView = inflater.inflate(R.layout.reservation_card, reservationsLayout, false) as ViewGroup

        val refNumTextView = cardView.findViewById<TextView>(R.id.dispRefNum)
        refNumTextView.text = "Reference Number: $refNum"

        // Get the CardView and arrow ImageView
        val cardViewBackground = cardView.findViewById<CardView>(R.id.reservation_card)
        val arrowImageView = cardView.findViewById<ImageView>(R.id.arrow)

        // Set card color based on status
        val (cardColor, arrowColor) = when (status.uppercase()) {
            "NO SHOW/ABSENT" -> Pair(
                ContextCompat.getColor(this, R.color.pending),
                ContextCompat.getColor(this, R.color.pending_dark)
            )
            "SHOWED UP" -> Pair(
                ContextCompat.getColor(this, R.color.confirm),
                ContextCompat.getColor(this, R.color.confirm_dark)
            )
            "CANCELLED" -> Pair(
                ContextCompat.getColor(this, R.color.cancel),
                ContextCompat.getColor(this, R.color.cancel_dark)
            )
            else -> Pair(
                ContextCompat.getColor(this, R.color.blue),
                ContextCompat.getColor(this, R.color.blue_dark)
            )
        }

        // Apply colors
        cardViewBackground.setCardBackgroundColor(cardColor)
        arrowImageView.background = ColorDrawable(arrowColor)
        arrowImageView.imageTintList = ColorStateList.valueOf(Color.WHITE)

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