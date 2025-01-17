package com.example.lrcd_r.admin

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.icu.util.Calendar
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.core.content.ContextCompat
import com.example.lrcd_r.R
import com.example.lrcd_r.databinding.ActivityAdminHomepageBinding
import java.text.SimpleDateFormat
import java.util.Locale

class AdminHomepage : AdminDrawerBaseActivity() {

    private lateinit var adminHomepageBinding: ActivityAdminHomepageBinding
    private lateinit var datePickerDialog: DatePickerDialog
    private lateinit var startTimePickerDialog: TimePickerDialog
    private lateinit var endTimePickerDialog: TimePickerDialog
    private lateinit var availabilityLayout: LinearLayout //
    private lateinit var btnDate: Button
    private lateinit var btnTimeStart: Button
    private lateinit var btnTimeEnd: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_homepage) // Provide layout resource ID
        adminHomepageBinding = ActivityAdminHomepageBinding.inflate(layoutInflater)
        enableEdgeToEdge()

        // Initialize buttons manually using findViewById
        btnDate = findViewById(R.id.btnDate)
        btnTimeStart = findViewById(R.id.btnTimeStart)
        btnTimeEnd = findViewById(R.id.btnTimeEnd)

        setupDatePicker()
        setupTimePickers()

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

    private fun setupDatePicker() {
        val today = Calendar.getInstance()
        val currentYear = today.get(Calendar.YEAR)
        val year = today.get(Calendar.YEAR)
        val month = today.get(Calendar.MONTH)
        val day = today.get(Calendar.DAY_OF_MONTH)

        datePickerDialog = DatePickerDialog(
            this,
            { _, selectedYear, selectedMonth, selectedDay ->
                val calendar = Calendar.getInstance()
                calendar.set(selectedYear, selectedMonth, selectedDay)

                // Determine the format based on the year
                val dateFormat = if (selectedYear == currentYear) {
                    SimpleDateFormat("EEEE, MMMM dd", Locale.getDefault()) // Same year
                } else {
                    SimpleDateFormat("MMMM dd, yyyy", Locale.getDefault()) // Different year
                }

                val selectedDate = dateFormat.format(calendar.time)
                btnDate.text = selectedDate // Update button text
            },
            year,
            month,
            day
        )
    }

    private fun setupTimePickers() {
        val timeFormat = SimpleDateFormat("h:mm a", Locale.getDefault()) // 12-hour format with AM/PM

        startTimePickerDialog = TimePickerDialog(
            this,
            { _, hourOfDay, minute ->
                val calendar = Calendar.getInstance()
                calendar.set(Calendar.HOUR_OF_DAY, hourOfDay)
                calendar.set(Calendar.MINUTE, minute)

                val selectedTime = timeFormat.format(calendar.time)
                btnTimeStart.text = selectedTime // Update button text
            },
            12,
            0,
            false
        )

        endTimePickerDialog = TimePickerDialog(
            this,
            { _, hourOfDay, minute ->
                val calendar = Calendar.getInstance()
                calendar.set(Calendar.HOUR_OF_DAY, hourOfDay)
                calendar.set(Calendar.MINUTE, minute)

                val selectedTime = timeFormat.format(calendar.time)
                btnTimeEnd.text = selectedTime // Update button text
            },
            12,
            0,
            false
        )
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