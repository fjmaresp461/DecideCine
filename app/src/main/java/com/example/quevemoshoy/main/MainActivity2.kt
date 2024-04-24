package com.example.quevemoshoy.main

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.example.quevemoshoy.DetailActivity
import com.example.quevemoshoy.LoginActivity
import com.example.quevemoshoy.R
import com.example.quevemoshoy.RecyclerActivity
import com.example.quevemoshoy.database.DatabaseManager
import com.example.quevemoshoy.databinding.ActivityMain2Binding
import com.example.quevemoshoy.model.Movie
import com.example.quevemoshoy.model.MoviesManager
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
    private lateinit var sharedPreferences:SharedPreferences
    private val moviesManager = MoviesManager()
    val currentUser = FirebaseAuth.getInstance().currentUser
    private val PREFS_NAME = "user_prefs"
    private val GENRE_PREFS_KEY = "genre_prefs"


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMain2Binding.inflate(layoutInflater)
        setContentView(binding.root)
        auth = Firebase.auth
        sharedPreferences=getSharedPreferences(PREFS_NAME,Context.MODE_PRIVATE)
        setListeners()
        setAnimations()
        initRecommendations()
    }

    fun initRecommendations() {
        initMovies(
            "recommended",
            listOf(binding.ivReco1, binding.ivReco2, binding.ivReco3, binding.ivReco4)
        )
        initMovies("latest", listOf(binding.ivLat1, binding.ivLat2, binding.ivLat3, binding.ivLat4))
        //añadir similar a "mi lista"
    }

    fun initMovies(recommendationType: String, imageViews: List<ImageView>) {
        lifecycleScope.launch {
            val userGenrePreferences = if (recommendationType != "latest") {
                sharedPreferences.getString(GENRE_PREFS_KEY, null)?.let { jsonString ->
                    Gson().fromJson(jsonString, object : TypeToken<Map<String, Int>>() {}.type)
                } ?: moviesManager.getAllGenrePreferences(currentUser)
            } else {
                mapOf<String, Int>()
            }

            val movies = if (recommendationType == "latest") {
                moviesManager.fetchMovies(recommendationType)
            } else {
                moviesManager.fetchMoviesByGenre()
            }

            bindImagesToViews(movies.take(4), imageViews)
        }
    }


    fun bindImagesToViews(movies: List<Movie>, imageViews: List<ImageView>) {
        for (i in movies.indices) {
            bindImageToView(imageViews[i], movies[i])
        }
    }

    fun bindImageToView(imageView: ImageView, movie: Movie) {
        Glide.with(this).load("https://image.tmdb.org/t/p/w500${movie.posterPath}")
            .diskCacheStrategy(DiskCacheStrategy.ALL).into(imageView)

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
        optionsFragment.view?.findViewById<ImageButton>(R.id.ib_list)?.isClickable = false

    }

    private fun setListeners() {
        binding.cntRecoMore.setOnClickListener {
            intentRecycler("recommended")
        }

        binding.cntLatMore.setOnClickListener {
            intentRecycler("latest")
        }
    }

    private fun intentRecycler(type: String) {
        lifecycleScope.launch(Dispatchers.IO) {
            val movies = when (type) {
                "recommended" -> {

                    moviesManager.fetchMoviesByGenre()
                }
                "latest" -> {
                    moviesManager.fetchMovies(type)
                }
                else -> {
                    Log.w("MainActivity", "Unknown type: $type")
                    return@launch
                }
            }

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
        }
    }



    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_options, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {


            R.id.item_sign_out -> {
                auth.signOut()
                finish()
                startActivity(Intent(this, LoginActivity::class.java))
            }


        }
        return super.onOptionsItemSelected(item)
    }
}
