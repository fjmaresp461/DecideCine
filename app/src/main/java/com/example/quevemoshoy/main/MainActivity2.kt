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
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.example.quevemoshoy.AllGenresActivity
import com.example.quevemoshoy.DetailActivity
import com.example.quevemoshoy.LoginActivity
import com.example.quevemoshoy.R
import com.example.quevemoshoy.RecyclerActivity
import com.example.quevemoshoy.adapter.FavAdapter
import com.example.quevemoshoy.adapter.MovieAdapter
import com.example.quevemoshoy.database.DBStarter
import com.example.quevemoshoy.database.DatabaseHelper
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
    private lateinit var sharedPreferences: SharedPreferences
    private val moviesManager = MoviesManager()
    val currentUser = FirebaseAuth.getInstance().currentUser
    private val PREFS_NAME = "user_prefs"
    private val GENRE_PREFS_KEY = "genre_prefs"
    private val dbManager = DatabaseManager()
    private var movies = mutableListOf<Movie>()
    private val movieManager = MoviesManager()


    companion object {
        var favoriteMoviesList = mutableListOf<Movie?>()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMain2Binding.inflate(layoutInflater)
        setContentView(binding.root)




        auth = Firebase.auth
        sharedPreferences = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        setListeners()
        setAnimations()
        initRecommendations()
        initDatabase()
        lifecycleScope.launch(Dispatchers.Main) {
            loadFavoriteMovies()

        }


    }

    private fun initDatabase() {
        DBStarter.appContext = this.applicationContext

        val currentUser = FirebaseAuth.getInstance().currentUser
        if (currentUser != null) {
            DBStarter.DB = DatabaseHelper(currentUser.uid)
        }

        lifecycleScope.launch(Dispatchers.Main) {
            loadFavoriteMovies()
            movieManager.getPreferredProviders(currentUser)

        }
    }

     suspend fun loadFavoriteMovies() {
         favoriteMoviesList.clear()
        val favoriteMovies = dbManager.readAll()
        for (simpleMovie in favoriteMovies) {
            val movie = withContext(Dispatchers.IO) { movieManager.fetchMovieById(simpleMovie.id) }
            if (movie != null && movie !in favoriteMoviesList) {
                movies.add(movie)
                favoriteMoviesList.add(movie)
            }
        }

        val recyclerView = findViewById<RecyclerView>(R.id.recyclerView)
        recyclerView.adapter = FavAdapter(favoriteMoviesList, this)

    }







    private fun initRecommendations() {
        initMovies(
            "recommended",
            listOf(binding.ivReco1, binding.ivReco2, binding.ivReco3, binding.ivReco4)
        )
        initMovies("latest",
            listOf(binding.ivLat1, binding.ivLat2, binding.ivLat3, binding.ivLat4)
        )

    }

    private fun initMovies(recommendationType: String, imageViews: List<ImageView>) {
        lifecycleScope.launch {
            if (recommendationType != "latest" && recommendationType != "myList") {
                sharedPreferences.getString(GENRE_PREFS_KEY, null)?.let { jsonString ->
                    Gson().fromJson(jsonString, object : TypeToken<Map<String, Int>>() {}.type)
                } ?: moviesManager.getAllGenrePreferences(currentUser)
            } else {
                mapOf<String, Int>()
            }

            val movies = when (recommendationType) {
                "latest" -> {
                    moviesManager.fetchMovies(recommendationType)
                }
                "myList" -> {
                    favoriteMoviesList
                }
                else -> {
                    moviesManager.fetchMoviesByGenre()
                }
            }

            bindImagesToViews(movies.take(4), imageViews)
        }
    }



    private fun bindImagesToViews(movies: List<Movie?>, imageViews: List<ImageView>) {
        for (i in movies.indices) {
            movies[i]?.let { bindImageToView(imageViews[i], it) }
        }
    }

    private fun bindImageToView(imageView: ImageView, movie: Movie) {
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
        binding.cntRecommended.setOnClickListener {
            intentRecycler("recommended")
        }

        binding.cntLatest.setOnClickListener {
            intentRecycler("latest")
        }
       binding.cntList
           .setOnClickListener{
            intentRecycler("myList")
        }
        binding.cntAllGenres.setOnClickListener {
            startActivity(Intent(this, AllGenresActivity::class.java))
        }
        binding.ivActionMain.setOnClickListener{
            movieManager.fetchAndStartActivity(this,"28")
        }
        binding.ivAnimationMain.setOnClickListener{
            movieManager.fetchAndStartActivity(this,"16")
        }
        binding.ivMysteryMain.setOnClickListener{
            movieManager.fetchAndStartActivity(this,"9648")
        }
        binding.ivWesterMain.setOnClickListener{
            movieManager.fetchAndStartActivity(this,"37")
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

                "myList" -> {
                   favoriteMoviesList
                }

                else -> {
                    Log.w("MainActivity", "Unknown type: $type")
                    return@launch
                }
            }

            withContext(Dispatchers.Main) {
                if (movies.isNotEmpty()) {
                    val intent = Intent(this@MainActivity2, RecyclerActivity::class.java).apply {
                        putExtra("movies", ArrayList(movies))
                    }
                    startActivity(intent)
                } else {
                    Toast.makeText(
                        this@MainActivity2, R.string.no_matching_movies, Toast.LENGTH_SHORT
                    ).show()
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
    override fun onResume() {
        super.onResume()

        lifecycleScope.launch(Dispatchers.Main) {
            loadFavoriteMovies()
        }
    }
}
