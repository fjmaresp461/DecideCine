package com.example.quevemoshoy.preferences

import android.content.ContentValues.TAG
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import com.example.quevemoshoy.R
import com.example.quevemoshoy.authentication.RegisterActivity2
import com.example.quevemoshoy.databinding.ActivityProvidersBinding
import com.example.quevemoshoy.main.MainActivity2
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.getValue

class ProvidersActivity : AppCompatActivity() {
    private lateinit var binding: ActivityProvidersBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProvidersBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setListeners()
        loadPreferencesFromFirebase()
        supportActionBar?.hide()
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

        binding.btnFinish.setOnClickListener {
            saveProviderPreferencesToFirebase()
            startActivity(Intent(this, MainActivity2::class.java))
            this.overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
        }
    }

    private fun loadPreferencesFromFirebase() {
        val uid = "your_user_id" // Reemplaza esto con el ID de usuario correspondiente
        val database = FirebaseDatabase.getInstance()
        val myRef = database.getReference("users/$uid/proveedores")

        myRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val providerValues = dataSnapshot.getValue<Map<String, Boolean>>()

                providerValues?.let {
                    for ((providerId, isSelected) in it) {
                        val button = findImageButtonByProviderId(providerId)
                        button?.isSelected = isSelected
                        button?.alpha = if (isSelected) 1.0f else 0.5f
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                // Failed to read value
                Log.w(TAG, "Failed to read value.", error.toException())
            }
        })
    }

    private fun saveProviderPreferencesToFirebase() {
        val uid = "your_user_id" // Reemplaza esto con el ID de usuario correspondiente
        val database = FirebaseDatabase.getInstance()
        val myRef = database.getReference("users/$uid/proveedores")

        val providerValues = mapOf(
            "netflix" to binding.ibNetflix.isSelected,
            "amazonPrimeVideo" to binding.ibAmazonPrimeVideo.isSelected,
            "disneyPlus" to binding.ibDisneyPlus.isSelected,
            "hboMax" to binding.ibHboMax.isSelected,
            "movistarPlus" to binding.ibMovistarPlus.isSelected,
            "appleTv" to binding.ibAppleTv.isSelected,
            "rakutenTv" to binding.ibRakutenTv.isSelected,
            "skyshowtime" to binding.ibSkyshowtime.isSelected,
            "googlePlayMovies" to binding.ibGooglePlayMovies.isSelected,
            "crunchyroll" to binding.ibCrunchyroll.isSelected
        )

        myRef.setValue(providerValues)
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
