package com.example.lrcd_r.users

import android.content.Context
import android.content.Intent
import android.graphics.Typeface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.core.content.ContextCompat
import com.example.lrcd_r.R
import com.example.lrcd_r.databinding.ActivityReservationsBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class ReservationsActivity :  DrawerBaseActivity() {

    private lateinit var activityReservationsBinding: ActivityReservationsBinding
    private lateinit var reservationsLayout: LinearLayout
    private lateinit var databaseReference: DatabaseReference
    private var userID: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reservations) // Provide layout resource ID
        activityReservationsBinding = ActivityReservationsBinding.inflate(layoutInflater)
        enableEdgeToEdge()

        reservationsLayout = findViewById(R.id.ReservationCard)

        // Retrieve logged-in userID from SharedPreferences
        val userPrefs = getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
        userID = userPrefs.getString("USER_ID", null)

        if (userID == null) {
            Toast.makeText(this, "User not found!", Toast.LENGTH_SHORT).show()
            return
        }

        // Reference to Firebase Reservations node
        databaseReference = FirebaseDatabase.getInstance().getReference("Reservations")

        // Fetch reservations
        fetchUserReservations()
    }

    private fun fetchUserReservations() {
        databaseReference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                reservationsLayout.removeAllViews() // Clear old data
                
                // Create default text view first but set to GONE
                val defaultTextView = TextView(this@ReservationsActivity).apply {
                    id = R.id.default_text
                    layoutParams = LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                    ).apply {
                        topMargin = resources.getDimensionPixelSize(R.dimen.margin_20dp)
                    }
                    text = "You have no upcoming reservation schedule"
                    textAlignment = View.TEXT_ALIGNMENT_CENTER
                    setTextColor(ContextCompat.getColor(context, R.color.gray))
                    textSize = 15f
                    typeface = Typeface.create("sans-serif-medium", Typeface.ITALIC)
                    visibility = View.GONE // Set to GONE by default
                }
                reservationsLayout.addView(defaultTextView)
                
                var hasReservations = false

                // Add all reservation cards
                for (reservationSnapshot in snapshot.children) {
                    val reservationUserID = reservationSnapshot.child("userID").value?.toString()
                    val status = reservationSnapshot.child("status").getValue(String::class.java)

                    if (reservationUserID == userID && status.isNullOrEmpty()) {
                        hasReservations = true
                        val refNum = reservationSnapshot.key.toString()
                        addReservationCard(refNum)
                    }
                }

                // Only make default text visible if there are no reservations
                if (!hasReservations) {
                    defaultTextView.visibility = View.VISIBLE
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@ReservationsActivity, "Error: ${error.message}", Toast.LENGTH_SHORT).show()
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
                val intent = Intent(this, ReservationDetailsActivity::class.java)
                overridePendingTransition(0, 0) // Disable animations
                intent.putExtra("REF_NUM", refNumText) // Pass Reference Number
                startActivity(intent)
                overridePendingTransition(0, 0) // Disable animations
            } else {
                Toast.makeText(this, "Reference number not found!", Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun History(view: View) {
        val intent = Intent(this, ReservationsHistoryActivity::class.java)
        startActivity(intent)
        finish() // Finish current activity immediately
        overridePendingTransition(0, 0) // Remove transition animation
    }
}