package com.example.quevemoshoy.register

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.SeekBar
import androidx.core.graphics.drawable.toBitmap
import com.airbnb.lottie.LottieCompositionFactory
import com.airbnb.lottie.LottieDrawable
import com.example.quevemoshoy.R
import com.example.quevemoshoy.databinding.ActivityRegister2Binding
import com.example.quevemoshoy.model.UserPreferences
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.lang.reflect.Type



/**
 * `RegisterActivity2` es una actividad que permite al usuario gestionar sus preferencias de género durante el proceso de registro.
 *
 * Esta actividad proporciona una interfaz para que el usuario ajuste sus preferencias de género y las guarda en las preferencias compartidas.
 *
 * @property binding Enlace de la actividad con su vista.
 * @property userPreferences Las preferencias de género del usuario.
 *
 * @constructor Crea una instancia de `RegisterActivity2`.
 */
class RegisterActivity2 : AppCompatActivity() {
    private lateinit var binding: ActivityRegister2Binding
    private val userPreferences = UserPreferences()

    /**
     * Se llama cuando se crea la actividad. Inicializa la vista, establece los oyentes, carga las preferencias y carga el fragmento del stepper.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegister2Binding.inflate(layoutInflater)
        setContentView(binding.root)
        setListener()
        loadPreferences()
        loadStepperFragment()
        supportActionBar?.hide()

    }

    /**
     * Carga las preferencias del usuario desde las preferencias compartidas y actualiza el progreso de las barras de búsqueda.
     */
    private fun loadPreferences() {
        val preferences = getSharedPreferences("Registro", MODE_PRIVATE)
        val jsonString = preferences.getString("preferencias", "")
        if (!jsonString.isNullOrEmpty()) {
            val type: Type = object : TypeToken<Map<String, Int>>() {}.type
            val preferencias: Map<String, Int> = Gson().fromJson(jsonString, type)
            userPreferences.genres = preferencias
            updateSeekBarProgress()
        }
    }

    /**
     * Actualiza el progreso de las barras de búsqueda basándose en las preferencias del usuario.
     */
    private fun updateSeekBarProgress() {
        for ((genreId, progress) in userPreferences.genres) {
            findSeekBarByGenreId(genreId)?.progress = progress
        }
    }

    /**
     * Carga el fragmento del stepper en la actividad.
     */
    private fun loadStepperFragment() {
        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        val myFragment = StepperFragment.newInstance(1)
        fragmentTransaction.add(R.id.fragmentContainerView, myFragment)
        fragmentTransaction.commit()
    }

    /**
     * Establece los oyentes para los botones de la interfaz.
     */
    private fun setListener() {
        binding.btnBack.setOnClickListener {
            startActivity(Intent(this, RegisterActivity1::class.java))
            checkAndSave()
            this.overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
        }
        binding.btnNext.setOnClickListener {
            checkAndSave()

            startActivity(Intent(this, RegisterActivity3::class.java))
            this.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
        }

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
    private fun checkAndSave() {
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

        val preferences = getSharedPreferences("Registro", MODE_PRIVATE)
        val editor = preferences.edit()
        editor.putString("preferencias", Gson().toJson(userPreferences.genres))
        editor.apply()


    }


    /**
     * Se llama cuando se pausa la actividad. Comprueba y guarda las preferencias del usuario.
     */
    override fun onPause() {
        super.onPause()

        checkAndSave()
    }

    /**
     * Se llama cuando se presiona el botón de retroceso. Redirige al usuario a la actividad `RegisterActivity1` y comprueba y guarda las preferencias del usuario.
     */
    @Deprecated("deprecated")
    override fun onBackPressed() {
        super.onBackPressed()
        startActivity(Intent(this, RegisterActivity1::class.java))
        checkAndSave()
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
    }
}