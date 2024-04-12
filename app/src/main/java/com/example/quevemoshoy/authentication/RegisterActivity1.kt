package com.example.quevemoshoy.authentication

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.example.quevemoshoy.LoginActivity
import com.example.quevemoshoy.R
import com.example.quevemoshoy.databinding.ActivityRegister1Binding
import com.google.firebase.auth.FirebaseAuth

class RegisterActivity1 : AppCompatActivity() {
    private lateinit var binding: ActivityRegister1Binding
    var isGoogleSignIn: Boolean = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegister1Binding.inflate(layoutInflater)
        setContentView(binding.root)
        isGoogleSignIn = intent.getBooleanExtra("googleSignIn", false)
        if(isGoogleSignIn){
            hideEmailAndPasswordFields()
        }
        setListeners()
        loadPreferences()
        loadStepperFragment()
        supportActionBar?.hide()
    }

    private fun checkErrors(): Boolean {
        val password = binding.etPassword.text.toString().trim()
        val repeatPassword = binding.etRepeatPassword.text.toString().trim()
        var isValid = true

        if (isGoogleSignIn && binding.etUser.text.isNullOrBlank()) {
            binding.etUser.error = "El usuario es obligatorio"
            isValid = false
        } else {
            if (binding.etUser.text.isNullOrBlank()) {
                binding.etUser.error = "El usuario es obligatorio"
                isValid = false
            }
            if (binding.etEmail.text.isNullOrBlank()) {
                binding.etEmail.error = "El email es obligatorio"
                isValid = false
            }
            if (binding.etPassword.text.isNullOrBlank()) {
                binding.etPassword.error = "La contraseña es obligatoria"
                isValid = false
            }
            if (binding.etRepeatPassword.text.isNullOrBlank()) {
                binding.etRepeatPassword.error = "Repetir contraseña es obligatorio"
                isValid = false
            }
            if (password != repeatPassword) {
                binding.etRepeatPassword.error = "Las contraseñas no coinciden"
                isValid = false
            }
        }

        return isValid
    }



    private fun hideEmailAndPasswordFields() {
        binding.tvEmail.visibility = View.GONE
        binding.etEmail.visibility = View.GONE
        binding.tvPassword.visibility = View.GONE
        binding.etPassword.visibility = View.GONE
        binding.tvRepeatPassword.visibility = View.GONE
        binding.etRepeatPassword.visibility = View.GONE
    }

    private fun setListeners() {
        binding.btnContinue.setOnClickListener {
            checkErrors()
            savePreferences()
            startActivity(Intent(this, RegisterActivity2::class.java))
            this.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
        }
        binding.tvLogin.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
        }
    }

    private fun loadPreferences() {
        val preferences = getSharedPreferences("Registro", MODE_PRIVATE)
        binding.etUser.setText(preferences.getString("user", ""))
        binding.etEmail.setText(preferences.getString("email", ""))
        binding.etPassword.setText(preferences.getString("password", ""))
        binding.etRepeatPassword.setText(preferences.getString("repeatPassword", ""))
    }

    private fun savePreferences() {
        val user = binding.etUser.text.toString().trim()
        val email = binding.etEmail.text.toString().trim()
        val password = binding.etPassword.text.toString().trim()
        val repeatPassword = binding.etRepeatPassword.text.toString().trim()

        if (password != repeatPassword) {
            binding.etRepeatPassword.error = "Las contraseñas no coinciden"
            return
        }

        val preferences = getSharedPreferences("Registro", MODE_PRIVATE)
        with(preferences.edit()) {
            putString("user", user)
            putString("email", email)
            putString("password", password)
            putString("repeatPassword", repeatPassword)
            apply()
        }
    }

    private fun loadStepperFragment() {
        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        val myFragment = StepperFragment.newInstance(0)
        fragmentTransaction.add(R.id.fragmentContainerView, myFragment)
        fragmentTransaction.commit()
    }


    override fun onPause() {
        super.onPause()
        savePreferences()
    }

    override fun onBackPressed() {

        startActivity(Intent(this, LoginActivity::class.java))
    }

}
