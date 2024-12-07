package com.example.lrcd_r.users

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import com.example.lrcd_r.R
import com.example.lrcd_r.databinding.ActivityReserveBinding

class Reserve : DrawerBaseActivity() {

    lateinit var activityReserveBinding: ActivityReserveBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reserve) // Provide layout resource ID
        activityReserveBinding = ActivityReserveBinding.inflate(layoutInflater)
        enableEdgeToEdge()
    }
    fun btn_reserve_next(view: View) {
        val intent = Intent(this, FormsActivity::class.java)
        startActivity(intent)
    }
}