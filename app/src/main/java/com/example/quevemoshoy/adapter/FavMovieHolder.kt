package com.example.quevemoshoy.adapter

import android.content.Context
import android.content.Intent
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.quevemoshoy.DetailActivity
import com.example.quevemoshoy.RecyclerActivity
import com.example.quevemoshoy.databinding.CardviewHorizontalBinding
import com.example.quevemoshoy.databinding.SeeMoreBinding
import com.example.quevemoshoy.model.Movie

class FavMovieHolder(v: View, private val context: Context) : RecyclerView.ViewHolder(v) {
    private val movieBinding = CardviewHorizontalBinding.bind(v)

    fun render(movie: Movie) {
        Glide.with(itemView.context).load("https://image.tmdb.org/t/p/w500${movie.posterPath}")
            .into(movieBinding.ivPoster)
        itemView.setOnClickListener {
            val intent = Intent(context, DetailActivity::class.java).apply {
                putExtra("MOVIE_ID", movie.id)
            }
            context.startActivity(intent)
        }
    }
}








