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
import com.example.quevemoshoy.R
import com.example.quevemoshoy.databinding.ActivityRegister3Binding
import com.example.quevemoshoy.main.MainActivity2
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.database.FirebaseDatabase
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.shuhart.stepview.StepView
import java.lang.reflect.Type

class RegisterActivity3 : AppCompatActivity() {
    private lateinit var binding: ActivityRegister3Binding
    private lateinit var stepView: StepView
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


    private fun loadStepperFragment() {
        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        val miFragmento = StepperFragment.newInstance(2)
        fragmentTransaction.add(R.id.fragmentContainerView, miFragmento)
        fragmentTransaction.commit()
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

    private fun handleAuthentication() {
        val preferences = getSharedPreferences("Registro", Context.MODE_PRIVATE)
        val user = preferences.getString("user", "")
        val email = preferences.getString("email", "")
        val password = preferences.getString("password", "")
        val repeatPassword = preferences.getString("repeatPassword", "")
        val currentUser = FirebaseAuth.getInstance().currentUser

        if (currentUser!=null) {
            handleGoogleSignIn(preferences)
        } else {
            if (email != null&&password!=null) {
                handleEmailPasswordSignIn(email, password, preferences)
            }
        }



    }

    private fun handleGoogleSignIn(preferences: SharedPreferences) {
        val uid = FirebaseAuth.getInstance().currentUser?.uid

        if (uid != null) {
            savePreferencesToFirebaseWithGoogle(uid, preferences)
        }
        startActivity(Intent(this, LoginActivity::class.java))
    }

    private fun handleEmailPasswordSignIn(
        email: String,
        password: String,
        preferences: SharedPreferences
    ) {
        val auth = FirebaseAuth.getInstance()
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // User registration successful
                    val user = auth.currentUser
                    user?.sendEmailVerification()?.addOnCompleteListener { emailTask ->
                        if (emailTask.isSuccessful) {
                            // Email sent successfully
                            Toast.makeText(this, "Registro exitoso. Por favor, revisa tu correo electrónico para verificar tu cuenta.", Toast.LENGTH_LONG).show()
                            val uid = user.uid
                            savePreferencesToFirebaseWithEmail(uid, preferences)
                            preferences.edit().clear().apply()
                            startActivity(Intent(this, LoginActivity::class.java))
                        } else {
                            // Failed to send verification email
                            Log.d("RegisterActivity", "Error al enviar el correo de verificación", emailTask.exception)
                        }
                    }
                } else {
                    // User registration failed
                    when (task.exception) {
                        is FirebaseAuthUserCollisionException -> {
                            Toast.makeText(this, "Este correo electrónico ya está en uso.", Toast.LENGTH_SHORT).show()
                        }
                        is FirebaseAuthInvalidCredentialsException -> {
                            Toast.makeText(this, "El correo electrónico está mal formado.", Toast.LENGTH_SHORT).show()
                        }
                        else -> {
                            Log.d("RegisterActivity", "Registro fallido", task.exception)
                        }
                    }
                }
            }
    }


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
    override fun onPause() {
        super.onPause()
        saveProviderPreferences()
    }
    override fun onBackPressed() {
        startActivity(Intent(this, RegisterActivity2::class.java))
        saveProviderPreferences()
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
    }

}