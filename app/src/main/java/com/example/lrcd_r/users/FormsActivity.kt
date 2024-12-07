package com.example.lrcd_r.users

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import com.example.lrcd_r.R
import com.example.lrcd_r.databinding.ActivityFormsBinding

class FormsActivity : DrawerBaseActivity() {

    lateinit var activityFormsBinding: ActivityFormsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forms) // Provide layout resource ID
        activityFormsBinding = ActivityFormsBinding.inflate(layoutInflater)
        enableEdgeToEdge()
    }
    fun btn_forms_submit(view: View){
        val intent = Intent(this, ConfirmationActivity::class.java)
        startActivity(intent)
    }
}