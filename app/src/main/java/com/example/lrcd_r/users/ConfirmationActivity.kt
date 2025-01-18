package com.example.lrcd_r.users

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import com.example.lrcd_r.R
import com.example.lrcd_r.databinding.ActivityConfirmationBinding

class ConfirmationActivity : DrawerBaseActivity() {

    lateinit var activityConfirmationBinding: ActivityConfirmationBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_confirmation) // Provide layout resource ID
        activityConfirmationBinding = ActivityConfirmationBinding.inflate(layoutInflater)
        enableEdgeToEdge()
    }
    fun btn_confirmation_confirm(view: View){
        val intent = Intent(this, RemindersActivity::class.java)
        startActivity(intent)
    }
    fun edit_confirmation_details(view: View){
        val intent = Intent(this, FormsActivity::class.java)
        startActivity(intent)
    }

}