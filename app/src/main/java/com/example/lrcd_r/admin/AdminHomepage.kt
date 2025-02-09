package com.example.lrcd_r.admin

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.icu.util.Calendar
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.core.content.ContextCompat
import com.example.lrcd_r.R
import com.example.lrcd_r.databinding.ActivityAdminHomepageBinding
import java.text.SimpleDateFormat
import java.util.Locale
import com.google.firebase.database.*
import android.util.Log

class AdminHomepage : AdminDrawerBaseActivity() {

    private lateinit var adminHomepageBinding: ActivityAdminHomepageBinding
    private lateinit var datePickerDialog: DatePickerDialog
    private lateinit var startTimePickerDialog: TimePickerDialog
    private lateinit var endTimePickerDialog: TimePickerDialog
    private lateinit var availabilityLayout: LinearLayout
    private lateinit var btnDate: Button
    private lateinit var btnTimeStart: Button
    private lateinit var btnTimeEnd: Button
    private var isReservationMade = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_homepage)
        adminHomepageBinding = ActivityAdminHomepageBinding.inflate(layoutInflater)
        enableEdgeToEdge()

        // Initialize buttons manually
        btnDate = findViewById(R.id.btnDate)
        btnTimeStart = findViewById(R.id.btnTimeStart)
        btnTimeEnd = findViewById(R.id.btnTimeEnd)

        setupDatePicker()
        setupTimePickers()

        // Initialize availability layout
        availabilityLayout = findViewById(R.id.availability)
    }

    private fun setupDatePicker() {
        val today = Calendar.getInstance()
        val currentYear = today.get(Calendar.YEAR)

        datePickerDialog = DatePickerDialog(
            this,
            { _, selectedYear, selectedMonth, selectedDay ->
                val calendar = Calendar.getInstance()
                calendar.set(selectedYear, selectedMonth, selectedDay)

                // Prevent selection of Sundays
                if (calendar.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY) {
                    Toast.makeText(this, "Reservations are only available Monday to Saturday. Please select a different date.", Toast.LENGTH_LONG).show()
                    return@DatePickerDialog
                }

                val dateFormat = SimpleDateFormat("MMMM dd, yyyy", Locale.getDefault())
                val selectedDate = dateFormat.format(calendar.time)
                btnDate.text = selectedDate
            },
            today.get(Calendar.YEAR),
            today.get(Calendar.MONTH),
            today.get(Calendar.DAY_OF_MONTH)
        )

        // Disable past dates
        datePickerDialog.datePicker.minDate = System.currentTimeMillis() - 1000
    }

    private fun setupTimePickers() {
        val timeFormat = SimpleDateFormat("h:mm a", Locale.getDefault()) // 12-hour format with AM/PM
        var selectedStartTime: Calendar? = null

        startTimePickerDialog = TimePickerDialog(
            this,
            { _, hourOfDay, minute ->
                if (hourOfDay < 8 || hourOfDay >= 17) {  // Restrict time from 8AM to 5PM
                    Toast.makeText(this, "Please select a time from 8:00AM to 5:00PM", Toast.LENGTH_SHORT).show()
                    return@TimePickerDialog
                }

                val calendar = Calendar.getInstance()
                calendar.set(Calendar.HOUR_OF_DAY, hourOfDay)
                calendar.set(Calendar.MINUTE, minute)
                selectedStartTime = calendar

                val selectedTime = timeFormat.format(calendar.time)
                btnTimeStart.text = selectedTime
            },
            8, // Default to 8 AM
            0,
            false
        )

        endTimePickerDialog = TimePickerDialog(
            this,
            { _, hourOfDay, minute ->
                if (hourOfDay < 8 || hourOfDay >= 17 || (hourOfDay == 17 && minute > 0)) {  // Restrict time from 8AM to 5PM
                    Toast.makeText(this, "Please select a time from 8:00AM to 5:00PM", Toast.LENGTH_SHORT).show()
                    return@TimePickerDialog
                }

                val calendarEnd = Calendar.getInstance()
                calendarEnd.set(Calendar.HOUR_OF_DAY, hourOfDay)
                calendarEnd.set(Calendar.MINUTE, minute)

                // Check if end time is after start time
                if (selectedStartTime != null && calendarEnd.before(selectedStartTime)) {
                    Toast.makeText(
                        this,
                        "Please ensure the end time is later than the start time.",
                        Toast.LENGTH_SHORT
                    ).show()
                    return@TimePickerDialog
                }

                val selectedTime = timeFormat.format(calendarEnd.time)
                btnTimeEnd.text = selectedTime
                checkRoomAvailabilityForAdmin() // Check availability after selecting end time
            },
            17, // Default to 5 PM
            0,
            false
        )
    }

    private fun checkRoomAvailabilityForAdmin() {
        val selectedDate = btnDate.text.toString()
        val startTime = btnTimeStart.text.toString()
        val endTime = btnTimeEnd.text.toString()

        if (selectedDate.isEmpty() || startTime.isEmpty() || endTime.isEmpty()) {
            return
        }

        val databaseRef = FirebaseDatabase.getInstance().getReference("Reservations")

        databaseRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                // Track unavailable rooms for each room number
                val roomConflicts = mutableMapOf(
                    "1" to false,
                    "2" to false,
                    "3" to false,
                    "4" to false
                )

                for (reservationSnapshot in snapshot.children) {
                    val date = reservationSnapshot.child("date").getValue(String::class.java)
                    val stime = reservationSnapshot.child("stime").getValue(String::class.java)
                    val etime = reservationSnapshot.child("etime").getValue(String::class.java)
                    val roomNum = reservationSnapshot.child("roomNum").getValue(String::class.java)
                    val status = reservationSnapshot.child("status").getValue(String::class.java)

                    // Skip this reservation if it's cancelled
                    if (status == "CANCELLED") continue

                    // Only check conflicts for reservations on the selected date
                    if (date == selectedDate && isTimeOverlapping(startTime, endTime, stime, etime)) {
                        // Mark each room in this reservation as unavailable
                        roomNum?.split(",")?.map { it.trim() }?.forEach { room ->
                            roomConflicts[room] = true
                        }
                    }
                }

                Log.d("Debug", "Room conflicts after checking: $roomConflicts")
                updateRoomAvailabilityUI(roomConflicts)
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("FirebaseError", "Failed to read data: ${error.message}")
            }
        })
    }

    private fun isTimeOverlapping(start1: String, end1: String, start2: String?, end2: String?): Boolean {
        if (start2 == null || end2 == null) return false
        val sdf = SimpleDateFormat("h:mm a", Locale.getDefault()) // Ensure AM/PM is considered
        val time1Start = sdf.parse(start1)?.time ?: return false
        val time1End = sdf.parse(end1)?.time ?: return false
        val time2Start = sdf.parse(start2)?.time ?: return false
        val time2End = sdf.parse(end2)?.time ?: return false
        return time1Start < time2End && time1End > time2Start
    }

    private fun updateRoomAvailabilityUI(roomConflicts: Map<String, Boolean>) {
        val roomTextViews = listOf(
            findViewById<TextView>(R.id.textView18),
            findViewById<TextView>(R.id.textView19),
            findViewById<TextView>(R.id.textView20),
            findViewById<TextView>(R.id.textView21)
        )

        for ((index, textView) in roomTextViews.withIndex()) {
            val roomNumber = (index + 1).toString()
            val isConflict = roomConflicts[roomNumber] ?: false

            availabilityLayout.visibility = View.VISIBLE
            if (isConflict) {
                textView.text = "Unavailable"
                textView.setTextColor(ContextCompat.getColor(this, R.color.cancel))
            } else {
                textView.text = "Available"
                textView.setTextColor(ContextCompat.getColor(this, R.color.confirm))
            }
        }
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

    fun btnCheckAvail(view: View) {
        checkRoomAvailabilityForAdmin()
    }
}
