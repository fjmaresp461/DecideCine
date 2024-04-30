package com.example.quevemoshoy.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.quevemoshoy.R
import com.example.quevemoshoy.model.Movie

class FavAdapter(
    private var list: MutableList<Movie?>,
    private val context: Context
) : RecyclerView.Adapter<FavMovieHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FavMovieHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.cardview_horizontal, parent, false)
        return FavMovieHolder(view, context)
    }

    override fun onBindViewHolder(holder: FavMovieHolder, position: Int) {
        list[position]?.let { holder.render(it) }
    }

    override fun getItemCount(): Int {
        return list.size
    }
}


