package com.example.quevemoshoy.main

import android.content.ContentValues.TAG
import android.content.Intent
import android.content.Intent.FLAG_ACTIVITY_CLEAR_TASK
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.util.Log
import android.widget.ImageButton
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.quevemoshoy.AboutAppActivity
import com.example.quevemoshoy.LoginActivity
import com.example.quevemoshoy.R
import com.example.quevemoshoy.databinding.ActivitySettingsBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

/**
 * `SettingsActivity` es una actividad que proporciona opciones de configuración al usuario.
 *
 * Esta actividad permite al usuario cerrar sesión, obtener información sobre la aplicación y eliminar su cuenta.
 *
 * @property binding Enlace de la actividad con su vista.
 * @property auth Autenticación de Firebase.
 *
 * @constructor Crea una instancia de `SettingsActivity`.
 */
class SettingsActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySettingsBinding
    private lateinit var auth: FirebaseAuth

    /**
     * Se llama cuando se crea la actividad. Inicializa la vista, la autenticación de Firebase, las animaciones y los oyentes.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        auth = FirebaseAuth.getInstance()
        supportActionBar?.hide()
        setAnimations()
        setListeners()
    }

    /**
     * Establece los oyentes para los contenedores de la interfaz.
     */
    private fun setListeners() {
        binding.cntLogout.setOnClickListener {
            auth.signOut()
            finish()
            val intent = Intent(this, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        }
        binding.cntAbout.setOnClickListener {
            startActivity(Intent(this, AboutAppActivity::class.java))

        }
        binding.cntDelAcc.setOnClickListener {
            showDeleteAccountDialog()
        }
    }

    /**
     * Muestra un cuadro de diálogo para confirmar la eliminación de la cuenta del usuario.
     */
    private fun showDeleteAccountDialog() {
        val userId = FirebaseAuth.getInstance().currentUser?.uid
        if (userId != null) {
            AlertDialog.Builder(this).setTitle("Eliminar cuenta")
                .setMessage("¿Estás seguro de que quieres eliminar tu cuenta? Esta acción no se puede deshacer.")
                .setPositiveButton("Sí") { _, _ ->
                    deleteUserAccount(userId)
                    auth.signOut()
                    startActivity(Intent(this, LoginActivity::class.java))
                }.setNegativeButton("No", null).show()
        } else {
            Log.e(TAG, "Usuario no autenticado")
        }

    }

    /**
     * Elimina la cuenta del usuario de la base de datos.
     *
     * @param userId El ID del usuario a eliminar.
     */
    private fun deleteUserAccount(userId: String) {
        val database = FirebaseDatabase.getInstance()
        val userRef = database.getReference("users/$userId")
        userRef.removeValue().addOnSuccessListener {
            Log.d(TAG, "Usuario eliminado exitosamente")
        }.addOnFailureListener { e ->
            Log.e(TAG, "Error al eliminar usuario", e)
        }
    }

    /**
     * Establece las animaciones para los botones de la interfaz.
     */
    private fun setAnimations() {
        val optionsFragment =
            supportFragmentManager.findFragmentById(R.id.fragmentContainerView2) as OptionsFragment
        optionsFragment.view?.findViewById<ImageButton>(R.id.ib_list)?.setOnClickListener {
            intent = Intent(this, MainActivity2::class.java)

            startActivity(intent)
            this.overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
        }
        optionsFragment.view?.findViewById<ImageButton>(R.id.ib_users)?.setOnClickListener {
            startActivity(Intent(this, UsersActivity::class.java))
            this.overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
        }
        optionsFragment.view?.findViewById<ImageButton>(R.id.ib_genres)?.setOnClickListener {
            startActivity(Intent(this, AllGenresActivity::class.java))
            this.overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
        }
        optionsFragment.view?.findViewById<ImageButton>(R.id.ib_settings)?.isClickable = false
    }
    /**
     * Se llama cuando se presiona el botón de retroceso. Inicia la actividad `MainActivity2`.
     */
    override fun onBackPressed() {

    }
}