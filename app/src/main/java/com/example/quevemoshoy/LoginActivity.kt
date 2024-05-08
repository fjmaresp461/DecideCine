package com.example.quevemoshoy

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import com.example.quevemoshoy.register.RegisterActivity1
import com.example.quevemoshoy.databinding.ActivityLoginBinding
import com.example.quevemoshoy.main.MainActivity2
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ktx.Firebase


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

        auth = Firebase.auth
        setListeners()
        getSharedPreferences("Registro", MODE_PRIVATE).edit().clear().apply()
        supportActionBar?.hide()
    }

    override fun onStart() {
        super.onStart()
        val user = auth.currentUser
        if (user != null) {
            checkPreferences(user)
        }
    }

    private fun setListeners() {
        binding.btnGoogleSignIn.setOnClickListener {
            loginGoogle()
        }
        binding.btnLogin.setOnClickListener{
            val email=binding.etEmail.text.toString().trim()
            val pass=binding.etPassword.text.toString().trim()

            if (email.isEmpty()) {
                Toast.makeText(this, "Por favor, introduce el correo electrónico.", Toast.LENGTH_SHORT).show()
            } else {
                auth.signInWithEmailAndPassword(email, pass).addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val user = auth.currentUser
                        checkPreferences(user)
                    } else {
                        Toast.makeText(this, "Authentication failed.", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }


        binding.tvForgottenPass.setOnClickListener{
            val email=binding.etEmail.text.toString().trim()
         if(email.isEmpty()){
             Toast.makeText(this, "Ingresa un email por favor", Toast.LENGTH_SHORT).show()
         }else{
             auth.sendPasswordResetEmail(email).addOnCompleteListener { task ->
                 if (task.isSuccessful) {
                     Toast.makeText(this, "Email Enviado, revisa tu correo", Toast.LENGTH_SHORT).show()
                 }else{
                     Toast.makeText(this, "Error.", Toast.LENGTH_SHORT).show()
                 }
             }
         }

        }

        binding.tvRegister.setOnClickListener{
            startActivity(Intent(this, RegisterActivity1::class.java))
            finish()
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
        val usersRef = database.getReference("users")

        user?.let {
            if (!it.isEmailVerified) {
                Toast.makeText(this, "Por favor, verifica tu correo electrónico.", Toast.LENGTH_SHORT).show()
                auth.signOut()
                return
            }

            val uidRef = usersRef.child(it.uid)
            uidRef.get().addOnSuccessListener { snapshot ->
                if (snapshot.exists()) {
                    goToMainActivity()
                } else {
                    goToRegisterActivity()
                }
            }
        }
    }

    private fun goToRegisterActivity() {
        val intent = Intent(this, RegisterActivity1::class.java)
        intent.putExtra("googleSignIn", true)
        startActivity(intent)
        finish()
    }

    private fun goToMainActivity() {
        intent = Intent(this, MainActivity2::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
    }
    override fun onResume() {
        super.onResume()

        getSharedPreferences("Registro", MODE_PRIVATE).edit().clear().apply()
    }

    override fun onBackPressed() {
        finishAffinity()
    }
}
