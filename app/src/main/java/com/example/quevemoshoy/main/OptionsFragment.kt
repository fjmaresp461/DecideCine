package com.example.quevemoshoy.main

import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import com.example.quevemoshoy.R

/**
 * `OptionsFragment` es un fragmento que proporciona opciones de navegación en la aplicación.
 *
 * Este fragmento contiene botones que permiten al usuario navegar a diferentes partes de la aplicación.
 *
 * @property ibList Botón para navegar a la lista de películas.
 * @property ibUsers Botón para navegar a la actividad de usuarios.
 * @property ibSettings Botón para navegar a la actividad de configuración.
 *
 * @constructor Crea una instancia de `OptionsFragment`.
 */
class OptionsFragment : Fragment() {
    private lateinit var ibList: ImageButton
    private lateinit var ibUsers: ImageButton
    private lateinit var ibSettings: ImageButton
    private lateinit var ibGenres: ImageButton

    /**
     * Se llama cuando se crea el fragmento. Inicializa cualquier dato necesario.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {

        }
    }

    /**
     * Se llama para crear la vista del fragmento.
     * @return Devuelve la vista del fragmento.
     */
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_options, container, false)
    }

    /**
     * Se llama inmediatamente después de  onCreateView.
     *
     * @param view La vista devuelta por onCreateView(LayoutInflater, ViewGroup, Bundle).

     */
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        ibList = view.findViewById(R.id.ib_list)
        ibUsers = view.findViewById(R.id.ib_users)
        ibSettings = view.findViewById(R.id.ib_settings)
        ibGenres = view.findViewById(R.id.ib_genres)
        mostrarSombra()
        setListeners()
    }

    /**
     * Muestra la sombra en los botones de la interfaz.
     */
    private fun mostrarSombra() {
        ibList.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#00000000"))
        ibUsers.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#00000000"))
        ibSettings.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#00000000"))
        ibGenres.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#00000000"))

        if (this.activity is MainActivity2) {
            ibList.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#FFD4E4A4"))
        }
        if (this.activity is UsersActivity) {
            ibUsers.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#FFD4E4A4"))
        }
        if (this.activity is SettingsActivity) {
            ibSettings.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#FFD4E4A4"))
        }
        if (this.activity is AllGenresActivity) {
            ibGenres.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#FFD4E4A4"))
        }
    }

    /**
     * Establece los oyentes para los botones de la interfaz.
     */

    private fun setListeners() {
        ibList.setOnClickListener {
            ibList.isClickable = false

            ibList.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#FFD4E4A4"))
            ibUsers.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#00000000"))
            ibSettings.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#00000000"))

            startActivity(Intent(context, MainActivity2::class.java))

        }
        ibGenres.setOnClickListener {
            it.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#FFD4E4A4"))
            ibList.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#00000000"))
            ibSettings.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#00000000"))
            ibUsers.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#00000000"))
            it.isClickable = false

            startActivity(Intent(context, AllGenresActivity::class.java))

        }
        ibUsers.setOnClickListener {
            it.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#FFD4E4A4"))
            ibList.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#00000000"))
            ibSettings.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#00000000"))
            ibGenres.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#00000000"))
            it.isClickable = false

            startActivity(Intent(context, UsersActivity::class.java))

        }

        ibSettings.setOnClickListener {
            it.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#FFD4E4A4"))
            ibUsers.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#00000000"))
            ibList.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#00000000"))
            ibGenres.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#00000000"))
            it.isClickable = false

            startActivity(Intent(context, SettingsActivity::class.java))

        }
    }

}
