package com.example.quevemoshoy.main

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.example.quevemoshoy.DetailActivity
import com.example.quevemoshoy.R
import com.example.quevemoshoy.RecyclerActivity
import com.example.quevemoshoy.adapter.FavAdapter
import com.example.quevemoshoy.database.DBStarter
import com.example.quevemoshoy.database.DatabaseHelper
import com.example.quevemoshoy.database.DatabaseManager
import com.example.quevemoshoy.databinding.ActivityMain2Binding
import com.example.quevemoshoy.model.Movie
import com.example.quevemoshoy.model.MoviesManager
import com.example.quevemoshoy.model.MoviesManager.Companion.genrePreferencesCache
import com.example.quevemoshoy.model.MoviesManager.Companion.moviesCache
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * `MainActivity2` es la actividad principal de la aplicación.
 *
 * Esta clase se encarga de inicializar y gestionar las principales funcionalidades de la aplicación,
 * como la carga de películas recomendadas y recientes, la interacción con la base de datos y la gestión de las preferencias del usuario.
 *
 * @property binding Enlace de la actividad con su vista.
 * @property auth Autenticación de Firebase.
 * @property sharedPreferences Preferencias compartidas para almacenar las preferencias de género del usuario.
 * @property moviesManager Gestor de películas para interactuar con la API de películas.
 * @property currentUser Usuario actual autenticado con Firebase.
 * @property PREFS_NAME Nombre de las preferencias compartidas.
 * @property GENRE_PREFS_KEY Clave para almacenar y recuperar las preferencias de género del usuario.
 * @property dbManager Gestor de la base de datos para interactuar con la base de datos local.
 * @property movies Lista de películas cargadas.
 * @property movieManager Gestor de películas para interactuar con la API de películas.
 * @property favoriteMoviesList Lista de películas favoritas del usuario.
 *
 * @constructor Crea una instancia de `MainActivity2`.
 */
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

    /**
     * Se llama cuando se crea la actividad. Inicializa la vista, la autenticación de Firebase,
     * las preferencias compartidas, los oyentes y las animaciones. También inicia las recomendaciones y la base de datos.
     *
     * @param savedInstanceState El estado guardado de la actividad, si está disponible.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMain2Binding.inflate(layoutInflater)
        setContentView(binding.root)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        supportActionBar?.hide()
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

    /**
     * Inicializa la base de datos.
     */
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

    /**
     * Carga las películas favoritas del usuario.
     */
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
        val emptyView = binding.emptyView
        if (favoriteMoviesList.isEmpty()) {
            recyclerView.visibility = View.GONE
            emptyView.visibility = View.VISIBLE
        } else {
            recyclerView.visibility = View.VISIBLE
            emptyView.visibility = View.GONE
        }
        recyclerView.adapter = FavAdapter(favoriteMoviesList, this)
    }


    /**
     * Inicializa las recomendaciones de películas.
     */

    private fun initRecommendations() {
        initMovies(
            "recommended",
            listOf(binding.ivReco1, binding.ivReco2, binding.ivReco3, binding.ivReco4)
        )
        initMovies(
            "latest", listOf(binding.ivLat1, binding.ivLat2, binding.ivLat3, binding.ivLat4)
        )

    }

    /**
     * Inicializa las películas basándose en el tipo de recomendación y las vistas de imagen proporcionadas.
     */
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
                    moviesManager.fetchMoviesByGenreAndProvider()
                }
            }

            bindImagesToViews(movies.take(4), imageViews)
        }
    }


    /**
     * Vincula las imágenes a las vistas proporcionadas.
     */
    private fun bindImagesToViews(movies: List<Movie?>, imageViews: List<ImageView>) {
        for (i in movies.indices) {
            movies[i]?.let { bindImageToView(imageViews[i], it) }
        }
    }

    /**
     * Vincula una imagen a una vista.
     */
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

    /**
     * Establece las animaciones para los botones de la interfaz.
     */
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
        optionsFragment.view?.findViewById<ImageButton>(R.id.ib_genres)?.setOnClickListener {
            startActivity(Intent(this, AllGenresActivity::class.java))
            this.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
        }
        optionsFragment.view?.findViewById<ImageButton>(R.id.ib_list)?.isClickable = false

    }

    /**
     * Establece los oyentes para los botones y contenedores de la interfaz.
     */
    private fun setListeners() {
        binding.cntRecommended.setOnClickListener {
            intentRecycler("recommended")
        }

        binding.cntLatest.setOnClickListener {
            intentRecycler("latest")
        }
        binding.cntList.setOnClickListener {
            intentRecycler("myList")
        }


    }

    /**
     * Inicia la actividad `RecyclerActivity` basándose en el tipo de películas proporcionado.
     */
    private fun intentRecycler(type: String) {
        lifecycleScope.launch(Dispatchers.IO) {
            val movies = when (type) {
                "recommended" -> {
                    moviesManager.fetchMoviesByGenreAndProvider()
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


    /**
     * Se llama cuando se reanuda la actividad. Recarga las recomendaciones y las películas favoritas.
     */
    override fun onResume() {
        super.onResume()

        lifecycleScope.launch(Dispatchers.Main) {
            val currentUserGenrePreferences =
                MoviesManager().getAllGenrePreferences(currentUser).toMutableList()
            if (genrePreferencesCache != currentUserGenrePreferences) {
                moviesCache = null
            }
        }


        initRecommendations()
        lifecycleScope.launch(Dispatchers.Main) {
            loadFavoriteMovies()
        }
    }


    /**
     * Se llama cuando se presiona el botón de retroceso. En este caso, no hace nada.
     */
    override fun onBackPressed() {

    }
}
