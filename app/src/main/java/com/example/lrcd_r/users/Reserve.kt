package com.example.lrcd_r.users

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Intent
import android.icu.util.Calendar
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.CheckBox
import android.widget.Toast
import com.example.lrcd_r.R
import com.example.lrcd_r.Schedule
import com.example.lrcd_r.databinding.ActivityReserveBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.text.SimpleDateFormat
import java.util.Locale

class Reserve : DrawerBaseActivity() {

    private lateinit var activityReserveBinding: ActivityReserveBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var databaseReferences: DatabaseReference

    private lateinit var datePickerDialog: DatePickerDialog
    private lateinit var startTimePickerDialog: TimePickerDialog
    private lateinit var endTimePickerDialog: TimePickerDialog
    private lateinit var btnDate: Button
    private lateinit var btnTimeStart: Button
    private lateinit var btnTimeEnd: Button
    private var isReservationMade = false

    private lateinit var checkBox1: CheckBox
    private lateinit var checkBox2: CheckBox
    private lateinit var checkBox3: CheckBox
    private lateinit var checkBox4: CheckBox
    private val selectedRooms: MutableList<String> = mutableListOf()

    private var selectedStartTime: Calendar? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize ViewBinding and set the root layout
        activityReserveBinding = ActivityReserveBinding.inflate(layoutInflater)
        setContentView(R.layout.activity_reserve) // Use the DrawerBaseActivity's setContentView

        //firebase
        firebaseAuth = FirebaseAuth.getInstance()
        databaseReferences = FirebaseDatabase.getInstance().getReference("Users")


        // Initialize buttons manually using findViewById
        btnDate = findViewById(R.id.btnDate)
        btnTimeStart = findViewById(R.id.btnTimeStart)
        btnTimeEnd = findViewById(R.id.btnTimeEnd)

        // Initialize DatePicker and TimePicker
        setupDatePicker()
        setupTimePickers()

        checkBox1 = findViewById(R.id.checkBox1)
        checkBox2 = findViewById(R.id.checkBox2)
        checkBox3 = findViewById(R.id.checkBox3)
        checkBox4 = findViewById(R.id.checkBox4)
    }

    private fun setupDatePicker() {
        val today = Calendar.getInstance()
        today.add(Calendar.DAY_OF_MONTH, 1) // Move to the next day to prevent selecting today

        val year = today.get(Calendar.YEAR)
        val month = today.get(Calendar.MONTH)
        val day = today.get(Calendar.DAY_OF_MONTH)

        datePickerDialog = DatePickerDialog(
            this,
            { _, selectedYear, selectedMonth, selectedDay ->
                val calendar = Calendar.getInstance()
                calendar.set(selectedYear, selectedMonth, selectedDay)

                // Check if the selected day is Sunday
                if (calendar.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY) {
                    Toast.makeText(this, "Reservations are only available Monday to Saturday. Please select a different date.", Toast.LENGTH_SHORT).show()
                    return@DatePickerDialog
                }

                val dateFormat = SimpleDateFormat("MMMM dd, yyyy", Locale.getDefault())
                val selectedDate = dateFormat.format(calendar.time)
                btnDate.text = selectedDate // Update button text
            },
            year, month, day
        )

        // Disable past dates and today's date
        datePickerDialog.datePicker.minDate = today.timeInMillis
    }

    private fun setupTimePickers() {
        val timeFormat = SimpleDateFormat("h:mm a", Locale.getDefault()) // 12-hour format with AM/PM

        startTimePickerDialog = TimePickerDialog(
            this,
            { _, hourOfDay, minute ->
                if (hourOfDay < 8 || hourOfDay >= 17) { // Restrict to 8 AM - 5 PM
                    Toast.makeText(this, "Select a time between 8:00am - 5:00pm.", Toast.LENGTH_SHORT).show()
                    return@TimePickerDialog
                }
                val calendar = Calendar.getInstance()
                calendar.set(Calendar.HOUR_OF_DAY, hourOfDay)
                calendar.set(Calendar.MINUTE, minute)
                selectedStartTime = calendar

                val selectedTime = timeFormat.format(calendar.time)
                btnTimeStart.text = selectedTime // Update button text only
            },
            8,
            0,
            false
        )

        endTimePickerDialog = TimePickerDialog(
            this,
            { _, hourOfDay, minute ->
                if (hourOfDay < 8 || hourOfDay >= 17 || (hourOfDay == 17 && minute > 0)) { // Restrict to 8 AM - 5 PM
                    Toast.makeText(this, "Select a time between 8:00am - 5:00pm.", Toast.LENGTH_SHORT).show()
                    return@TimePickerDialog
                }

                val calendarEnd = Calendar.getInstance()
                calendarEnd.set(Calendar.HOUR_OF_DAY, hourOfDay)
                calendarEnd.set(Calendar.MINUTE, minute)

                // Check if end time is after start time
                if (selectedStartTime != null) {
                    if (calendarEnd.before(selectedStartTime) || calendarEnd == selectedStartTime) {
                        Toast.makeText(
                            this,
                            "End time must be later than start time.",
                            Toast.LENGTH_SHORT
                        ).show()
                        return@TimePickerDialog
                    }
                }

                val selectedTime = timeFormat.format(calendarEnd.time)
                btnTimeEnd.text = selectedTime // Update button text
                checkRoomAvailability() // Only check availability after end time is selected
            },
            17,
            0,
            false
        )
    }


    private fun checkRoomAvailability() {
        val selectedDate = btnDate.text.toString()
        val startTime = btnTimeStart.text.toString()
        val endTime = btnTimeEnd.text.toString()

        if (selectedDate.isEmpty() || startTime.isEmpty() || endTime.isEmpty()) {
            return
        }

        val databaseRef = FirebaseDatabase.getInstance().getReference("Reservations")

        // Force reload of data if a new reservation was made
        if (isReservationMade) {
            // After reservation, reset the flag and re-check availability
            fetchReservations(databaseRef, selectedDate, startTime, endTime)
            isReservationMade = false
        } else {
            fetchReservations(databaseRef, selectedDate, startTime, endTime)
        }
    }

    private fun fetchReservations(databaseRef: DatabaseReference, selectedDate: String, startTime: String, endTime: String) {
        databaseRef.get().addOnSuccessListener { snapshot ->
            if (!snapshot.exists()) {
                // No reservations exist, all rooms are available
                updateUIAvailability(emptyList())
                return@addOnSuccessListener
            }

            val unavailableRooms = mutableListOf<String>()

            for (reservationSnapshot in snapshot.children) {
                val resDate = reservationSnapshot.child("date").getValue(String::class.java) ?: continue
                val status = reservationSnapshot.child("status").getValue(String::class.java)

                // Skip this reservation if it's cancelled
                if (status == "CANCELLED") continue

                if (resDate == selectedDate) { // Check only reservations on the selected date
                    val rooms = reservationSnapshot.child("roomNum").getValue(String::class.java) ?: continue
                    val bookedStart = reservationSnapshot.child("stime").getValue(String::class.java) ?: continue
                    val bookedEnd = reservationSnapshot.child("etime").getValue(String::class.java) ?: continue

                    // Split the room numbers (stored as comma-separated values)
                    val reservedRooms = rooms.split(",").map { it.trim() }

                    // Check if the room conflicts, excluding cancelled reservations
                    for (roomNum in reservedRooms) {
                        if (isTimeConflict(startTime, endTime, bookedStart, bookedEnd)) {
                            unavailableRooms.add(roomNum)
                        }
                    }
                }
            }

            updateUIAvailability(unavailableRooms)
        }.addOnFailureListener {
            Log.e("FirebaseError", "Failed to fetch reservations: ${it.message}")
        }
    }

    private fun isTimeConflict(startTime: String, endTime: String, bookedStart: String, bookedEnd: String): Boolean {
        val formatter = SimpleDateFormat("hh:mm a", Locale.getDefault())

        val selectedStart = formatter.parse(startTime)
        val selectedEnd = formatter.parse(endTime)
        val existingStart = formatter.parse(bookedStart)
        val existingEnd = formatter.parse(bookedEnd)

        return (selectedStart!!.before(existingEnd) && selectedEnd!!.after(existingStart))
    }

    private fun updateUIAvailability(unavailableRooms: List<String>) {
        val checkBoxes = listOf(checkBox1, checkBox2, checkBox3, checkBox4)
        val roomNumbers = listOf("1", "2", "3", "4")

        for (i in checkBoxes.indices) {
            val checkBox = checkBoxes[i]
            val roomNumber = roomNumbers[i]

            if (unavailableRooms.contains(roomNumber)) {
                checkBox.isEnabled = false
                checkBox.text = "Discussion Room $roomNumber (Unavailable)"
                checkBox.isChecked = false // Uncheck if previously selected
            } else {
                checkBox.isEnabled = true
                checkBox.text = "Discussion Room $roomNumber (Available)"
            }
        }
    }

    fun btn_reserve_next(view: View) {
        val uid = firebaseAuth.currentUser?.uid
        val date = btnDate.text.toString()
        val startTime = btnTimeStart.text.toString()
        val endTime = btnTimeEnd.text.toString()

        selectedRooms.clear() // Clear the list to avoid duplicate entries
        if (checkBox1.isChecked) selectedRooms.add("1")
        if (checkBox2.isChecked) selectedRooms.add("2")
        if (checkBox3.isChecked) selectedRooms.add("3")
        if (checkBox4.isChecked) selectedRooms.add("4")

        val rooms: String = selectedRooms.joinToString(", ")

        // Default values (to prevent using unchanged button text as valid input)
        val defaultDate = getString(R.string.default_date_text)
        val defaultStartTime = getString(R.string.default_start_time_text)
        val defaultEndTime = getString(R.string.default_end_time_text)

        // Validate all fields
        if (date == defaultDate || startTime == defaultStartTime || endTime == defaultEndTime || rooms.isEmpty()) {
            Toast.makeText(this, "You should select date, time, and a room.", Toast.LENGTH_SHORT).show()
            return
        } else {

            // Create Intent and pass data to FormsActivity
            val intent = Intent(this, FormsActivity::class.java).apply {
                putExtra("DATE", date)
                putExtra("START_TIME", startTime)
                putExtra("END_TIME", endTime)
                putExtra("ROOMS", rooms)
            }
            startActivity(intent)
            finish()
        }
    }

    fun btnDate(view: View) {
        datePickerDialog.show()
    }

    fun btnTimeStart(view: View) {
        val defaultDate =  getString(R.string.default_date_text)
        if (btnDate.text.toString() == defaultDate){
            Toast.makeText(this, "Please select a date first.", Toast.LENGTH_SHORT).show()
        }else{
            startTimePickerDialog.show()
        }
    }

    fun btnTimeEnd(view: View) {
        val defaultStartTime = getString(R.string.default_start_time_text)
        if (btnTimeStart.text.toString() == defaultStartTime){
            Toast.makeText(this, "Please select start time first.", Toast.LENGTH_SHORT).show()
        } else {
            endTimePickerDialog.show()
        }

    }
}