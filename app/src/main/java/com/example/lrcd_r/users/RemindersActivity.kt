package com.example.lrcd_r.users

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.CheckBox
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import com.example.lrcd_r.R

class RemindersActivity : DrawerBaseActivity() {
    private var refNo: String? = null // Store REF_NO

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reminders) // Keep the layout as is
        enableEdgeToEdge()

        // Retrieve the REF_NO passed from ConfirmationActivity
        refNo = intent.getStringExtra("REF_NO")


    }

    fun btn_reminders_reserve(view: View) {
        val checkBox = findViewById<CheckBox>(R.id.reminders_i_agree)

        if (checkBox.isChecked) {
            val intent = Intent(this, ReceiptActivity::class.java)
            intent.putExtra("REF_NO", refNo) // Pass REF_NO to ReceiptActivity
            startActivity(intent)
        } else {
            Toast.makeText(this, "You must read and agree on the rules and regulations to proceed.", Toast.LENGTH_SHORT).show()
        }
    }

    fun btnBack(view: View) {
        val intent = Intent(this, ConfirmationActivity::class.java)
        startActivity(intent)
    }
}
