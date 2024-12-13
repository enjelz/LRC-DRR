package com.example.lrcd_r.admin

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.lrcd_r.R
import com.example.lrcd_r.databinding.ActivityAdminHistoryDetailsBinding
import com.example.lrcd_r.databinding.ActivityAdminReservationHistoryBinding

class AdminHistoryDetails : AdminDrawerBaseActivity() {

    private lateinit var adminHistoryDetailsBinding: ActivityAdminHistoryDetailsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_history_details) // Provide layout resource ID
        adminHistoryDetailsBinding = ActivityAdminHistoryDetailsBinding.inflate(layoutInflater)
        enableEdgeToEdge()
    }
    fun H1Back (view: View){
        startActivity(Intent(this, AdminReservationHistoryActivity::class.java))
        overridePendingTransition(0, 0)
    }
}