package com.example.lrcd_r.users

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.cardview.widget.CardView
import com.example.lrcd_r.R
import com.example.lrcd_r.databinding.ActivityReservationDetailsBinding

class ReservationDetailsActivity : DrawerBaseActivity() {

    lateinit var activityReservationDetailsBinding: ActivityReservationDetailsBinding
    lateinit var dialog1: Dialog
    private lateinit var reservationCancelledCardView: CardView
    private lateinit var btnCancelReservation: Button
    lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reservation_details) // Provide layout resource ID
        activityReservationDetailsBinding = ActivityReservationDetailsBinding.inflate(layoutInflater)
        enableEdgeToEdge()

        //inflating dialog box
        dialog1 = Dialog(this)
        dialog1.setContentView(R.layout.user_dialog_cancel_reservation)
        dialog1.getWindow()
            ?.setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        dialog1.getWindow()?.setBackgroundDrawable(getDrawable(R.drawable.dialog_box))
        dialog1.setCancelable(false)

        reservationCancelledCardView = findViewById(R.id.reservationCancelled)
        btnCancelReservation = findViewById(R.id.btn_cancel_reservation)
        sharedPreferences = getSharedPreferences("visibility_prefs", Context.MODE_PRIVATE)

        val cardViewVisible = sharedPreferences.getBoolean("cardViewVisible", false)
        val buttonVisible = sharedPreferences.getBoolean("buttonVisible", true)

        reservationCancelledCardView.visibility = if (cardViewVisible) View.VISIBLE else View.GONE
        btnCancelReservation.visibility = if (buttonVisible) View.VISIBLE else View.GONE

    }
    fun btn_reservation_details_back(view: View) {
        val intent = Intent(this, ReservationsActivity::class.java)
        startActivity(intent)
    }
    fun btn_reservation_cancel(view: View) {
        dialog1.show()
    }

    fun btnCancelYesClicked(view: View) {
        dialog1.dismiss()

        // Save visibility states in SharedPreferences
        val editor = sharedPreferences.edit()
        editor.putBoolean("cardViewVisible", true)
        editor.putBoolean("buttonVisible", false)
        editor.apply()

        reservationCancelledCardView.visibility = View.VISIBLE
        btnCancelReservation.visibility = View.GONE // Hide the button
    }

    fun btnCancelNoClicked(view: View) {
        dialog1.dismiss()
    }
}