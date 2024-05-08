package com.example.quevemoshoy


import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.quevemoshoy.databinding.ActivityAboutAppBinding


class AboutAppActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAboutAppBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAboutAppBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setListener()
    }

    private fun setListener() {
        binding.ibAboutBack.setOnClickListener {
            finish()
        }

    }



}
