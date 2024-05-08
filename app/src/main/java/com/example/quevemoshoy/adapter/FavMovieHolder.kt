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

/**
 * FavMovieHolder muestra los detalles de una película favorita.
 *
 * @property v La vista que se utilizará para mostrar los detalles de la película.
 * @property context El entorno donde se usa este adaptador.
 */
class FavMovieHolder(v: View, private val context: Context) : RecyclerView.ViewHolder(v) {
    private val movieBinding = CardviewHorizontalBinding.bind(v)

    /**
     * Muestra los detalles de una película en la vista.
     *
     * @param movie La película cuyos detalles se van a mostrar.
     */
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
