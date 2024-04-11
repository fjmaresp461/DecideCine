package com.example.quevemoshoy.main

import android.content.Intent
import android.os.Bundle
import android.widget.ImageButton
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.quevemoshoy.R
import com.example.quevemoshoy.authentication.StepperFragment
import com.example.quevemoshoy.databinding.ActivitySettingsBinding

class SettingsActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySettingsBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()
        setAnimations()
    }
    private fun setAnimations() {
        val optionsFragment =
            supportFragmentManager.findFragmentById(R.id.fragmentContainerView2) as OptionsFragment
        optionsFragment.view?.findViewById<ImageButton>(R.id.ib_list)?.setOnClickListener {
            startActivity(Intent(this, MainActivity2::class.java))
            this.overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
        }
        optionsFragment.view?.findViewById<ImageButton>(R.id.ib_users)?.setOnClickListener {
            startActivity(Intent(this, UsersActivity::class.java))
            this.overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
        }
        optionsFragment.view?.findViewById<ImageButton>(R.id.ib_settings)?.isClickable=false
    }
}