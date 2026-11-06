package com.example.connectapp

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.auth.FirebaseAuth

class MainActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        auth = FirebaseAuth.getInstance()

        // Not signed in? Send the user to the login screen.
        if (auth.currentUser == null) {
            goToLogin()
            return
        }

        enableEdgeToEdge()
        setContentView(R.layout.activity_home)
        applyInsets()

        // LOGOUT: sign out of Firebase, then go back to the login screen
        findViewById<ImageButton>(R.id.btnLogout).setOnClickListener {
            auth.signOut()
            Toast.makeText(this, "Signed out", Toast.LENGTH_SHORT).show()
            goToLogin()
        }
    }

    private fun goToLogin() {
        startActivity(Intent(this, LoginActivity::class.java))
        finish()
    }

    // Keeps the top bar, bottom nav and "+" button clear of the system bars
    private fun applyInsets() {
        val topBar = findViewById<View>(R.id.topBar)
        val bottomNav = findViewById<View>(R.id.bottomNav)
        val fab = findViewById<View>(R.id.fabNewPost)

        val topPadding = topBar.paddingTop
        val navHeight = bottomNav.layoutParams.height
        val fabBottomMargin = (fab.layoutParams as ViewGroup.MarginLayoutParams).bottomMargin

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.homeRoot)) { _, insets ->
            val bars = insets.getInsets(WindowInsetsCompat.Type.systemBars())

            topBar.setPadding(topBar.paddingLeft, topPadding + bars.top, topBar.paddingRight, topBar.paddingBottom)

            bottomNav.layoutParams = bottomNav.layoutParams.apply { height = navHeight + bars.bottom }
            bottomNav.setPadding(0, 0, 0, bars.bottom)

            fab.layoutParams = (fab.layoutParams as ViewGroup.MarginLayoutParams).apply {
                bottomMargin = fabBottomMargin + bars.bottom
            }
            insets
        }
    }
}
