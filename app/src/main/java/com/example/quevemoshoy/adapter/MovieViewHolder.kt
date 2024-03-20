package com.example.quevemoshoy.adapter

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.quevemoshoy.databinding.MovieLayoutBinding
import com.example.quevemoshoy.model.Movie

class MovieViewHolder(v: View) : RecyclerView.ViewHolder(v) {
    private val binding = MovieLayoutBinding.bind(v)
    fun render(movie: Movie, onItemClick: (Int) -> Unit) {
        binding.tvRecTitle.text = movie.title
        Glide.with(itemView.context).load("https://image.tmdb.org/t/p/w500${movie.posterPath}")
            .into(binding.ivRecImage)
        itemView.setOnClickListener {
            onItemClick(movie.id)
        }
    }
}