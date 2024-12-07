package com.example.lrcd_r.users

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import com.example.lrcd_r.R
import com.example.lrcd_r.databinding.ActivityReservationDetailsBinding

class ReservationDetailsActivity : DrawerBaseActivity() {

    lateinit var activityReservationDetailsBinding: ActivityReservationDetailsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reservation_details) // Provide layout resource ID
        activityReservationDetailsBinding = ActivityReservationDetailsBinding.inflate(layoutInflater)
        enableEdgeToEdge()
    }
    fun btn_reservation_details_back(view: View) {
        val intent = Intent(this, ReservationsActivity::class.java)
        startActivity(intent)
    }
}