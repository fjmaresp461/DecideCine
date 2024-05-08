package com.example.quevemoshoy.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.quevemoshoy.R
import com.example.quevemoshoy.model.Movie

/**
 * MovieAdapter muestra una lista de películas en un RecyclerView.
 *
 * @property list La lista de películas a mostrar.
 * @property onItemClick La acción a realizar cuando se hace clic en una película.
 */
class MovieAdapter(
    private var list: List<Movie>, private val onItemClick: (Int) -> Unit
) : RecyclerView.Adapter<MovieViewHolder>() {

    /**
     * Crea una nueva vista para mostrar una película.
     *
     * @param parent El grupo de vistas donde se agregará la nueva vista.
     * @param viewType El tipo de vista que se va a crear.
     * @return Una nueva vista para mostrar una película.
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MovieViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.cardview_vertical, parent, false)
        return MovieViewHolder(view)
    }

    /**
     * Muestra los detalles de una película en la posición especificada.
     *
     * @param holder La vista que se actualizará con los detalles de la película.
     * @param position La posición de la película en la lista.
     */
    override fun onBindViewHolder(holder: MovieViewHolder, position: Int) {
        holder.render(list[position], onItemClick)
    }

    /**
     * Devuelve el número total de películas en la lista.
     *
     * @return El número total de películas.
     */
    override fun getItemCount(): Int {
        return list.size
    }
}