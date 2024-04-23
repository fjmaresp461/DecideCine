package com.example.quevemoshoy.main

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.example.quevemoshoy.AboutAppActivity
import com.example.quevemoshoy.DetailActivity
import com.example.quevemoshoy.LoginActivity
import com.example.quevemoshoy.MapActivity
import com.example.quevemoshoy.PreferencesActivity
import com.example.quevemoshoy.R
import com.example.quevemoshoy.RecyclerActivity
import com.example.quevemoshoy.database.DatabaseManager
import com.example.quevemoshoy.databinding.ActivityMain2Binding
import com.example.quevemoshoy.model.Movie
import com.example.quevemoshoy.model.MoviesManager
import com.example.quevemoshoy.viewModel.MoviesViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class MainActivity2 : AppCompatActivity() {
    private lateinit var binding: ActivityMain2Binding
    private lateinit var auth: FirebaseAuth
    private val dbManager = DatabaseManager()
    private val moviesManager = MoviesManager()
    val currentUser = FirebaseAuth.getInstance().currentUser
    private val PREFS_NAME = "user_prefs"
    private val GENRE_PREFS_KEY = "genre_prefs"
    private val RECOMMENDATION_TYPE_KEY = "recommendation_type"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMain2Binding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = Firebase.auth
        setListeners()
        setAnimations()
        initRecommendations()
    }

    fun initRecommendations() {
        initMovies("recommended", 7, 10, listOf(binding.ivReco1, binding.ivReco2, binding.ivReco3, binding.ivReco4))
        initMovies("surprise", 4, 7, listOf(binding.ivSur1, binding.ivSur2, binding.ivSur3, binding.ivSur4))
        initMovies("latest", 0, 10, listOf(binding.ivLat1, binding.ivLat2, binding.ivLat3, binding.ivLat4))
    }

    fun initMovies(recommendationType: String, minScore: Int, maxScore: Int, imageViews: List<ImageView>) {
        lifecycleScope.launch {
            val sharedPreferences = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            val userGenrePreferences = if (recommendationType != "latest") {
                sharedPreferences.getString(GENRE_PREFS_KEY, null)?.let { jsonString ->
                    Gson().fromJson(jsonString, object : TypeToken<Map<String, Int>>() {}.type)
                } ?: moviesManager.getAllGenrePreferences(currentUser, minScore, maxScore)
            } else {
                mapOf<String, Int>()
            }
            val movies = moviesManager.fetchMovies(userGenrePreferences, recommendationType)
            bindImagesToViews(movies.take(4), imageViews)
        }
    }

    fun bindImagesToViews(movies: List<Movie>, imageViews: List<ImageView>) {
        for (i in movies.indices) {
            bindImageToView(imageViews[i], movies[i])
        }
    }

    fun bindImageToView(imageView: ImageView, movie: Movie) {
        Glide.with(this)
            .load("https://image.tmdb.org/t/p/w500${movie.posterPath}")
            .diskCacheStrategy(DiskCacheStrategy.ALL)
            .into(imageView)

        imageView.setOnClickListener {
            val intent = Intent(this, DetailActivity::class.java).apply {
                putExtra("MOVIE_ID", movie.id)
            }
            startActivity(intent)
        }
    }

    private fun setAnimations() {
        val optionsFragment =
            supportFragmentManager.findFragmentById(R.id.fragmentContainerView) as OptionsFragment
        optionsFragment.view?.findViewById<ImageButton>(R.id.ib_settings)?.setOnClickListener {
            startActivity(Intent(this, SettingsActivity::class.java))
            this.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
        }
        optionsFragment.view?.findViewById<ImageButton>(R.id.ib_users)?.setOnClickListener {
            startActivity(Intent(this, UsersActivity::class.java))
            this.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
        }
        optionsFragment.view?.findViewById<ImageButton>(R.id.ib_list)?.isClickable=false

    }

    private fun setListeners() {
        binding.cntRecoMore.setOnClickListener {
            getRecommendedMovies("recommended", 7, 10)
        }

        binding.cntSurMore.setOnClickListener {
            getRecommendedMovies("surprise", 4, 7)
        }
        binding.cntLatMore.setOnClickListener {
            getRecommendedMovies("latest", 0, 10)
        }
    }

    private fun getRecommendedMovies(recommendationType: String, minScore: Int, maxScore: Int) {
        lifecycleScope.launch(Dispatchers.IO) {
            val currentUser = FirebaseAuth.getInstance().currentUser
            if (currentUser != null) {
                val userGenrePreferences = moviesManager.getAllGenrePreferences(currentUser, minScore, maxScore)
                fetchAndDisplayMovies(userGenrePreferences, recommendationType)
            } else {
                Log.w("MoviesManager", "No user signed in, cannot retrieve preferences")
            }
        }
    }

    private suspend fun fetchAndDisplayMovies(userGenrePreferences: Map<String, Int>, recommendationType: String) {
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
