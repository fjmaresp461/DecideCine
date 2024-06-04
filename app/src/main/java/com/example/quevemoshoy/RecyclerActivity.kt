package com.example.quevemoshoy

import android.content.Intent
import android.content.pm.ActivityInfo
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.quevemoshoy.adapter.MovieAdapter
import com.example.quevemoshoy.databinding.ActivityRecyclerBinding
import com.example.quevemoshoy.main.AllGenresActivity
import com.example.quevemoshoy.main.MainActivity2
import com.example.quevemoshoy.model.Movie

/**
 * `RecyclerActivity` es una actividad que muestra una lista de películas en un RecyclerView.
 *
 * Esta actividad recibe una lista de películas, las muestra en un RecyclerView y permite al usuario seleccionar una película para ver sus detalles.
 *
 * @property binding Enlace de la actividad con su vista.
 * @property adapter El adaptador para el RecyclerView.
 * @property moviesList La lista de películas a mostrar.
 *
 * @constructor Crea una instancia de `RecyclerActivity`.
 */
class RecyclerActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRecyclerBinding
    private lateinit var adapter: MovieAdapter
    private var moviesList: ArrayList<Movie> = ArrayList()

    /**
     * Se llama cuando se crea la actividad. Inicializa la vista, recupera la lista de películas del intento y establece el RecyclerView.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRecyclerBinding.inflate(layoutInflater)
        setContentView(binding.root)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        moviesList = (intent.getSerializableExtra("movies") as? ArrayList<Movie>)!!
        supportActionBar?.hide()
        setRecyclerView(moviesList)
    }

    /**
     * Establece el RecyclerView con la lista de películas.
     *
     * @param moviesList La lista de películas a mostrar.
     */
    private fun setRecyclerView(moviesList: ArrayList<Movie>) {
        adapter = MovieAdapter(moviesList) { movieId ->
            val intent = Intent(this, DetailActivity::class.java)
            intent.putExtra("MOVIE_ID", movieId)
            startActivity(intent)
        }
        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        binding.recyclerView.adapter = adapter

    }

    /**
     * Se llama cuando se presiona el botón de retroceso. Redirige al usuario a la actividad `AllGenresActivity` o `MainActivity2` dependiendo de si la actividad fue iniciada desde `AllGenresActivity`.
     */
    override fun onBackPressed() {
        super.onBackPressed()

            startActivity(Intent(this, MainActivity2::class.java))



    }
}


