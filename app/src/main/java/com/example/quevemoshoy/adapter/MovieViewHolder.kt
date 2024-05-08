package com.example.quevemoshoy.adapter

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.quevemoshoy.databinding.CardviewVerticalBinding
import com.example.quevemoshoy.model.Movie

/**
 * MovieViewHolder muestra los detalles de una película en una vista.
 *
 * @property v La vista que se utilizará para mostrar los detalles de la película.
 */
class MovieViewHolder(v: View) : RecyclerView.ViewHolder(v) {
    private val binding = CardviewVerticalBinding.bind(v)

    /**
     * Muestra los detalles de una película en la vista.
     *
     * @param movie La película cuyos detalles se van a mostrar.
     * @param onItemClick La acción a realizar cuando se hace clic en la película.
     */
    fun render(movie: Movie, onItemClick: (Int) -> Unit) {
        binding.tvRecTitle.text = movie.title
        Glide.with(itemView.context).load("https://image.tmdb.org/t/p/w500${movie.posterPath}")
            .into(binding.ivRecImage)
        itemView.setOnClickListener {
            onItemClick(movie.id)
        }
    }
}