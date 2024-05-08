package com.example.quevemoshoy.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.example.quevemoshoy.R

/**
 * GenreAdapter muestra una lista de géneros en un RecyclerView.
 *
 * @property genres La lista de géneros a mostrar.
 */
class GenreAdapter
    (private val genres: List<String>) : RecyclerView.Adapter<GenreViewHolder>() {

    /**
     * Crea una nueva vista para mostrar un género.
     *
     * @param parent El grupo de vistas donde se agregará la nueva vista.
     * @param viewType El tipo de vista que se va a crear.
     * @return Una nueva vista para mostrar un género.
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GenreViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.cardview_horizontal, parent, false)
        return GenreViewHolder(view)
    }

    /**
     * Muestra un género en la posición especificada.
     *
     * @param holder La vista que se actualizará con el género.
     * @param position La posición del género en la lista.
     */
    override fun onBindViewHolder(holder: GenreViewHolder, position: Int) {
        val genre = genres[position]
        val resourceId = holder.itemView.context.resources.getIdentifier(
            genre, "drawable", holder.itemView.context.packageName
        )
        holder.imageView.setImageResource(resourceId)
        holder.imageView.setOnClickListener {

        }
    }

    /**
     * Devuelve el número total de géneros en la lista.
     *
     * @return El número total de géneros.
     */
    override fun getItemCount() = genres.size
}