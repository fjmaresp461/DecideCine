package com.example.quevemoshoy


import android.content.Intent
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.example.quevemoshoy.database.DatabaseManager
import com.example.quevemoshoy.databinding.ActivityDetailBinding
import com.example.quevemoshoy.main.MainActivity2
import com.example.quevemoshoy.model.Genre
import com.example.quevemoshoy.model.Movie
import com.example.quevemoshoy.model.MoviesManager
import com.example.quevemoshoy.model.MoviesManager.Companion.latestMoviesCache
import com.example.quevemoshoy.model.MoviesManager.Companion.moviesCache
import com.example.quevemoshoy.model.Providers
import com.example.quevemoshoy.model.SimpleMovie
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * `DetailActivity` es una actividad que muestra los detalles de una película.
 *
 * Esta actividad proporciona una interfaz para que el usuario vea los detalles de una película, incluyendo el título, la descripción, la duración, los géneros y los proveedores. También permite al usuario añadir la película a sus favoritos.
 *
 * @property binding Enlace de la actividad con su vista.
 * @property movieTitle El título de la película.
 * @property dbManager El gestor de la base de datos para interactuar con la base de datos local.
 * @property movieManager El gestor de películas para interactuar con la API de películas.
 * @property genreNameToIdMap Un mapa de los nombres de los géneros a sus IDs.
 *
 * @constructor Crea una instancia de `DetailActivity`.
 */
class DetailActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDetailBinding
    private var movieTitle: String? = null
    private val dbManager = DatabaseManager()
    private var movieManager = MoviesManager()

    val genreNameToIdMap = mapOf(
        "Acción" to "28",
        "Aventura" to "12",
        "Animación" to "16",
        "Comedia" to "35",
        "Crimen" to "80",
        "Documental" to "99",
        "Drama" to "18",
        "Familia" to "10751",
        "Fantasía" to "14",
        "Historia" to "36",
        "Horror" to "27",
        "Música" to "10402",
        "Misterio" to "9648",
        "Romance" to "10749",
        "Ciencia ficción" to "878",
        "Película de TV" to "10770",
        "Suspense" to "53",
        "Bélica" to "10752",
        "Western" to "37"
    )

    /**
     * Se llama cuando se crea la actividad. Inicializa la vista, recupera el ID de la película del intento, recupera y muestra los detalles de la película, y establece los oyentes.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        supportActionBar?.hide()
        val movieId = intent.getIntExtra("MOVIE_ID", -1)
        fetchAndDisplayMovieDetails(movieId)

        setListeners(movieId)
    }

    /**
     * Establece los oyentes para los botones de la interfaz.
     */
    private fun setListeners(movieId: Int) {
        binding.ibTmdb.setOnClickListener {
            val url = "https://www.themoviedb.org/movie/$movieId?language=es"
            val intent = Intent(this, WebViewActivity::class.java)
            intent.putExtra("url", url)
            startActivity(intent)
        }
        binding.swFav.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                addMovieToFavorites(movieId, movieTitle!!)

            } else {
                removeMoviefromFavorites(movieId)
            }
        }

    }

    /**
     * Actualiza las películas en caché.
     */
    suspend fun updateMovies() {
        moviesCache = MoviesManager().fetchMoviesByGenreAndProvider()
        latestMoviesCache = MoviesManager().fetchMovies("latest")
    }

    /**
     * Recupera y muestra los detalles de una película.
     */
    private fun fetchAndDisplayMovieDetails(movieId: Int) {
        lifecycleScope.launch(Dispatchers.Main) {
            val movie = withContext(Dispatchers.IO) { movieManager.fetchMovieById(movieId) }
            if (movie != null) {
                displayMovieDetails(movie)
                movieTitle = movie.title
                val isFavorite = dbManager.readAll().any { it.id == movieId }
                binding.swFav.isChecked = isFavorite
                val providers =
                    withContext(Dispatchers.IO) { movieManager.fetchMovieProviders(movieId) }
                displayProviders(providers)
            } else {
                Toast.makeText(
                    this@DetailActivity, R.string.movie_not_found, Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    /**
     * Muestra los proveedores de una película.
     */
    private fun displayProviders(providers: List<Providers>?) {
        binding.providersLayout.removeAllViews()
        if (providers.isNullOrEmpty()) {
            Toast.makeText(this, R.string.movie_dont_available, Toast.LENGTH_SHORT).show()
        } else {
            val stringBuilder = StringBuilder()
            stringBuilder.append(R.string.available_platforms.toString())
            providers.forEach { provider ->
                stringBuilder.append(provider.providerName).append(", ")
                val imageView = ImageView(this)

                when (provider.providerName) {
                    "Netflix" -> imageView.setImageResource(R.drawable.netflix_icon)
                    "Amazon Prime Video" -> imageView.setImageResource(R.drawable.amazon_prime_icon)
                    "Disney Plus" -> imageView.setImageResource(R.drawable.disney_plus_icon)
                    "Apple TV" -> imageView.setImageResource(R.drawable.apple_tv_icon)
                    "Crunchyroll" -> imageView.setImageResource(R.drawable.crunchyroll_icon)
                    "Google Play Movies" -> imageView.setImageResource(R.drawable.google_play_icon)
                    "HBO Max" -> imageView.setImageResource(R.drawable.hbo_max_icon)
                    "Movistar Plus" -> imageView.setImageResource(R.drawable.movistar_plus_icon)
                    "Rakuten TV" -> imageView.setImageResource(R.drawable.rakuten_icon)
                    "SkyShowtime" -> imageView.setImageResource(R.drawable.skyshowtime_icon)
                }

                binding.providersLayout.addView(imageView)
            }
            stringBuilder.setLength(stringBuilder.length - 2)
        }
    }


    /**
     * Muestra los detalles de una película.
     */
    private fun displayMovieDetails(movie: Movie) {
        binding.movieTitle.text = movie.title
        binding.tvSummary.text = movie.overview
        binding.tvRuntime.text = "${movie.runtime} minutos."
        Glide.with(this).load("https://image.tmdb.org/t/p/w500${movie.posterPath}")
            .into(binding.movieImg)

        val genreNames = movie.genres.joinToString(", ") { it.name }
        val spannableString = createSpannableGenres(genreNames, movie.genres)

        binding.tvGenres.movementMethod = LinkMovementMethod.getInstance()
        binding.tvGenres.text = spannableString
    }

    /**
     * Crea un texto seleccionable para los géneros de una película.
     */
    private fun createSpannableGenres(genreNames: String, genres: List<Genre>): SpannableString {
        val spannableString = SpannableString(genreNames)


        var start = 0
        for (genre in genres) {
            val end = start + genre.name.length
            val clickableSpan = object : ClickableSpan() {
                override fun onClick(widget: View) {

                    val genreId = genreNameToIdMap[genre.name]

                    if (genreId != null) {
                        movieManager.fetchAndStartActivity(this@DetailActivity, genreId)
                    }
                }
            }
            spannableString.setSpan(clickableSpan, start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
            start = end + 2
        }

        return spannableString
    }

    /**
     * Añade una película a los favoritos del usuario.
     */
    private fun addMovieToFavorites(movieId: Int, movieTitle: String) {
        val dbManager = DatabaseManager()
        val movie = SimpleMovie(movieId, movieTitle)
        dbManager.create(movie)
    }

    /**
     * Elimina una película de los favoritos del usuario.
     */
    private fun removeMoviefromFavorites(movieId: Int) {
        val dbManager = DatabaseManager()
        dbManager.delete(movieId)
        startActivity(Intent(this, MainActivity2::class.java))
    }

    /**
     * Se llama cuando se presiona el botón de retroceso. Finaliza la actividad.
     */
    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }
}

