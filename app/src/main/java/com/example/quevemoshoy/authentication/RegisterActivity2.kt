package com.example.quevemoshoy.authentication

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.SeekBar
import android.widget.Toast
import com.example.quevemoshoy.MainActivity2
import com.example.quevemoshoy.R
import com.example.quevemoshoy.databinding.ActivityRegister1Binding
import com.example.quevemoshoy.databinding.ActivityRegister2Binding
import com.example.quevemoshoy.model.UserPreferences
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.lang.reflect.Type

class RegisterActivity2 : AppCompatActivity() {
    private lateinit var binding: ActivityRegister2Binding
    private val userPreferences = UserPreferences()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityRegister2Binding.inflate(layoutInflater)
        setContentView(binding.root)
        setListener()

        // Cargar preferencias de SharedPreferences
        val preferences = getSharedPreferences("Registro", MODE_PRIVATE)
        val jsonString = preferences.getString("preferencias", "")
        if (!jsonString.isNullOrEmpty()) {
            val type: Type = object : TypeToken<Map<String, Int>>() {}.type
            val preferencias: Map<String, Int> = Gson().fromJson(jsonString, type)
            userPreferences.genres = preferencias

            // Actualizar el progreso de cada SeekBar
            for ((genreId, progress) in userPreferences.genres) {
                findSeekBarByGenreId(genreId)?.progress = progress
            }
        }
    }


    private fun setListener() {
        binding.btnBack.setOnClickListener{
            finish()
        }
        binding.btnNext.setOnClickListener {
            checkValues()
            Toast.makeText(
                this, R.string.preferences_saved, Toast.LENGTH_LONG
            ).show()
            startActivity(Intent(this, RegisterActivity3::class.java))
        }

    }

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

        // Guardar preferencias en SharedPreferences
        val preferences = getSharedPreferences("Registro", MODE_PRIVATE)
        val editor = preferences.edit()
        editor.putString("preferencias",  Gson().toJson(userPreferences.genres))
        editor.apply()
    }

    private fun loadUserPreferencesFromFirebase(userId: String) {
        val database = FirebaseDatabase.getInstance()
        val reference = database.getReference("preferences/$userId")
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
        }.addOnFailureListener {
        }
    }
}