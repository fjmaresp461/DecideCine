package com.example.quevemoshoy

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.quevemoshoy.adapter.GenreAdapter
import com.example.quevemoshoy.databinding.ActivityAllGenresBinding
import com.example.quevemoshoy.model.Movie
import com.example.quevemoshoy.model.MoviesManager
import com.example.quevemoshoy.provider.ApiClient
import com.example.quevemoshoy.provider.MovieInterface
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class AllGenresActivity : AppCompatActivity() {
    private lateinit var binding:ActivityAllGenresBinding
    private val apiService = ApiClient.retrofit.create(MovieInterface::class.java)
    private val movieManager = MoviesManager()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivityAllGenresBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()
        setListeners()

    }

    private fun setListeners() {
        binding.cntAction.setOnClickListener {
            movieManager.fetchAndStartActivity(this,"28")
        }
        binding.cntAnimation.setOnClickListener {
            movieManager.fetchAndStartActivity(this,"16")
        }
        binding.cntAdventure.setOnClickListener {
            movieManager.fetchAndStartActivity(this,"12")
        }
        binding.cntComedy.setOnClickListener {
            movieManager.fetchAndStartActivity(this,"35")
        }
        binding.cntCrime.setOnClickListener {
            movieManager.fetchAndStartActivity(this,"80")
        }
        binding.cntDocu.setOnClickListener {
            movieManager.fetchAndStartActivity(this,"99")
        }
        binding.cntDrama.setOnClickListener {
            movieManager.fetchAndStartActivity(this,"18")
        }
        binding.cntFamiliar.setOnClickListener {
            movieManager.fetchAndStartActivity(this,"10751")
        }
        binding.cntHistory.setOnClickListener {
            movieManager.fetchAndStartActivity(this,"36")
        }
        binding.cntHorror.setOnClickListener {
            movieManager.fetchAndStartActivity(this,"27")
        }
        binding.cntMusic.setOnClickListener {
            movieManager.fetchAndStartActivity(this,"10402")
        }
        binding.cntMistery.setOnClickListener {
            movieManager.fetchAndStartActivity(this,"9648")
        }
        binding.cntRomantic.setOnClickListener {
            movieManager.fetchAndStartActivity(this,"10749")
        }
        binding.cntScifi.setOnClickListener {
            movieManager.fetchAndStartActivity(this,"878")
        }
        binding.cntTv.setOnClickListener {
            movieManager.fetchAndStartActivity(this,"10770")
        }
        binding.cntThriller.setOnClickListener {
            movieManager.fetchAndStartActivity(this,"53")
        }
        binding.cntWar.setOnClickListener {
            movieManager.fetchAndStartActivity(this,"10752")
        }
        binding.cntWestern.setOnClickListener {
            movieManager.fetchAndStartActivity(this,"37")
        }
    }


    private fun fetchAndStartActivity(genreId: String) {
        CoroutineScope(Dispatchers.IO).launch {
            val movies = fetchMoviesByOneGenre(genreId)
            withContext(Dispatchers.Main) {
                val intent = Intent(this@AllGenresActivity, RecyclerActivity::class.java).apply {
                    putExtra("movies", ArrayList(movies))
                }
                startActivity(intent)
            }
        }
    }

    suspend fun fetchMoviesByOneGenre(genreId: String): List<Movie> {
        val response = apiService.getMoviesByGenres(genres = genreId)
        return response.movies.take(20) // Toma solo las primeras 20 películas
    }

    /*accion,animacion,aventura,belica,cienciaficcion,comedia,crimen,
    documental,drama,familiar,historia,misterio,musica,oeste,
    romantica,television,terror,thriller
     En el layout, existe un constraintlayout para cada genero, como la linea anterior
     esta nombrado asi: cntAction,cntAnimation, etc y asi con cada genero(están en ingles)
     quiero que si se pulsa un  genero, llame al siguiente metodo de la interfaz
     @GET("/3/discover/movie")
        suspend fun getMoviesByGenres(
            @Query("api_key") apiKey: String = ApiClient.API_KEY,
            @Query("with_genres") genres: String,
            @Query("language") language: String = "es-ES",
            @Query("sort_by") sortBy: String = "vote_average.desc",
            @Query("vote_count.gte") voteCount: Int = 100,
            @Query("page") page: Int =3

        ): MovieResponse
        el resultado se pasará a un intent a recyclerActivity, debe ser una lista movie.
     el genero se manda por su id, esta es la lista donde se relaciona un id a su genero
     (te lo muestro en un metodo para que lo tengas en cuenta
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
    */

}