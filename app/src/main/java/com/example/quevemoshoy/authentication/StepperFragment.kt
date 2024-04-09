package com.example.quevemoshoy.authentication

import android.animation.ValueAnimator
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.quevemoshoy.R
import com.shuhart.stepview.StepView



private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"


class StepperFragment : Fragment() {

    private var param1: String? = null
    private var param2: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_stepper, container, false)

        // Encuentra la vista StepView en tu layout
        val stepView = view.findViewById<StepView>(R.id.step_view)

        // Configura los pasos
        stepView.setSteps(listOf("Datos usuario", "Preferencias", "Proveedores"))

        // Establece el paso actual
        val currentStep = arguments?.getInt("currentStep", 0) ?: 0
        stepView.go(currentStep, false)

        // Configura el listener de clic en los pasos
        stepView.setOnStepClickListener { step ->
            // Aquí puedes manejar el clic en los pasos
            // Por ejemplo, puedes iniciar la actividad correspondiente
            when (step) {
                0 -> startActivity(Intent(context, RegisterActivity1::class.java))
                1 -> startActivity(Intent(context, RegisterActivity2::class.java))
                2 -> startActivity(Intent(context, RegisterActivity3::class.java))
                //guardar preferencias.
            }


        }

        return view
    }




    companion object {
        @JvmStatic
        fun newInstance(currentStep: Int) =
            StepperFragment().apply {
                arguments = Bundle().apply {
                    putInt("currentStep", currentStep)
                }
            }
    }

}







// ...


