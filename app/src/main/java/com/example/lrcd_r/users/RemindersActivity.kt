package com.example.lrcd_r.users

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.CheckBox
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import com.example.lrcd_r.R
import com.example.lrcd_r.databinding.ActivityRemindersBinding

class RemindersActivity : DrawerBaseActivity() {

    lateinit var activityRemindersBinding: ActivityRemindersBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityRemindersBinding = ActivityRemindersBinding.inflate(layoutInflater)
        setContentView(activityRemindersBinding.root)
        enableEdgeToEdge()
    }

    fun btn_reminders_reserve(view: View) {
        val checkBox = findViewById<CheckBox>(R.id.reminders_i_agree)

        if (checkBox.isChecked) {
            val intent = Intent(this, ReceiptActivity::class.java)
            startActivity(intent)
        } else {
            Toast.makeText(this, "You must agree to proceed.", Toast.LENGTH_SHORT).show()
        }
    }

    fun btnBack(view: View) {
        val intent = Intent(this, ConfirmationActivity::class.java)
        startActivity(intent)
    }
}
