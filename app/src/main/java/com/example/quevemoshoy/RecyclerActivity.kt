package com.example.quevemoshoy

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.quevemoshoy.adapter.MovieAdapter
import com.example.quevemoshoy.databinding.ActivityRecyclerBinding
import com.example.quevemoshoy.main.MainActivity2
import com.example.quevemoshoy.model.Movie

class RecyclerActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRecyclerBinding
    private lateinit var adapter: MovieAdapter
    private var moviesList: ArrayList<Movie> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRecyclerBinding.inflate(layoutInflater)
        setContentView(binding.root)
        moviesList = (intent.getSerializableExtra("movies") as? ArrayList<Movie>)!!
        supportActionBar?.hide()
        setRecyclerView(moviesList)
    }


    private fun setRecyclerView(moviesList: ArrayList<Movie>) {
        adapter = MovieAdapter(moviesList) { movieId ->
            val intent = Intent(this, DetailActivity::class.java)
            intent.putExtra("MOVIE_ID", movieId)
            startActivity(intent)
        }
        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        binding.recyclerView.adapter = adapter

    }

    override fun onBackPressed() {
        super.onBackPressed()
        val isFromAllGenres = intent.getBooleanExtra("isFromAllGenres", false)
        if (isFromAllGenres) {
            startActivity(Intent(this, AllGenresActivity::class.java))
        } else {
            startActivity(Intent(this,MainActivity2::class.java))

        }

    }
}


