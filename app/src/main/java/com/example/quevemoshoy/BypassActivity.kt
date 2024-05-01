package com.example.quevemoshoy

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

class BypassActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_bypass)
        startActivity(Intent(this, LoadActivity::class.java))
        finish()
    }
}