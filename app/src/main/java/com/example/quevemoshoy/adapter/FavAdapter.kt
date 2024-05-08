package com.example.quevemoshoy.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.quevemoshoy.R
import com.example.quevemoshoy.model.Movie

/**
 * FavAdapter es un adaptador que muestra una lista de películas favoritas.
 *
 * @property list La lista de películas a mostrar.
 * @property context El entorno donde se usa este adaptador.
 */
class FavAdapter(
    private var list: MutableList<Movie?>, private val context: Context
) : RecyclerView.Adapter<FavMovieHolder>() {

    /**
     * Crea una nueva vista para mostrar una película.
     *
     * @param parent El grupo de vistas donde se agregará la nueva vista.
     * @param viewType El tipo de vista que se va a crear.
     * @return Una nueva vista para mostrar una película.
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FavMovieHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.cardview_horizontal, parent, false)
        return FavMovieHolder(view, context)
    }

    /**
     * Muestra los datos de una película en la posición especificada.
     *
     * @param holder La vista que se actualizará con los datos de la película.
     * @param position La posición de la película en la lista.
     */
    override fun onBindViewHolder(holder: FavMovieHolder, position: Int) {
        list[position]?.let { holder.render(it) }
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
