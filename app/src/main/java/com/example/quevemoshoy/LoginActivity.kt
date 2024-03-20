package com.example.quevemoshoy

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatDelegate
import com.example.quevemoshoy.databinding.ActivityLoginBinding
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class LoginActivity : AppCompatActivity() {
    private val responseLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == RESULT_OK) {
                loginResult(it.data)
            } else {
                Toast.makeText(this, getString(R.string.user_cancelled), Toast.LENGTH_SHORT).show()
            }
        }
    lateinit var binding: ActivityLoginBinding
    lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupThemeSwitch()
        auth = Firebase.auth
        setListeners()
    }

    override fun onStart() {
        super.onStart()
        val user = auth.currentUser
        if (user != null) {
            checkPreferences(user)
        }
    }

    private fun setListeners() {
        binding.ibLogin.setOnClickListener {
            loginGoogle()
        }
    }

    private fun loginGoogle() {
        val googleConf = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.id_client_google)).requestEmail().build()
        val googleClient = GoogleSignIn.getClient(this, googleConf)
        googleClient.signOut()
        responseLauncher.launch(googleClient.signInIntent)
    }

    private fun loginResult(data: Intent?) {
        val task = GoogleSignIn.getSignedInAccountFromIntent(data)
        try {
            val account = task.getResult(ApiException::class.java)
            val credentials = GoogleAuthProvider.getCredential(account.idToken, null)
            FirebaseAuth.getInstance().signInWithCredential(credentials)
                .addOnCompleteListener {
                    if (it.isSuccessful) {
                        val user = FirebaseAuth.getInstance().currentUser
                        checkPreferences(user)
                    }
                }
        } catch (e: Exception) {
            Toast.makeText(this, "Error:" + e.message, Toast.LENGTH_SHORT).show()
        }
    }

    private fun checkPreferences(user: FirebaseUser?) {
        val database = FirebaseDatabase.getInstance()
        val userRef = database.getReference("preferences").child(user!!.uid)
        userRef.get().addOnSuccessListener {
            if (it.exists()) {
                goToMainActivity()
            } else {
                goToPreferencesActivity()
            }
        }
    }

    private fun goToPreferencesActivity() {
        val intent = Intent(this, PreferencesActivity::class.java)
        intent.putExtra("skipFragment", true)
        startActivity(intent)
    }

    private fun goToMainActivity() {
        startActivity(Intent(this, MainActivity2::class.java))
    }
    private fun setupThemeSwitch() {
        val sharedPref = getSharedPreferences("AppPreferences", Context.MODE_PRIVATE)
        binding.swDarkMode.isChecked = sharedPref.getBoolean("DarkMode", false)

        binding.swDarkMode.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                sharedPref.edit().putBoolean("DarkMode", true).apply()
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                sharedPref.edit().putBoolean("DarkMode", false).apply()
            }
        }

    }
}
