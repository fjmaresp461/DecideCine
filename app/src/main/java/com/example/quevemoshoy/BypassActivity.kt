package com.example.quevemoshoy

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

/**
 * `BypassActivity` es una actividad que inicia la actividad `LoadActivity` y finaliza.
 *
 * Esta actividad se utiliza para redirigir al usuario a la actividad `LoadActivity`.
 *
 * @constructor Crea una instancia de `BypassActivity`.
 */
class BypassActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        /**
         * Se llama cuando se crea la actividad. Inicia la actividad `LoadActivity` y finaliza.
         */
        setContentView(R.layout.activity_bypass)
        startActivity(Intent(this, LoadActivity::class.java))
        finish()
    }
}