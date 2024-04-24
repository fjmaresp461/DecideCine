package com.example.quevemoshoy

import android.net.Uri
import android.os.Bundle
import android.widget.MediaController
import androidx.appcompat.app.AppCompatActivity
import com.example.quevemoshoy.databinding.ActivityAboutAppBinding


class AboutAppActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAboutAppBinding
    private lateinit var mediaController: MediaController
    private var posicion = 0


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAboutAppBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()
        val videoPath = "android.resource://" + packageName + "/" + R.raw.video_ejemplo
        val uri = Uri.parse(videoPath)
        mediaController = MediaController(this)
        binding.videoView.setMediaController(mediaController)
        binding.videoView.setVideoURI(uri)
        binding.ibPlay.setOnClickListener {
            binding.videoView.start()
        }
        setListener()
    }

    private fun setListener() {
        binding.ibAboutBack.setOnClickListener {
            finish()
        }

    }


    override fun onPause() {
        super.onPause()
        posicion = binding.videoView.currentPosition
    }


    override fun onResume() {
        super.onResume()
        if (posicion != 0) {
            binding.videoView.seekTo(posicion)
        }
    }
}
