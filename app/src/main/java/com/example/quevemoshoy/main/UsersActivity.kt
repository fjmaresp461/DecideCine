package com.example.quevemoshoy.main

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.widget.ImageButton
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.quevemoshoy.R
import com.example.quevemoshoy.authentication.StepperFragment
import com.example.quevemoshoy.databinding.ActivitySettingsBinding
import com.example.quevemoshoy.databinding.ActivityUsersBinding

class UsersActivity : AppCompatActivity() {
    private lateinit var binding: ActivityUsersBinding


    // Dentro de UsersActivity

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUsersBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()
        setAnimations()

    }

    private fun setAnimations() {
        val optionsFragment =
            supportFragmentManager.findFragmentById(R.id.fragmentContainerView2) as OptionsFragment
        optionsFragment.view?.findViewById<ImageButton>(R.id.ib_list)?.setOnClickListener {
            startActivity(Intent(this, MainActivity2::class.java))
            this.overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
        }
        optionsFragment.view?.findViewById<ImageButton>(R.id.ib_settings)?.setOnClickListener {
            startActivity(Intent(this, SettingsActivity::class.java))
            this.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
        }
        optionsFragment.view?.findViewById<ImageButton>(R.id.ib_users)?.isClickable=false
    }
}

//Quiero un metodo que alterne el fondo de color de transparente a gris0.5f alpha en funcion si se está en esa activity o no(pintado estamos, transparente no)
//(ibList->mainActivity2,ibUsers->UserActivity,ibSettings->SettingsActivity)
//aparte de alternar el color de fondo, alternaremos que se pueda clickar, es decir, si estoy en iblist(mainActivity2), estara blanco y no clickable,
// si pulsamos otro, volvera a su estado original (transparente y clickable)
//indica exactamnte donde realizas las modificacions(fragment, o activities)


