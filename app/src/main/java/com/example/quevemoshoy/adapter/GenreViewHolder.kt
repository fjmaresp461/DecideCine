package com.example.quevemoshoy.adapter

import android.view.View
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.example.quevemoshoy.R

/**
 * GenreViewHolder muestra un género en una vista.
 *
 * @property itemView La vista que se utilizará para mostrar el género.
 */
class GenreViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    val imageView: ImageView = itemView.findViewById(R.id.imageView)
}