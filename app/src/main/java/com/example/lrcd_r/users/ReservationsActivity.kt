package com.example.lrcd_r.users

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import com.example.lrcd_r.R
import com.example.lrcd_r.databinding.ActivityReservationsBinding

class ReservationsActivity :  DrawerBaseActivity() {

    lateinit var activityReservationsBinding: ActivityReservationsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reservations) // Provide layout resource ID
        activityReservationsBinding = ActivityReservationsBinding.inflate(layoutInflater)
        enableEdgeToEdge()
    }
    fun reservations (view: View) {
        val intent = Intent(this, ReservationDetailsActivity::class.java)
        startActivity(intent)
    }
}