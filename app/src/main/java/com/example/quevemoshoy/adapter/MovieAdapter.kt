package com.example.quevemoshoy.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.quevemoshoy.R
import com.example.quevemoshoy.model.Movie

class MovieAdapter(
    private var list: List<Movie>,
    private val onItemClick: (Int) -> Unit
) : RecyclerView.Adapter<MovieViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MovieViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.cardview_vertical, parent, false)
        return MovieViewHolder(view)
    }

    override fun onBindViewHolder(holder: MovieViewHolder, position: Int) {
        holder.render(list[position], onItemClick)
    }

    override fun getItemCount(): Int {
        return list.size
    }
}