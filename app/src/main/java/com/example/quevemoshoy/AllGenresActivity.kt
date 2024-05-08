package com.example.quevemoshoy

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.quevemoshoy.databinding.ActivityAllGenresBinding
import com.example.quevemoshoy.main.MainActivity2
import com.example.quevemoshoy.model.MoviesManager
import com.example.quevemoshoy.provider.ApiClient
import com.example.quevemoshoy.provider.MovieInterface

/**
 * `AllGenresActivity` es una actividad que muestra todas las categorías de géneros disponibles.
 *
 * Esta actividad proporciona una interfaz para que el usuario seleccione un género y vea las películas correspondientes.
 *
 * @property binding Enlace de la actividad con su vista.
 * @property apiService El servicio de la API de películas.
 * @property movieManager El gestor de películas para interactuar con la API de películas.
 *
 * @constructor Crea una instancia de `AllGenresActivity`.
 */
class AllGenresActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAllGenresBinding
    private val apiService = ApiClient.retrofit.create(MovieInterface::class.java)
    private val movieManager = MoviesManager()

    /**
     * Se llama cuando se crea la actividad. Inicializa la vista y establece los oyentes.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAllGenresBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()
        setListeners()

    }

    /**
     * Establece los oyentes para los contenedores de la interfaz.
     */
    private fun setListeners() {
        binding.cntAction.setOnClickListener {
            movieManager.fetchAndStartActivity(this, "28")
        }
        binding.cntAnimation.setOnClickListener {
            movieManager.fetchAndStartActivity(this, "16")
        }
        binding.cntAdventure.setOnClickListener {
            movieManager.fetchAndStartActivity(this, "12")
        }
        binding.cntComedy.setOnClickListener {
            movieManager.fetchAndStartActivity(this, "35")
        }
        binding.cntCrime.setOnClickListener {
            movieManager.fetchAndStartActivity(this, "80")
        }
        binding.cntDocu.setOnClickListener {
            movieManager.fetchAndStartActivity(this, "99")
        }
        binding.cntDrama.setOnClickListener {
            movieManager.fetchAndStartActivity(this, "18")
        }
        binding.cntFamiliar.setOnClickListener {
            movieManager.fetchAndStartActivity(this, "10751")
        }
        binding.cntHistory.setOnClickListener {
            movieManager.fetchAndStartActivity(this, "36")
        }
        binding.cntHorror.setOnClickListener {
            movieManager.fetchAndStartActivity(this, "27")
        }
        binding.cntMusic.setOnClickListener {
            movieManager.fetchAndStartActivity(this, "10402")
        }
        binding.cntMistery.setOnClickListener {
            movieManager.fetchAndStartActivity(this, "9648")
        }
        binding.cntRomantic.setOnClickListener {
            movieManager.fetchAndStartActivity(this, "10749")
        }
        binding.cntScifi.setOnClickListener {
            movieManager.fetchAndStartActivity(this, "878")
        }
        binding.cntTv.setOnClickListener {
            movieManager.fetchAndStartActivity(this, "10770")
        }
        binding.cntThriller.setOnClickListener {
            movieManager.fetchAndStartActivity(this, "53")
        }
        binding.cntWar.setOnClickListener {
            movieManager.fetchAndStartActivity(this, "10752")
        }
        binding.cntWestern.setOnClickListener {
            movieManager.fetchAndStartActivity(this, "37")
        }
    }

    /**
     * Se llama cuando se presiona el botón de retroceso. Inicia la actividad `MainActivity2`.
     */
    override fun onBackPressed() {
        super.onBackPressed()
        startActivity(Intent(this, MainActivity2::class.java))

    }


}