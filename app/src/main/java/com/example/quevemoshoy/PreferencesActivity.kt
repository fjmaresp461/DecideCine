package com.example.quevemoshoy

import android.content.Intent
import android.os.Bundle
import android.widget.SeekBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.quevemoshoy.databinding.ActivityPreferencesBinding
import com.example.quevemoshoy.main.MainActivity2
import com.example.quevemoshoy.model.MoviesManager
import com.example.quevemoshoy.model.UserPreferences
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


/**
 * `PreferencesActivity` es una actividad que permite al usuario gestionar sus preferencias de género.
 *
 * Esta actividad proporciona una interfaz para que el usuario ajuste sus preferencias de género y las guarda en la base de datos de Firebase.
 *
 * @property binding Enlace de la actividad con su vista.
 * @property userPreferences Las preferencias de género del usuario.
 * @property userId El ID del usuario actual.
 * @property userName El nombre del usuario actual.
 *
 * @constructor Crea una instancia de `PreferencesActivity`.
 */
class PreferencesActivity : AppCompatActivity() {
    private lateinit var binding: ActivityPreferencesBinding
    private val userPreferences = UserPreferences()
    private val userId = FirebaseAuth.getInstance().currentUser?.uid
    private var userName = ""

    /**
     * Se llama cuando se crea la actividad. Inicializa la vista, las preferencias del usuario, el ID del usuario y el nombre del usuario.
     */

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPreferencesBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()
        setListener()
        val isFirstTime = intent.getBooleanExtra("firstTime", false)
        userName = intent.getStringExtra("user").toString()
        if (!isFirstTime) {
            if (userId != null) {
                loadUserPreferencesFromFirebase(userId, userName)
            }
        }


    }


    /**
     * Establece el oyente para el botón de guardar.
     */
    private fun setListener() {
        binding.btnSave.setOnClickListener {
            checkValues()
            Toast.makeText(
                this, R.string.preferences_saved, Toast.LENGTH_LONG
            ).show()
            CoroutineScope(Dispatchers.IO).launch {
                updateMovies()
            }
            val intent = Intent(this, MainActivity2::class.java)
            intent.putExtra("preferencesChanged", true)
            startActivity(intent)
        }
    }

    /**
     * Actualiza las películas en caché.
     */
    suspend fun updateMovies() {
        MoviesManager.moviesCache = MoviesManager().fetchMoviesByGenreAndProvider()
        MoviesManager.latestMoviesCache = MoviesManager().fetchMovies("latest")
    }

    /**
     * Guarda las preferencias del usuario en la base de datos de Firebase.
     *
     * @param userId El ID del usuario.
     * @param userName El nombre del usuario.
     */
    private fun savePreferencesToFirebase(userId: String, userName: String) {
        val database = FirebaseDatabase.getInstance()
        val reference = database.getReference("users/$userId/preferencias/$userName")
        reference.setValue(userPreferences.genres)
    }

    /**
     * Carga las preferencias del usuario desde la base de datos de Firebase.
     *
     * @param userId El ID del usuario.
     * @param userName El nombre del usuario.
     */
    private fun loadUserPreferencesFromFirebase(userId: String, userName: String) {
        val database = FirebaseDatabase.getInstance()
        val reference = database.getReference("users/$userId/preferencias/$userName")
        reference.get().addOnSuccessListener { dataSnapshot ->
            val userGenrePreferences = mutableMapOf<String, Int>()
            for (genreSnapshot in dataSnapshot.children) {
                val genreId = genreSnapshot.key ?: continue
                val genrePreference = genreSnapshot.getValue(Int::class.java) ?: continue
                userGenrePreferences[genreId] = genrePreference
            }
            for ((genreId, genrePreference) in userGenrePreferences) {
                val seekBar = findSeekBarByGenreId(genreId)
                seekBar?.progress = genrePreference
            }
        }.addOnFailureListener {}
    }

    /**
     * Encuentra una barra de búsqueda por el ID del género.
     *
     * @param genreId El ID del género.
     * @return La barra de búsqueda correspondiente al ID del género.
     */
    private fun findSeekBarByGenreId(genreId: String): SeekBar? {
        return when (genreId) {
            "28" -> binding.actionSeekBar
            "12" -> binding.adventureSeekBar
            "16" -> binding.animationSeekBar
            "35" -> binding.comedySeekBar
            "80" -> binding.crimeSeekBar
            "99" -> binding.documentarySeekBar
            "18" -> binding.dramaSeekBar
            "10751" -> binding.familySeekBar
            "14" -> binding.fantasySeekBar
            "36" -> binding.historySeekBar
            "27" -> binding.horrorSeekBar
            "10402" -> binding.musicSeekBar
            "9648" -> binding.mysterySeekBar
            "10749" -> binding.romanceSeekBar
            "878" -> binding.scifiSeekBar
            "10770" -> binding.tvMovieSeekBar
            "53" -> binding.thrillerSeekBar
            "10752" -> binding.warSeekBar
            "37" -> binding.westernSeekBar
            else -> null
        }
    }

    /**
     * Comprueba los valores de las barras de búsqueda y los guarda en las preferencias del usuario.
     */
    private fun checkValues() {
        val genresMap = mutableMapOf<String, Int>()
        genresMap["28"] = binding.actionSeekBar.progress
        genresMap["12"] = binding.adventureSeekBar.progress
        genresMap["16"] = binding.animationSeekBar.progress
        genresMap["35"] = binding.comedySeekBar.progress
        genresMap["80"] = binding.crimeSeekBar.progress
        genresMap["99"] = binding.documentarySeekBar.progress
        genresMap["18"] = binding.dramaSeekBar.progress
        genresMap["10751"] = binding.familySeekBar.progress
        genresMap["14"] = binding.fantasySeekBar.progress
        genresMap["36"] = binding.historySeekBar.progress
        genresMap["27"] = binding.horrorSeekBar.progress
        genresMap["10402"] = binding.musicSeekBar.progress
        genresMap["9648"] = binding.mysterySeekBar.progress
        genresMap["10749"] = binding.romanceSeekBar.progress
        genresMap["878"] = binding.scifiSeekBar.progress
        genresMap["10770"] = binding.tvMovieSeekBar.progress
        genresMap["53"] = binding.thrillerSeekBar.progress
        genresMap["10752"] = binding.warSeekBar.progress
        genresMap["37"] = binding.westernSeekBar.progress
        userPreferences.genres = genresMap

        if (userId != null) {
            savePreferencesToFirebase(userId, userName)
        }
    }
}
