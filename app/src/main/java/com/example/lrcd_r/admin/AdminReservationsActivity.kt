package com.example.lrcd_r.admin

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import com.example.lrcd_r.R
import com.example.lrcd_r.databinding.ActivityAdminReservationsBinding

class AdminReservationsActivity : AdminDrawerBaseActivity() {

    private lateinit var adminReservationsBinding: ActivityAdminReservationsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_reservations) // Provide layout resource ID
        adminReservationsBinding = ActivityAdminReservationsBinding.inflate(layoutInflater)
        enableEdgeToEdge()
    }

    fun History(view: View) {
        startActivity(Intent(this, AdminReservationHistoryActivity::class.java))
        overridePendingTransition(0, 0)
    }

    fun R1(view: View) {
        startActivity(Intent(this, AdminUpdateStatusActivity::class.java))
        overridePendingTransition(0, 0)
    }
}