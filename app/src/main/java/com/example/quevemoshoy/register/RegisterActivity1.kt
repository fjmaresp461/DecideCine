package com.example.quevemoshoy.register

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.quevemoshoy.LoginActivity
import com.example.quevemoshoy.R
import com.example.quevemoshoy.databinding.ActivityRegister1Binding
import com.google.firebase.auth.FirebaseAuth
/**
 * `RegisterActivity1` es una actividad que permite al usuario introducir sus datos de usuario durante el proceso de registro.
 *
 * Esta actividad proporciona una interfaz para que el usuario introduzca su nombre de usuario, correo electrónico y contraseña, y las guarda en las preferencias compartidas.
 *
 * @property binding Enlace de la actividad con su vista.
 * @property isGoogleSignIn Indica si el usuario está iniciando sesión con Google.
 *
 * @constructor Crea una instancia de `RegisterActivity1`.
 */
class RegisterActivity1 : AppCompatActivity() {
    private lateinit var binding: ActivityRegister1Binding
    private var isGoogleSignIn: Boolean = false
    /**
     * Se llama cuando se crea la actividad. Inicializa la vista, comprueba si el usuario está autenticado con Google, establece los oyentes, carga las preferencias y carga el fragmento del stepper.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegister1Binding.inflate(layoutInflater)
        setContentView(binding.root)
        if (FirebaseAuth.getInstance().currentUser != null) {
            hideEmailAndPasswordFields()
        }

        setListeners()
        loadPreferences()
        loadStepperFragment()
        supportActionBar?.hide()
    }
    /**
     * Oculta los campos de correo electrónico y contraseña si el usuario está autenticado con Google.
     */
    private fun hideEmailAndPasswordFields() {
        binding.tvEmail.visibility = View.GONE
        binding.etEmail.visibility = View.GONE
        binding.tvPassword.visibility = View.GONE
        binding.etPassword.visibility = View.GONE
        binding.tvRepeatPassword.visibility = View.GONE
        binding.etRepeatPassword.visibility = View.GONE
    }
    /**
     * Establece los oyentes para los botones de la interfaz.
     */
    private fun setListeners() {
        binding.btnContinue.setOnClickListener {
            if (checkErrors()) {
                savePreferences()
                startActivity(Intent(this, RegisterActivity2::class.java))
                this.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
            } else {
                Toast.makeText(this, R.string.missing_fields_message, Toast.LENGTH_SHORT).show()
            }
        }
        binding.tvLogin.setOnClickListener {
            FirebaseAuth.getInstance().signOut()
            startActivity(Intent(this, LoginActivity::class.java))
        }
    }
    /**
     * Carga las preferencias del usuario desde las preferencias compartidas y actualiza los campos de texto.
     */
    private fun loadPreferences() {
        val preferences = getSharedPreferences("Registro", MODE_PRIVATE)
        binding.etUser.setText(preferences.getString("user", ""))
        binding.etEmail.setText(preferences.getString("email", ""))
        binding.etPassword.setText(preferences.getString("password", ""))
        binding.etRepeatPassword.setText(preferences.getString("repeatPassword", ""))
    }
    /**
     * Guarda las preferencias del usuario en las preferencias compartidas.
     */
    private fun savePreferences() {
        val user = binding.etUser.text.toString().trim()
        val email = binding.etEmail.text.toString().trim()
        val password = binding.etPassword.text.toString().trim()
        val repeatPassword = binding.etRepeatPassword.text.toString().trim()

        if (password != repeatPassword) {
            binding.etRepeatPassword.error = getString(R.string.passwords_do_not_match)
            return
        }

        val preferences = getSharedPreferences("Registro", MODE_PRIVATE)
        with(preferences.edit()) {
            putString("user", user)
            putString("email", email)
            putString("password", password)
            putString("repeatPassword", repeatPassword)
            putBoolean("googleSignIn", isGoogleSignIn)
            apply()
        }
    }
    /**
     * Comprueba si los campos de texto son válidos y, en caso afirmativo, guarda las preferencias del usuario.
     */
    fun checkErrors(): Boolean {
        val user = binding.etUser.text.toString().trim()
        val email = binding.etEmail.text.toString().trim()
        val password = binding.etPassword.text.toString().trim()
        val repeatPassword = binding.etRepeatPassword.text.toString().trim()
        var isValid = true

        if (FirebaseAuth.getInstance().currentUser != null) {
            if (user.isBlank()) {
                binding.etUser.error =  getString(R.string.user_is_required)
                isValid = false
            }

        } else {
            if (user.isBlank()) {
                binding.etUser.error = getString(R.string.user_is_required)
                isValid = false
            }
            if (email.isBlank()) {
                binding.etEmail.error = getString(R.string.email_is_required)
                isValid = false
            }else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                binding.etEmail.error =  getString(R.string.invalid_email)
                isValid = false
            }else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                binding.etEmail.error = "El email no es válido"
                isValid = false
            }
            if (password.isBlank()) {
                binding.etPassword.error = getString(R.string.password_is_required)
                isValid = false
            }
            if (password.length < 8) {
                binding.etPassword.error =  getString(R.string.password_min_length)
                isValid = false
            }
            if (binding.etRepeatPassword.text.isNullOrBlank()) {
                binding.etRepeatPassword.error = getString(R.string.repeat_password_is_required)
                isValid = false
            }
            if (password != repeatPassword) {
                binding.etRepeatPassword.error = getString(R.string.passwords_do_not_match)
                isValid = false
            }
        }

        return isValid
    }
    /**
     * Carga el fragmento del stepper en la actividad.
     */
    private fun loadStepperFragment() {
        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        val myFragment = StepperFragment.newInstance(0)
        fragmentTransaction.add(R.id.fragmentContainerView, myFragment)
        fragmentTransaction.commit()
    }
    /**
     * Se llama cuando se pausa la actividad. Guarda las preferencias del usuario.
     */
    override fun onPause() {
        super.onPause()
        savePreferences()
    }
    /**
     * Se llama cuando se presiona el botón de retroceso. Cierra la sesión del usuario y redirige al usuario a la actividad `LoginActivity`.
     */
    @Deprecated("Deprecated")
    override fun onBackPressed() {
        super.onBackPressed()
        FirebaseAuth.getInstance().signOut()
        startActivity(Intent(this, LoginActivity::class.java))
    }
}
