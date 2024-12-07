package com.example.lrcd_r.users

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import com.example.lrcd_r.R
import com.example.lrcd_r.databinding.ActivityRemindersBinding

class RemindersActivity : DrawerBaseActivity() {

    lateinit var activityRemindersBinding: ActivityRemindersBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reminders) // Provide layout resource ID
        activityRemindersBinding = ActivityRemindersBinding.inflate(layoutInflater)
        enableEdgeToEdge()
    }
    fun btn_reminders_reserve(view: View){
        val intent = Intent(this, ReceiptActivity::class.java)
        startActivity(intent)
    }
}