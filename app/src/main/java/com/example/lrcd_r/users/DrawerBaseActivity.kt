package com.example.lrcd_r.users

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.example.lrcd_r.Login
import com.example.lrcd_r.R
import com.example.lrcd_r.User
import com.example.lrcd_r.databinding.ActivityDrawerBaseBinding
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.StorageReference

open class DrawerBaseActivity : AppCompatActivity(),
    NavigationView.OnNavigationItemSelectedListener {

    private lateinit var auth: FirebaseAuth
    private lateinit var databaseReference: DatabaseReference
    private lateinit var user: User
    private lateinit var uid: String

    private lateinit var toolbar: Toolbar
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navigationView: NavigationView
    lateinit var dialog2: Dialog

    override fun setContentView(layoutResID: Int) {
        super.setContentView(R.layout.activity_drawer_base) // Your base layout
        val contentFrame = findViewById<FrameLayout>(R.id.activityContainer) // Placeholder

        layoutInflater.inflate(
            layoutResID,
            contentFrame,
            true
        ) // Inflate child layout into placeholder

        toolbar = findViewById(R.id.toolbars)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)

        val toolbarLayout = layoutInflater.inflate(R.layout.toolbar_layout, null)
        toolbar.addView(toolbarLayout) //inflate the logo sa toolbar

        menuInflater.inflate(R.menu.main_drawer_menu, toolbar.menu)

        drawerLayout = findViewById(R.id.drawerLayout) // Initialize drawerLayout
        navigationView = findViewById(R.id.nav_view)
        navigationView.setNavigationItemSelectedListener(this)

        val toggle = ActionBarDrawerToggle(
            this,
            drawerLayout,
            toolbar,
            R.string.menu_drawer_open,
            R.string.menu_drawer_close
        )
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        auth = FirebaseAuth.getInstance()
        uid = auth.currentUser?.uid.toString()
        databaseReference = FirebaseDatabase.getInstance().getReference("Users")
        if (uid.isNotEmpty()) {
            getUserData()
        }


        //inflating dialog box
        dialog2 = Dialog(this)
        dialog2.setContentView(R.layout.dialog_logout)
        dialog2.getWindow()
            ?.setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        dialog2.getWindow()?.setBackgroundDrawable(getDrawable(R.drawable.dialog_box))
        dialog2.setCancelable(false)

    }

    private fun getUserData() {

        val currentUser = auth.currentUser  // Get the currently logged-in user
        val currentUserEmail = currentUser?.email  // Retrieve email

        if (currentUserEmail.isNullOrEmpty()) {
            Toast.makeText(this@DrawerBaseActivity, "User not logged in", Toast.LENGTH_SHORT).show()
            return
        }

        databaseReference.orderByChild("email").equalTo(currentUserEmail)
            .addListenerForSingleValueEvent(object : ValueEventListener  {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        for (userSnapshot in snapshot.children) { // Iterate over results
                            user = userSnapshot.getValue(User::class.java)!!
                            // Find the views once and check for null

                            val txtName = findViewById<TextView>(R.id.txt_Name)
                            val txtID = findViewById<TextView>(R.id.txt_ID)

                            if (txtName != null) {
                                val lastName = user.lname ?: "N/A"
                                val givenName = user.gname ?: "N/A"
                                val middleName = user.mname ?: "N/A"
                                val formattedName = "$lastName, $givenName $middleName"
                                txtName.text = formattedName
                            }

                            if (txtID != null) txtID.text = user.id
                            break // Exit loop after first match
                        }
                    } else {
                        Toast.makeText(this@DrawerBaseActivity, "User not found", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(this@DrawerBaseActivity, "Something went wrong", Toast.LENGTH_SHORT)
                        .show()
                }
            })
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        drawerLayout.closeDrawer(GravityCompat.START)

        when (item.itemId) {
            R.id.nav_reserve -> {
                startActivity(Intent(this, Reserve::class.java))
                overridePendingTransition(0, 0)
            }
            R.id.nav_reservations -> {
                startActivity(Intent(this, ReservationsActivity::class.java))
                overridePendingTransition(0, 0)
            }
        }
        return false
    }


    fun btnLogout(view: View) {
        dialog2.show()
    }

    fun btnLogoutYes(view: View) {
        dialog2.dismiss()
        Toast.makeText(this, "Logout Succesfull", Toast.LENGTH_SHORT).show()
        startActivity(Intent(this, Login::class.java))
        overridePendingTransition(0, 0)
    }

    fun btnLogoutNo(view: View) {
        dialog2.dismiss()
    }
}