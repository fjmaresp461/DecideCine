package com.example.quevemoshoy.register

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.quevemoshoy.LoginActivity
import com.example.quevemoshoy.R
import com.example.quevemoshoy.databinding.ActivityRegister3Binding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.database.FirebaseDatabase
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

import java.lang.reflect.Type

/**
 * `RegisterActivity3` es una actividad que permite al usuario gestionar sus preferencias de proveedores durante el proceso de registro.
 *
 * Esta actividad proporciona una interfaz para que el usuario ajuste sus preferencias de proveedores y las guarda en las preferencias compartidas.
 *
 * @property binding Enlace de la actividad con su vista.
 *
 * @constructor Crea una instancia de `RegisterActivity3`.
 */
class RegisterActivity3 : AppCompatActivity() {
    private lateinit var binding: ActivityRegister3Binding

    /**
     * Se llama cuando se crea la actividad. Inicializa la vista, establece los oyentes, establece la opacidad inicial, carga las preferencias y carga el fragmento del stepper.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegister3Binding.inflate(layoutInflater)
        setContentView(binding.root)
        setListeners()
        setInitialOpacity()
        loadPreferences()
        loadStepperFragment()
        supportActionBar?.hide()
    }

    /**
     * Carga las preferencias del usuario desde las preferencias compartidas y actualiza los botones de imagen.
     */
    private fun loadPreferences() {
        val preferences = getSharedPreferences("Registro", Context.MODE_PRIVATE)
        val jsonString = preferences.getString("proveedores", "")
        if (!jsonString.isNullOrEmpty()) {
            val type: Type = object : TypeToken<Map<String, Boolean>>() {}.type
            val proveedores: Map<String, Boolean> = Gson().fromJson(jsonString, type)
            for ((providerId, isSelected) in proveedores) {
                val button = findImageButtonByProviderId(providerId)
                button?.isSelected = isSelected
                if (isSelected) {
                    button?.alpha = 1.0f
                }

            }
        }
    }

    /**
     * Encuentra un botón de imagen por el ID del proveedor.
     *
     * @param providerId El ID del proveedor.
     * @return El botón de imagen correspondiente al ID del proveedor.
     */
    private fun findImageButtonByProviderId(providerId: String): ImageButton? {
        return when (providerId) {
            "netflix" -> binding.ibNetflix
            "amazonPrimeVideo" -> binding.ibAmazonPrimeVideo
            "disneyPlus" -> binding.ibDisneyPlus
            "hboMax" -> binding.ibHboMax
            "movistarPlus" -> binding.ibMovistarPlus
            "appleTv" -> binding.ibAppleTv
            "rakutenTv" -> binding.ibRakutenTv
            "skyshowtime" -> binding.ibSkyshowtime
            "googlePlayMovies" -> binding.ibGooglePlayMovies
            "crunchyroll" -> binding.ibCrunchyroll
            else -> null
        }
    }

    /**
     * Carga el fragmento del stepper en la actividad.
     */
    private fun loadStepperFragment() {
        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        val miFragmento = StepperFragment.newInstance(2)
        fragmentTransaction.add(R.id.fragmentContainerView, miFragmento)
        fragmentTransaction.commit()
    }

    /**
     * Establece los oyentes para los botones de la interfaz.
     */
    private fun setListeners() {
        val clickListener = { button: ImageButton ->
            button.isSelected = !button.isSelected
            button.alpha = if (button.isSelected) 1.0f else 0.5f
        }

        binding.ibNetflix.setOnClickListener { clickListener(it as ImageButton) }
        binding.ibAmazonPrimeVideo.setOnClickListener { clickListener(it as ImageButton) }
        binding.ibDisneyPlus.setOnClickListener { clickListener(it as ImageButton) }
        binding.ibHboMax.setOnClickListener { clickListener(it as ImageButton) }
        binding.ibMovistarPlus.setOnClickListener { clickListener(it as ImageButton) }
        binding.ibAppleTv.setOnClickListener { clickListener(it as ImageButton) }
        binding.ibRakutenTv.setOnClickListener { clickListener(it as ImageButton) }
        binding.ibSkyshowtime.setOnClickListener { clickListener(it as ImageButton) }
        binding.ibGooglePlayMovies.setOnClickListener { clickListener(it as ImageButton) }
        binding.ibCrunchyroll.setOnClickListener { clickListener(it as ImageButton) }
        binding.btnPrev.setOnClickListener {

            startActivity(Intent(this, RegisterActivity2::class.java))
            saveProviderPreferences()
            this.overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
        }
        binding.btnFinish.setOnClickListener {
            saveProviderPreferences()
            handleAuthentication()
        }
    }

    /**
     * Guarda las preferencias del proveedor del usuario en las preferencias compartidas.
     */
    private fun saveProviderPreferences() {
        val preferences = getSharedPreferences("Registro", Context.MODE_PRIVATE)
        val editor = preferences.edit()
        val proveedores = mutableMapOf<String, Boolean>()
        proveedores["netflix"] = binding.ibNetflix.isSelected
        proveedores["amazonPrimeVideo"] = binding.ibAmazonPrimeVideo.isSelected
        proveedores["disneyPlus"] = binding.ibDisneyPlus.isSelected
        proveedores["hboMax"] = binding.ibHboMax.isSelected
        proveedores["movistarPlus"] = binding.ibMovistarPlus.isSelected
        proveedores["appleTv"] = binding.ibAppleTv.isSelected
        proveedores["rakutenTv"] = binding.ibRakutenTv.isSelected
        proveedores["skyshowtime"] = binding.ibSkyshowtime.isSelected
        proveedores["googlePlayMovies"] = binding.ibGooglePlayMovies.isSelected
        proveedores["crunchyroll"] = binding.ibCrunchyroll.isSelected
        editor.putString("proveedores", Gson().toJson(proveedores))
        editor.apply()
    }

    /**
     * Maneja la autenticación del usuario.
     */
    private fun handleAuthentication() {
        val preferences = getSharedPreferences("Registro", Context.MODE_PRIVATE)
        val email = preferences.getString("email", "")
        val password = preferences.getString("password", "")
        val currentUser = FirebaseAuth.getInstance().currentUser

        if (currentUser != null) {
            handleGoogleSignIn(preferences)
        } else {
            if (email != null && password != null) {
                handleEmailPasswordSignIn(email, password, preferences)
            }
        }


    }

    /**
     * Maneja el inicio de sesión con Google.
     */
    private fun handleGoogleSignIn(preferences: SharedPreferences) {
        val uid = FirebaseAuth.getInstance().currentUser?.uid

        if (uid != null) {
            savePreferencesToFirebaseWithGoogle(uid, preferences)
        }
        startActivity(Intent(this, LoginActivity::class.java))
        finish()
    }

    /**
     * Maneja el inicio de sesión con correo y contraseña.
     *
     * @param email Correo del usuario.
     * @param password Contraseña del usuario.
     * @param preferences Preferencias del usuario.
     */
    private fun handleEmailPasswordSignIn(
        email: String, password: String, preferences: SharedPreferences
    ) {
        val auth = FirebaseAuth.getInstance()
        auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val user = auth.currentUser
                    user?.sendEmailVerification()?.addOnCompleteListener { emailTask ->
                        if (emailTask.isSuccessful) {
                            Toast.makeText(
                                this,
                                R.string.registration_successful,
                                Toast.LENGTH_LONG
                            ).show()
                            val uid = user.uid
                            savePreferencesToFirebaseWithEmail(uid, preferences)
                            preferences.edit().clear().apply()
                            startActivity(Intent(this, LoginActivity::class.java))
                        } else {
                            Log.d(
                                "RegisterActivity",
                                getString(R.string.error_sending_verification_email),
                                emailTask.exception
                            )
                        }
                    }
                } else {
                    when (task.exception) {
                        is FirebaseAuthUserCollisionException -> {
                            Toast.makeText(
                                this, R.string.email_already_in_use, Toast.LENGTH_SHORT
                            ).show()
                        }

                        is FirebaseAuthInvalidCredentialsException -> {
                            Toast.makeText(
                                this, getString(R.string.email_is_malformed), Toast.LENGTH_SHORT
                            ).show()
                        }

                        else -> {
                            Log.d("RegisterActivity", getString(R.string.registration_failed), task.exception)
                        }
                    }
                }
            }
    }

    /**
     * Guarda las preferencias del usuario en Firebase usando correo electrónico.
     *
     * @param uid ID del usuario.
     * @param preferences Preferencias del usuario.
     */
    private fun savePreferencesToFirebaseWithEmail(uid: String?, preferences: SharedPreferences) {
        val database = FirebaseDatabase.getInstance()
        val usersRef = database.getReference("users")

        val user = preferences.getString("user", "")
        val type: Type = object : TypeToken<Map<String, Int>>() {}.type
        val preferencias: Map<String, Int> =
            Gson().fromJson(preferences.getString("preferencias", ""), type)
        val proveedoresGuardados: Map<String, Boolean> = Gson().fromJson(
            preferences.getString("proveedores", ""),
            object : TypeToken<Map<String, Boolean>>() {}.type
        )

        if (uid != null && user != null) {
            usersRef.child(uid).child("preferencias").child(user).setValue(preferencias)
            usersRef.child(uid).child("proveedores").setValue(proveedoresGuardados)
        }
    }

    /**
     * Guarda las preferencias del usuario en Firebase usando Google.
     *
     * @param uid ID del usuario.
     * @param preferences Preferencias del usuario.
     */
    private fun savePreferencesToFirebaseWithGoogle(uid: String, preferences: SharedPreferences) {
        val database = FirebaseDatabase.getInstance()
        val usersRef = database.getReference("users")

        val user = preferences.getString("user", "")
        val type: Type = object : TypeToken<Map<String, Int>>() {}.type
        val preferencias: Map<String, Int> =
            Gson().fromJson(preferences.getString("preferencias", ""), type)
        val proveedoresGuardados: Map<String, Boolean> = Gson().fromJson(
            preferences.getString("proveedores", ""),
            object : TypeToken<Map<String, Boolean>>() {}.type
        )

        if (user != null) {
            usersRef.child(uid).child("preferencias").child(user).setValue(preferencias)
            usersRef.child(uid).child("proveedores").setValue(proveedoresGuardados)
        }
    }

    /**
     * Establece la opacidad inicial de los botones de los proveedores de streaming.
     */

    private fun setInitialOpacity() {
        binding.ibNetflix.alpha = 0.5f
        binding.ibAmazonPrimeVideo.alpha = 0.5f
        binding.ibDisneyPlus.alpha = 0.5f
        binding.ibHboMax.alpha = 0.5f
        binding.ibMovistarPlus.alpha = 0.5f
        binding.ibAppleTv.alpha = 0.5f
        binding.ibRakutenTv.alpha = 0.5f
        binding.ibSkyshowtime.alpha = 0.5f
        binding.ibGooglePlayMovies.alpha = 0.5f
        binding.ibCrunchyroll.alpha = 0.5f
    }

    /**
     * Guarda las preferencias del proveedor cuando la actividad está en pausa.
     */
    override fun onPause() {
        super.onPause()
        saveProviderPreferences()
    }

    /**
     * Guarda las preferencias del proveedor y navega a RegisterActivity2 cuando se presiona el botón de retroceso.
     */
    @Deprecated("deprecated ")
    override fun onBackPressed() {
        super.onBackPressed()
        startActivity(Intent(this, RegisterActivity2::class.java))
        saveProviderPreferences()
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
    }

}