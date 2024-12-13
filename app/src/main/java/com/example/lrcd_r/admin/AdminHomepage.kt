package com.example.lrcd_r.admin

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.icu.util.Calendar
import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.compose.ui.graphics.Color
import androidx.core.content.ContextCompat
import androidx.core.graphics.red
import com.example.lrcd_r.R
import com.example.lrcd_r.databinding.ActivityAdminHomepageBinding

class AdminHomepage : AdminDrawerBaseActivity() {

    private lateinit var adminHomepageBinding: ActivityAdminHomepageBinding
    private lateinit var datePickerDialog: DatePickerDialog
    private lateinit var startTimePickerDialog: TimePickerDialog
    private lateinit var endTimePickerDialog: TimePickerDialog
    private lateinit var availabilityLayout: LinearLayout //

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_homepage) // Provide layout resource ID
        adminHomepageBinding = ActivityAdminHomepageBinding.inflate(layoutInflater)
        enableEdgeToEdge()

        // Calendar Picker
        val today = Calendar.getInstance()
        val year = today.get(Calendar.YEAR)
        val month = today.get(Calendar.MONTH)
        val day = today.get(Calendar.DAY_OF_MONTH)

        datePickerDialog = DatePickerDialog(
            this,
            { _, selectedYear, selectedMonth, selectedDay ->
                val selectedDate = "$selectedDay/${selectedMonth + 1}/$selectedYear"
                adminHomepageBinding.btnDate.text = selectedDate
            },
            year,
            month,
            day
        )

        // Time Picker
        startTimePickerDialog = TimePickerDialog(
            this,
            { _, hourOfDay, minute ->
                val selectedTime = String.format("%02d:%02d", hourOfDay, minute)
                adminHomepageBinding.btnTimeStart.text = selectedTime
            },
            12, // Default hour
            0, // Default minute
            false // Use 24-hour format
        )

        endTimePickerDialog = TimePickerDialog(
            this,
            { _, hourOfDay, minute ->
                val selectedTime = String.format("%02d:%02d", hourOfDay, minute)
                adminHomepageBinding.btnTimeEnd.text = selectedTime
            },
            12, // Default hour
            0, // Default minute
            false // Use 24-hour format
        )

        //availability visibility
        // Initialize availabilityLayout
        availabilityLayout = findViewById(R.id.availability)

        // Set text color based on availability
        val availabilityTextViews = listOf(
            findViewById<TextView>(R.id.textView18),
            findViewById<TextView>(R.id.textView19),
            findViewById<TextView>(R.id.textView20),
            findViewById<TextView>(R.id.textView21)
        )

        for (textView in availabilityTextViews) {
            when (textView.text) {
                "Available" -> textView.setTextColor(ContextCompat.getColor(this, R.color.confirm))
                "Unnavailable" -> textView.setTextColor(ContextCompat.getColor(this, R.color.cancel))
            }
        }
    }

    fun btnDate(view: View){
        datePickerDialog.show()
    }

    fun btnTimeStart(view: View){
        startTimePickerDialog.show()
    }

    fun btnTimeEnd(view: View){
        endTimePickerDialog.show()
    }

    fun btnCheckAvail(view: View) {
        availabilityLayout.visibility = View.VISIBLE
    }
}