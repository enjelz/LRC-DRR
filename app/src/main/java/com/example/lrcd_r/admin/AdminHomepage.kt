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

        startTimePickerDialog = TimePickerDialog(
            this,
            { _, hourOfDay, minute ->
                if (hourOfDay < 8 || hourOfDay >= 17) {  // Restrict time from 8AM to 5PM
                    Toast.makeText(this, "Reservations are only allowed between 8 AM and 5 PM.", Toast.LENGTH_SHORT).show()
                    return@TimePickerDialog
                }

                val calendar = Calendar.getInstance()
                calendar.set(Calendar.HOUR_OF_DAY, hourOfDay)
                calendar.set(Calendar.MINUTE, minute)

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
                if (hourOfDay < 8 || hourOfDay >= 17 && minute > 0) {  // Restrict time from 8AM to 5PM
                    Toast.makeText(this, "Reservations are only allowed from 8 AM to 5 PM.", Toast.LENGTH_SHORT).show()
                    return@TimePickerDialog
                }

                val calendar = Calendar.getInstance()
                calendar.set(Calendar.HOUR_OF_DAY, hourOfDay)
                calendar.set(Calendar.MINUTE, minute)

                val selectedTime = timeFormat.format(calendar.time)
                btnTimeEnd.text = selectedTime
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
                val reservedRooms = mutableSetOf<String>()

                for (reservation in snapshot.children) {
                    val date = reservation.child("date").getValue(String::class.java) ?: continue
                    val stime = reservation.child("stime").getValue(String::class.java) ?: continue
                    val etime = reservation.child("etime").getValue(String::class.java) ?: continue
                    val roomNum = reservation.child("roomNum").getValue(String::class.java)

                    if (date == selectedDate && isTimeOverlapping(startTime, endTime, stime, etime)) {
                        roomNum?.split(",")?.map { it.trim() }?.forEach { room -> reservedRooms.add(room) }
                    }
                }

                Log.d("Debug", "Reserved Rooms Set after Firebase fetch: $reservedRooms")
                updateRoomAvailabilityUI(reservedRooms)
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

    private fun updateRoomAvailabilityUI(reservedRooms: Set<String>) {
        val roomTextViews = listOf(
            findViewById<TextView>(R.id.textView18),
            findViewById<TextView>(R.id.textView19),
            findViewById<TextView>(R.id.textView20),
            findViewById<TextView>(R.id.textView21)
        )

        for ((index, textView) in roomTextViews.withIndex()) {
            val roomNumber = (index + 1).toString()

            Log.d("Debug", "Checking room $roomNumber - Reserved: ${reservedRooms.contains(roomNumber)}")

            availabilityLayout.visibility = View.VISIBLE
            if (reservedRooms.contains(roomNumber)) {
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
