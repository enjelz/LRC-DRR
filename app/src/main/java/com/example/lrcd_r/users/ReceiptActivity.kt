package com.example.lrcd_r.users

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import com.example.lrcd_r.R
import com.example.lrcd_r.databinding.ActivityReceiptBinding

class ReceiptActivity : DrawerBaseActivity() {

    private lateinit var activityReceiptBinding: ActivityReceiptBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_receipt) // Provide layout resource ID
        activityReceiptBinding = ActivityReceiptBinding.inflate(layoutInflater)
        enableEdgeToEdge()

        // Retrieve REF_NO from intent
        val refNo = intent.getStringExtra("REF_NO") ?: "N/A"

        // Display REF_NO in the receipt screen
        val refNoTextView = findViewById<TextView>(R.id.receipt_ref_number)
        refNoTextView.text = refNo
    }
    fun btn_receipt_home(view: View){
        val intent = Intent(this, Reserve::class.java)
        startActivity(intent)
    }
}