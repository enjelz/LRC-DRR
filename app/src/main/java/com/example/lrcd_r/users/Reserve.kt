package com.example.lrcd_r.users

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Intent
import android.icu.util.Calendar
import android.os.Bundle
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
    private lateinit var storageReference: StorageReference


    private lateinit var datePickerDialog: DatePickerDialog
    private lateinit var startTimePickerDialog: TimePickerDialog
    private lateinit var endTimePickerDialog: TimePickerDialog
    private lateinit var btnDate: Button
    private lateinit var btnTimeStart: Button
    private lateinit var btnTimeEnd: Button

    private lateinit var checkBox1: CheckBox
    private lateinit var checkBox2: CheckBox
    private lateinit var checkBox3: CheckBox
    private lateinit var checkBox4: CheckBox
    private val selectedRooms: MutableList<String> = mutableListOf()

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
        val defaultDate = getString(R.string.default_date_text) // Example: "Select Date"
        val defaultStartTime = getString(R.string.default_start_time_text) // Example: "Start Time"
        val defaultEndTime = getString(R.string.default_end_time_text) // Example: "End Time"

        // Validate all fields
        if (date == defaultDate || startTime == defaultStartTime || endTime == defaultEndTime || rooms.isEmpty()) {
            Toast.makeText(this, "Fields cannot be empty.", Toast.LENGTH_SHORT).show()
            return // Stop execution if validation fails
        } else {
            val schedule = Schedule(date, startTime, endTime, rooms)
            val scheduleRef = FirebaseDatabase.getInstance().getReference("Schedules")
            val scheduleId = scheduleRef.push().key

            if (scheduleId != null) {
                scheduleRef.child(scheduleId).setValue(schedule)
                    .addOnCompleteListener {
                        if (it.isSuccessful) {
                            Toast.makeText(this, "Schedule added successfully", Toast.LENGTH_SHORT).show()
                            startActivity(Intent(this, FormsActivity::class.java))
                            finish()
                        } else {
                            Toast.makeText(this, "Failed to add schedule: ${it.exception?.message}", Toast.LENGTH_SHORT).show()
                        }
                    }
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
}
