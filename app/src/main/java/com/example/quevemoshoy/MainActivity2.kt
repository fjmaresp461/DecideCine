package com.example.quevemoshoy

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.quevemoshoy.database.DatabaseManager
import com.example.quevemoshoy.databinding.ActivityMain2Binding
import com.example.quevemoshoy.model.MoviesManager
import com.example.quevemoshoy.viewModel.MoviesViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class MainActivity2 : AppCompatActivity() {
    private lateinit var binding: ActivityMain2Binding
    private val viewModel: MoviesViewModel by viewModels()
    private lateinit var auth: FirebaseAuth
    private val dbManager = DatabaseManager()
    private val moviesManager = MoviesManager()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMain2Binding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.title = ""
        auth = Firebase.auth
        setListeners()
    }

    private fun setListeners() {

        binding.ibRecomended.setOnClickListener {
            lifecycleScope.launch(Dispatchers.IO) {
                val userGenrePreferences = moviesManager.getUserGenrePreferences(7, 10)
                getRecommendedMovies(userGenrePreferences, "recommended")
            }
        }

        binding.ibSurprise.setOnClickListener {
            lifecycleScope.launch(Dispatchers.IO) {
                val userGenrePreferences = moviesManager.getUserGenrePreferences(4, 7)
                getRecommendedMovies(userGenrePreferences, "surprise")
            }
        }

        binding.ibComingSoon.setOnClickListener {

        }
    }


    suspend fun getRecommendedMovies(userGenrePreferences: Map<String, Int>, recommendationType: String) {
        withContext(Dispatchers.IO) {
            try {
                val movies = moviesManager.fetchMovies(userGenrePreferences, recommendationType)
                withContext(Dispatchers.Main) {
                    if (movies.isNotEmpty()) {
                        val intent =
                            Intent(this@MainActivity2, RecyclerActivity::class.java).apply {
                                putExtra("movies", ArrayList(movies))
                            }
                        startActivity(intent)
                    } else {
                        Toast.makeText(this@MainActivity2, R.string.no_matching_movies, Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: Exception) {
                Log.e("MainActivity", R.string.error_fetching.toString(), e)
            }
        }
    }


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_options, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.item_edit_profile_preferences -> {
                val intent = Intent(this, PreferencesActivity::class.java)
                intent.putExtra("skipFragment", false)
                startActivity(intent)
            }

            R.id.item_restore -> {
                AlertDialog.Builder(this)
                    .setTitle("Restaurar a valores predeterminados")
                    .setMessage("¿Estás seguro de que quieres restaurar tus preferencias a los valores predeterminados?")
                    .setPositiveButton("Sí") { _, _ ->
                        dbManager.deleteAll()
                        lifecycleScope.launch {
                            Dispatchers.Main
                            moviesManager.resetPreferences()
                        }

                    }
                    .setNegativeButton("No", null)
                    .show()
            }

            R.id.item_exit -> {
                finishAffinity()
            }

            R.id.item_sign_out -> {
                auth.signOut()
                finish()
                startActivity(Intent(this, LoginActivity::class.java))
            }

            R.id.about_author -> {
                startActivity(Intent(this, AboutAppActivity::class.java))
            }

            R.id.item_map -> {
                startActivity(Intent(this, MapActivity::class.java))
            }
        }
        return super.onOptionsItemSelected(item)
    }
}
