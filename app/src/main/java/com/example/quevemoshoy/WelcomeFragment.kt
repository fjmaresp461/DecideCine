package com.example.quevemoshoy

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.*


class WelcomeFragment : Fragment() {


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        (activity as? PreferencesActivity)?.supportActionBar?.hide()
        return inflater.inflate(R.layout.fragment_welcome, container, false)

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val showFragment = activity?.intent?.getBooleanExtra("skipFragment", false) ?: false

        if (showFragment) {
            view.visibility=View.VISIBLE

        } else {
            view.visibility=View.GONE
            (activity as? AppCompatActivity)?.supportActionBar?.hide()
        }
        val btnStart = view.findViewById<Button>(R.id.btnStart)
        btnStart.setOnClickListener {
            (activity as? PreferencesActivity)?.supportActionBar?.show()

            (activity as? PreferencesActivity)?.removeFragment(this)
        }
    }
}
