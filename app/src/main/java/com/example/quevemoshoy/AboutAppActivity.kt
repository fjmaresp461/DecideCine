package com.example.quevemoshoy


import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.quevemoshoy.databinding.ActivityAboutAppBinding

/**
 * `AboutAppActivity` es una actividad que muestra información sobre la aplicación.
 *
 * Esta actividad proporciona una interfaz para que el usuario vea detalles sobre la aplicación.
 *
 * @property binding Enlace de la actividad con su vista.
 *
 * @constructor Crea una instancia de `AboutAppActivity`.
 */

class AboutAppActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAboutAppBinding

    /**
     * Se llama cuando se crea la actividad. Inicializa la vista y establece los oyentes.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAboutAppBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()
        setListener()
    }

    /**
     * Establece el oyente para el botón de retroceso.
     */
    private fun setListener() {
        binding.ibAboutBack.setOnClickListener {
            finish()
        }

    }


}
// enlace a JAVADOC?
