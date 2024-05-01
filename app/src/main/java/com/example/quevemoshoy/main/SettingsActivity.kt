package com.example.quevemoshoy.main

import android.content.ContentValues.TAG
import android.content.Intent
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

class SettingsActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySettingsBinding
    private lateinit var auth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        auth = FirebaseAuth.getInstance()
        supportActionBar?.hide()
        setAnimations()
        setListeners()
    }

    private fun setListeners() {
        binding.cntLogout.setOnClickListener{
            auth.signOut()
            finish()
            startActivity(Intent(this, LoginActivity::class.java))
        }
        binding.cntAbout.setOnClickListener {
            startActivity(Intent(this, AboutAppActivity::class.java))

        }
        binding.cntDelAcc.setOnClickListener {
            showDeleteAccountDialog()
        }
    }

    private fun showDeleteAccountDialog() {
        val userId = FirebaseAuth.getInstance().currentUser?.uid
        if (userId != null) {
            AlertDialog.Builder(this)
                .setTitle("Eliminar cuenta")
                .setMessage("¿Estás seguro de que quieres eliminar tu cuenta? Esta acción no se puede deshacer.")
                .setPositiveButton("Sí") { _, _ ->
                    deleteUserAccount(userId)
                    auth.signOut()
                    startActivity(Intent(this, LoginActivity::class.java))
                }
                .setNegativeButton("No", null)
                .show()
        } else {
            Log.e(TAG, "Usuario no autenticado")
        }

    }

    private fun deleteUserAccount(userId: String) {
        val database = FirebaseDatabase.getInstance()
        val userRef = database.getReference("users/$userId")
        userRef.removeValue().addOnSuccessListener {
            Log.d(TAG, "Usuario eliminado exitosamente")
        }.addOnFailureListener { e ->
            Log.e(TAG, "Error al eliminar usuario", e)
        }
    }

    private fun setAnimations() {
        val optionsFragment =
            supportFragmentManager.findFragmentById(R.id.fragmentContainerView2) as OptionsFragment
        optionsFragment.view?.findViewById<ImageButton>(R.id.ib_list)?.setOnClickListener {
            startActivity(Intent(this, MainActivity2::class.java))
            this.overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
        }
        optionsFragment.view?.findViewById<ImageButton>(R.id.ib_users)?.setOnClickListener {
            startActivity(Intent(this, UsersActivity::class.java))
            this.overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
        }
        optionsFragment.view?.findViewById<ImageButton>(R.id.ib_settings)?.isClickable=false
    }
}