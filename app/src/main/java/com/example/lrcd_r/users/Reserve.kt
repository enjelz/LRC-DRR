package com.example.lrcd_r.users

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Intent
import android.icu.util.Calendar
import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import com.example.lrcd_r.R
import com.example.lrcd_r.databinding.ActivityReserveBinding

class Reserve : DrawerBaseActivity() {

    private lateinit var activityReserveBinding: ActivityReserveBinding
    private lateinit var datePickerDialog: DatePickerDialog
    private lateinit var startTimePickerDialog: TimePickerDialog
    private lateinit var endTimePickerDialog: TimePickerDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reserve) // Provide layout resource ID
        activityReserveBinding = ActivityReserveBinding.inflate(layoutInflater)
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
                activityReserveBinding.btnDate.text = selectedDate
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
                activityReserveBinding.btnTimeStart.text = selectedTime
            },
            12, // Default hour
            0, // Default minute
            false // Use 24-hour format
        )

        endTimePickerDialog = TimePickerDialog(
            this,
            { _, hourOfDay, minute ->
                val selectedTime = String.format("%02d:%02d", hourOfDay, minute)
                activityReserveBinding.btnTimeEnd.text = selectedTime
            },
            12, // Default hour
            0, // Default minute
            false // Use 24-hour format
        )

    }
    fun btn_reserve_next(view: View) {
        val intent = Intent(this, FormsActivity::class.java)
        startActivity(intent)
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


}