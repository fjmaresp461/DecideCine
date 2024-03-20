package com.example.quevemoshoy.authentication

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.quevemoshoy.LoginActivity
import com.example.quevemoshoy.MainActivity2
import com.example.quevemoshoy.databinding.ActivityRegister3Binding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.lang.reflect.Type

class RegisterActivity3 : AppCompatActivity() {
    private lateinit var binding: ActivityRegister3Binding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegister3Binding.inflate(layoutInflater)
        setContentView(binding.root)
        setListeners()
        setInitialOpacity()
    }

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

        binding.btnPrev.setOnClickListener{
            finish()
        }

            binding.btnFinish.setOnClickListener {
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

                // Obtener el correo electrónico y la contraseña de las SharedPreferences
                val email = preferences.getString("email", "")
                val password = preferences.getString("password", "")

                if (email.isNullOrBlank() || password.isNullOrBlank()) {
                    // El usuario inició sesión con Google, guardar las preferencias del usuario en Firebase
                    val uid = FirebaseAuth.getInstance().currentUser?.uid
                    Toast.makeText(this, uid.toString(), Toast.LENGTH_SHORT).show()
                    if (uid != null) {
                        savePreferencesToFirebaseWithGoogle(uid, preferences)
                    }

                    // Navegar a LoginActivity
                    startActivity(Intent(this, LoginActivity::class.java))
                } else {
                    // El correo electrónico y la contraseña no están vacíos, intenta crear un usuario
                    FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(this) { task ->
                            if (task.isSuccessful) {
                                // Registro exitoso, ahora puedes guardar los datos del usuario en Firebase
                                val uid = FirebaseAuth.getInstance().currentUser?.uid

                                // Guardar todos los datos en Firebase
                                savePreferencesToFirebaseWithEmail(uid, preferences)

                                // Limpiar SharedPreferences para la próxima sesión de registro
                                editor.clear()
                                editor.apply()

                                // Navegar a LoginActivity
                                startActivity(Intent(this, LoginActivity::class.java))
                            } else {
                                // Registro fallido, muestra un mensaje de error
                                Log.d("RegisterActivity", "Registro fallido", task.exception)
                            }
                        }
                }
            }
    }

    private fun savePreferencesToFirebaseWithEmail(uid: String?, preferences: SharedPreferences) {
        val database = FirebaseDatabase.getInstance()
        val usersRef = database.getReference("preferences")

        val user = preferences.getString("user", "")
        val type: Type = object : TypeToken<Map<String, Int>>() {}.type
        val preferencias: Map<String, Int> = Gson().fromJson(preferences.getString("preferencias", ""), type)
        val proveedoresGuardados: Map<String, Boolean> = Gson().fromJson(preferences.getString("proveedores", ""), object : TypeToken<Map<String, Boolean>>() {}.type)

        val usuario = mutableMapOf<String, Any>()
        usuario["nombre"] = user ?: ""
        usuario["preferencias"] = preferencias

        if (uid != null && user != null) {
            usersRef.child(uid).child(user).setValue(usuario)
            usersRef.child(uid).child("proveedores").setValue(proveedoresGuardados)
        }
    }

    private fun savePreferencesToFirebaseWithGoogle(uid: String, preferences: SharedPreferences) {
        val database = FirebaseDatabase.getInstance()
        val usersRef = database.getReference("preferences")

        val user = preferences.getString("user", "")
        val type: Type = object : TypeToken<Map<String, Int>>() {}.type
        val preferencias: Map<String, Int> = Gson().fromJson(preferences.getString("preferencias", ""), type)
        val proveedoresGuardados: Map<String, Boolean> = Gson().fromJson(preferences.getString("proveedores", ""), object : TypeToken<Map<String, Boolean>>() {}.type)

        val usuario = mutableMapOf<String, Any>()
        usuario["nombre"] = user ?: ""
        usuario["preferencias"] = preferencias


        if (user != null) {
            usersRef.child(uid).child(user).setValue(usuario)
        }
            usersRef.child(uid).child("proveedores").setValue(proveedoresGuardados)

    }

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
    }