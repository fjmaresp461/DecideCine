package com.example.quevemoshoy

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.quevemoshoy.adapter.GenreAdapter
import com.example.quevemoshoy.databinding.ActivityAllGenresBinding
import com.example.quevemoshoy.main.MainActivity2
import com.example.quevemoshoy.model.Movie
import com.example.quevemoshoy.model.MoviesManager
import com.example.quevemoshoy.provider.ApiClient
import com.example.quevemoshoy.provider.MovieInterface
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class AllGenresActivity : AppCompatActivity() {
    private lateinit var binding:ActivityAllGenresBinding
    private val apiService = ApiClient.retrofit.create(MovieInterface::class.java)
    private val movieManager = MoviesManager()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivityAllGenresBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()
        setListeners()

    }

    private fun setListeners() {
        binding.cntAction.setOnClickListener {
            movieManager.fetchAndStartActivity(this,"28")
        }
        binding.cntAnimation.setOnClickListener {
            movieManager.fetchAndStartActivity(this,"16")
        }
        binding.cntAdventure.setOnClickListener {
            movieManager.fetchAndStartActivity(this,"12")
        }
        binding.cntComedy.setOnClickListener {
            movieManager.fetchAndStartActivity(this,"35")
        }
        binding.cntCrime.setOnClickListener {
            movieManager.fetchAndStartActivity(this,"80")
        }
        binding.cntDocu.setOnClickListener {
            movieManager.fetchAndStartActivity(this,"99")
        }
        binding.cntDrama.setOnClickListener {
            movieManager.fetchAndStartActivity(this,"18")
        }
        binding.cntFamiliar.setOnClickListener {
            movieManager.fetchAndStartActivity(this,"10751")
        }
        binding.cntHistory.setOnClickListener {
            movieManager.fetchAndStartActivity(this,"36")
        }
        binding.cntHorror.setOnClickListener {
            movieManager.fetchAndStartActivity(this,"27")
        }
        binding.cntMusic.setOnClickListener {
            movieManager.fetchAndStartActivity(this,"10402")
        }
        binding.cntMistery.setOnClickListener {
            movieManager.fetchAndStartActivity(this,"9648")
        }
        binding.cntRomantic.setOnClickListener {
            movieManager.fetchAndStartActivity(this,"10749")
        }
        binding.cntScifi.setOnClickListener {
            movieManager.fetchAndStartActivity(this,"878")
        }
        binding.cntTv.setOnClickListener {
            movieManager.fetchAndStartActivity(this,"10770")
        }
        binding.cntThriller.setOnClickListener {
            movieManager.fetchAndStartActivity(this,"53")
        }
        binding.cntWar.setOnClickListener {
            movieManager.fetchAndStartActivity(this,"10752")
        }
        binding.cntWestern.setOnClickListener {
            movieManager.fetchAndStartActivity(this,"37")
        }
    }


    override fun onBackPressed() {
        super.onBackPressed()
            startActivity(Intent(this, MainActivity2::class.java))

    }



}