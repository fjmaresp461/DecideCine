package com.example.quevemoshoy.authentication

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.quevemoshoy.R
import com.example.quevemoshoy.databinding.ActivityRegister1Binding
import com.example.quevemoshoy.databinding.FragmentStepperBinding
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth

class RegisterActivity1 : AppCompatActivity() {
    private lateinit var binding: ActivityRegister1Binding
    private lateinit var inflater :LayoutInflater


    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        binding = ActivityRegister1Binding.inflate(layoutInflater)
        setContentView(binding.root)
        val isGoogleSignIn = intent.getBooleanExtra("googleSignIn", false)
        if (isGoogleSignIn) {
            binding.tvEmail.visibility = View.GONE
            binding.etEmail.visibility = View.GONE
            binding.tvPassword.visibility = View.GONE
            binding.etPassword.visibility = View.GONE
            binding.tvRepeatPassword.visibility = View.GONE
            binding.etRepeatPassword.visibility = View.GONE
        }
        setListeners()

        val preferences = getSharedPreferences("Registro", MODE_PRIVATE)
        val user = preferences.getString("user", "")
        val email = preferences.getString("email", "")
        val password = preferences.getString("password", "")
        val repeatPassword = preferences.getString("repeatPassword", "")

        binding.etUser.setText(user)
        binding.etEmail.setText(email)
        binding.etPassword.setText(password)
        binding.etRepeatPassword.setText(repeatPassword)

        val uid = FirebaseAuth.getInstance().currentUser?.uid
        Toast.makeText(this, uid.toString(), Toast.LENGTH_SHORT).show()


        // Obtén el supportFragmentManager
        val fragmentManager = supportFragmentManager

// Inicia una transacción
        val fragmentTransaction = fragmentManager.beginTransaction()

// Crea una instancia de tu fragmento
        val miFragmento = StepperFragment.newInstance(0)

// Añade el fragmento a tu actividad
        fragmentTransaction.add(R.id.fragmentContainerView, miFragmento)

// Confirma la transacción
        fragmentTransaction.commit()

        supportActionBar?.hide()
    }

    private fun setListeners() {
        binding.btnContinue.setOnClickListener {
            val user =binding.etUser.text.toString().trim()
            val email = binding.etEmail.text.toString().trim()
            val password = binding.etPassword.text.toString().trim()
            val repeatPassword = binding.etPassword.text.toString().trim()

            if (password != binding.etRepeatPassword.text.toString().trim()) {
                binding.etRepeatPassword.error = "Las contraseñas no coinciden"
                return@setOnClickListener
            }



            val preferences = getSharedPreferences("Registro", MODE_PRIVATE)
            val editor = preferences.edit()
            editor.putString("user", user)
            editor.putString("email", email)
            editor.putString("password", password)
            editor.putString("repeatPassword", repeatPassword)
            editor.apply()
            val intent = Intent(this, RegisterActivity2::class.java)
            startActivity(intent)
        }
    }
    override fun onBackPressed() {
        val preferences = getSharedPreferences("Registro", MODE_PRIVATE)
        val editor = preferences.edit()
        editor.clear()
        editor.apply()

        super.onBackPressed()
    }
}


