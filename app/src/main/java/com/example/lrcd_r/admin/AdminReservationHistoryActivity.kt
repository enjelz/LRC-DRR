package com.example.lrcd_r.admin

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import com.example.lrcd_r.R
import com.example.lrcd_r.databinding.ActivityAdminReservationHistoryBinding

class AdminReservationHistoryActivity : AdminDrawerBaseActivity() {

    private lateinit var adminReservationHistoryBinding: ActivityAdminReservationHistoryBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_reservation_history) // Provide layout resource ID
        adminReservationHistoryBinding = ActivityAdminReservationHistoryBinding.inflate(layoutInflater)
        enableEdgeToEdge()
    }

    fun Upcoming(view: View) {
        startActivity(Intent(this, AdminReservationsActivity::class.java))
        overridePendingTransition(0, 0)
    }

    fun H1(view: View) {
        startActivity(Intent(this, AdminHistoryDetails::class.java))
        overridePendingTransition(0, 0)
    }
}