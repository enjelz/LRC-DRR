package com.example.lrcd_r.users

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Intent
import android.icu.util.Calendar
import android.os.Bundle
import android.view.View
import android.widget.Button
import com.example.lrcd_r.R
import com.example.lrcd_r.databinding.ActivityReserveBinding
import java.text.SimpleDateFormat
import java.util.Locale

class Reserve : DrawerBaseActivity() {

    private lateinit var activityReserveBinding: ActivityReserveBinding
    private lateinit var datePickerDialog: DatePickerDialog
    private lateinit var startTimePickerDialog: TimePickerDialog
    private lateinit var endTimePickerDialog: TimePickerDialog
    private lateinit var btnDate: Button
    private lateinit var btnTimeStart: Button
    private lateinit var btnTimeEnd: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize ViewBinding and set the root layout
        activityReserveBinding = ActivityReserveBinding.inflate(layoutInflater)
        setContentView(R.layout.activity_reserve) // Use the DrawerBaseActivity's setContentView

        // Initialize buttons manually using findViewById
        btnDate = findViewById(R.id.btnDate)
        btnTimeStart = findViewById(R.id.btnTimeStart)
        btnTimeEnd = findViewById(R.id.btnTimeEnd)

        // Initialize DatePicker and TimePicker
        setupDatePicker()
        setupTimePickers()
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

    fun btn_reserve_next(view: View) {
        val intent = Intent(this, FormsActivity::class.java)
        startActivity(intent)
    }

    fun btnDate(view: View) {
        datePickerDialog.show()
    }

    fun btnTimeStart(view: View) {
        startTimePickerDialog.show()
    }

    fun btnTimeEnd(view: View) {
        endTimePickerDialog.show()
    }
}
