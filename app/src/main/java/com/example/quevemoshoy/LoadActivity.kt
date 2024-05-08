package com.example.quevemoshoy

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import androidx.appcompat.app.AppCompatActivity
import com.example.quevemoshoy.main.MainActivity2
import com.google.firebase.auth.FirebaseAuth

class LoadActivity : AppCompatActivity() {

    private lateinit var mAuth: FirebaseAuth
    private val handler = Handler()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)
        supportActionBar?.hide()
        mAuth = FirebaseAuth.getInstance()
    }

    override fun onResume() {
        super.onResume()
        handler.postDelayed({ checkUserLoginStatus() }, 3000)
    }

    private fun checkUserLoginStatus() {
        mAuth.addAuthStateListener { firebaseAuth ->
            if (firebaseAuth.currentUser != null) {
                startMainActivity()
            } else {
                startLoginActivity()
            }
        }
    }

    private fun startMainActivity() {
        val intent = Intent(this, MainActivity2::class.java)
        startActivity(intent)
        finish()
    }

    private fun startLoginActivity() {
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
        finish()
    }
}
