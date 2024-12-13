package com.example.lrcd_r.users

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import androidx.activity.enableEdgeToEdge
import com.example.lrcd_r.R
import com.example.lrcd_r.databinding.ActivityFormsBinding

class FormsActivity : DrawerBaseActivity() {

    lateinit var activityFormsBinding: ActivityFormsBinding
    private lateinit var droptype: AutoCompleteTextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forms) // Provide layout resource ID
        activityFormsBinding = ActivityFormsBinding.inflate(layoutInflater)
        enableEdgeToEdge()

        droptype = findViewById(R.id.droptype)

        val userTypes = resources.getStringArray(R.array.user_types)
        val userTypeAdapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, userTypes)
        droptype.setAdapter(userTypeAdapter)

    }

    fun btn_forms_submit(view: View){
        val intent = Intent(this, ConfirmationActivity::class.java)
        startActivity(intent)
    }

}