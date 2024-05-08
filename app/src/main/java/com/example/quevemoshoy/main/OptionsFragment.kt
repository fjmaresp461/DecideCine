package com.example.quevemoshoy.main

import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import com.example.quevemoshoy.R

class OptionsFragment : Fragment() {
    private lateinit var ibList: ImageButton
    private lateinit var ibUsers: ImageButton
    private lateinit var ibSettings: ImageButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {

        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_options, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        ibList = view.findViewById(R.id.ib_list)
        ibUsers = view.findViewById(R.id.ib_users)
        ibSettings = view.findViewById(R.id.ib_settings)
        mostrarSombra()
        setListeners()
    }

    private fun mostrarSombra() {
        ibList.backgroundTintList =
            ColorStateList.valueOf(Color.parseColor("#00000000"))
        ibUsers.backgroundTintList =
            ColorStateList.valueOf(Color.parseColor("#00000000"))
        ibSettings.backgroundTintList =
            ColorStateList.valueOf(Color.parseColor("#00000000"))

        if (this.activity is MainActivity2) {
            ibList.backgroundTintList =
                ColorStateList.valueOf(Color.parseColor("#FFD4E4A4"))
        }
        if (this.activity is UsersActivity) {
            ibUsers.backgroundTintList =
                ColorStateList.valueOf(Color.parseColor("#FFD4E4A4"))
        }
        if (this.activity is SettingsActivity) {
            ibSettings.backgroundTintList =
                ColorStateList.valueOf(Color.parseColor("#FFD4E4A4"))
        }
    }


    private fun setListeners() {
        ibList.setOnClickListener {
            ibList.isClickable = false

            ibList.backgroundTintList =
                ColorStateList.valueOf(Color.parseColor("#FFD4E4A4"))
            ibUsers.backgroundTintList =
                ColorStateList.valueOf(Color.parseColor("#00000000"))
            ibSettings.backgroundTintList =
                ColorStateList.valueOf(Color.parseColor("#00000000"))

            startActivity(Intent(context, MainActivity2::class.java))

        }

        ibUsers.setOnClickListener {
            it.backgroundTintList =
                ColorStateList.valueOf(Color.parseColor("#FFD4E4A4"))
            ibList.backgroundTintList =
                ColorStateList.valueOf(Color.parseColor("#00000000"))
            ibSettings.backgroundTintList =
                ColorStateList.valueOf(Color.parseColor("#00000000"))
            it.isClickable = false

            startActivity(Intent(context, UsersActivity::class.java))

        }

        ibSettings.setOnClickListener {
            it.backgroundTintList =
                ColorStateList.valueOf(Color.parseColor("#FFD4E4A4"))
            ibUsers.backgroundTintList =
                ColorStateList.valueOf(Color.parseColor("#00000000"))
            ibList.backgroundTintList =
                ColorStateList.valueOf(Color.parseColor("#00000000"))
            it.isClickable = false

            startActivity(Intent(context, SettingsActivity::class.java))

        }
    }

}
