package com.example.quevemoshoy

import android.content.Intent
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.os.Handler
import androidx.appcompat.app.AppCompatActivity
import com.example.quevemoshoy.main.MainActivity2
import com.google.firebase.auth.FirebaseAuth

/**
 * `LoadActivity` es una actividad que verifica el estado de inicio de sesión del usuario y redirige a la actividad correspondiente.
 *
 * Esta actividad verifica si el usuario está autenticado y, en función de ello, redirige al usuario a la actividad `LoginActivity` o `MainActivity2`.
 *
 * @property mAuth Autenticación de Firebase.
 * @property handler Manejador para ejecutar una acción después de un retraso.
 *
 * @constructor Crea una instancia de `LoadActivity`.
 */
class LoadActivity : AppCompatActivity() {

    private lateinit var mAuth: FirebaseAuth
    private val handler = Handler()

    /**
     * Se llama cuando se crea la actividad. Inicializa la vista, la autenticación de Firebase y verifica el estado de inicio de sesión del usuario después de un retraso.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)
        supportActionBar?.hide()
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        mAuth = FirebaseAuth.getInstance()
    }

    /**
     * Se llama cuando se reanuda la actividad. Verifica el estado de inicio de sesión del usuario después de un retraso.
     */
    override fun onResume() {
        super.onResume()
        handler.postDelayed({ checkUserLoginStatus() }, 3000)
    }

    /**
     * Verifica el estado de inicio de sesión del usuario y redirige a la actividad correspondiente.
     */
    private fun checkUserLoginStatus() {
        val firebaseAuth = FirebaseAuth.getInstance()
        if (firebaseAuth.currentUser == null) {
            startActivity(Intent(this, LoginActivity::class.java))
        } else {
            startActivity(Intent(this, MainActivity2::class.java))
        }
    }

}
