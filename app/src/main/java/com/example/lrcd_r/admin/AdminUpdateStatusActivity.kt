package com.example.lrcd_r.admin

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import com.example.lrcd_r.R
import com.example.lrcd_r.databinding.ActivityAdminUpdateStatusBinding

class AdminUpdateStatusActivity : AdminDrawerBaseActivity() {

    private lateinit var adminUpdateStatusBinding: ActivityAdminUpdateStatusBinding
    private lateinit var reservationStatusSpinner: Spinner

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
            // Override the getView method to set the background color and text color
            override fun getView(position: Int, convertView: View?, parent: android.view.ViewGroup): View {
                val view = super.getView(position, convertView, parent)
                val textView = view.findViewById<TextView>(android.R.id.text1)
                textView.setBackgroundColor(getStatusColor(position)) // Set background color
                textView.setTextColor(Color.WHITE) // Set text color to white
                return view
            }

            // Override the getDropDownView method to set the background color and text color for dropdown items
            override fun getDropDownView(position: Int, convertView: View?, parent: android.view.ViewGroup): View {
                val view = super.getDropDownView(position, convertView, parent)
                val textView = view.findViewById<TextView>(android.R.id.text1)
                textView.setBackgroundColor(getStatusColor(position)) // Set background color for the dropdown items
                textView.setTextColor(Color.WHITE) // Set text color to white for dropdown items
                return view
            }
        }

        // Set the adapter to the spinner
        reservationStatusSpinner.adapter = adapter

        // Set a listener for Spinner item selection
        reservationStatusSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                val selectedStatus = parent.getItemAtPosition(position).toString()
                // Change the background color based on the selected status
                // (You might want to remove this line if you're setting the background color in the adapter)
                // view?.setBackgroundColor(getStatusColor(position))
            }

            override fun onNothingSelected(parent: AdapterView<*>) {}
        }
    }

    // Function to get the background color based on the selected status position
    private fun getStatusColor(position: Int): Int {
        return when (position) {
            0 -> Color.parseColor("#a06617") // Orange for Pending
            1 -> Color.parseColor("#265427") // Green for Confirmed
            2 -> Color.parseColor("#3e0606") // Maroon for Cancelled
            3 -> Color.parseColor("#4c4848") // Grey for No Show/Absent
            else -> Color.TRANSPARENT // Default transparent if position is invalid
        }
    }

    fun R1Back (view: View) {
        startActivity(Intent(this, AdminReservationsActivity::class.java))
        overridePendingTransition(0, 0)
    }
}