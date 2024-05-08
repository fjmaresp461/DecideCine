package com.example.quevemoshoy.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.example.quevemoshoy.R

class GenreAdapter
    (private val genres: List<String>) : RecyclerView.Adapter<GenreViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GenreViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.cardview_horizontal, parent, false)
        return GenreViewHolder(view)
    }

    override fun onBindViewHolder(holder: GenreViewHolder, position: Int) {
        val genre = genres[position]
        val resourceId = holder.itemView.context.resources.getIdentifier(genre, "drawable", holder.itemView.context.packageName)
        holder.imageView.setImageResource(resourceId)
        holder.imageView.setOnClickListener {

        }
    }

    override fun getItemCount() = genres.size
}
