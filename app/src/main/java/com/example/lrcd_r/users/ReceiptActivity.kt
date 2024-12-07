package com.example.lrcd_r.users

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import com.example.lrcd_r.R
import com.example.lrcd_r.databinding.ActivityReceiptBinding

class ReceiptActivity : DrawerBaseActivity() {

    lateinit var activityReceiptBinding: ActivityReceiptBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_receipt) // Provide layout resource ID
        activityReceiptBinding = ActivityReceiptBinding.inflate(layoutInflater)
        enableEdgeToEdge()
    }
    fun btn_receipt_home(view: View){
        val intent = Intent(this, Reserve::class.java)
        startActivity(intent)
    }
}