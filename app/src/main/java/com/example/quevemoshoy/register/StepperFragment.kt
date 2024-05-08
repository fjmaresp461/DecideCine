package com.example.quevemoshoy.register


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
/**
 * `StepperFragment` es un fragmento que muestra un StepperView para navegar entre las etapas del proceso de registro.
 *
 * Este fragmento proporciona una interfaz para que el usuario vea en qué etapa del proceso de registro se encuentra y le permite navegar a las diferentes etapas.
 *
 * @property param1 El primer parámetro del fragmento.
 * @property param2 El segundo parámetro del fragmento.
 *
 * @constructor Crea una instancia de `StepperFragment`.
 */

class StepperFragment : Fragment() {

    private var param1: String? = null
    private var param2: String? = null
    /**
     * Se llama cuando se crea el fragmento. Inicializa los parámetros del fragmento.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }
    /**
     * Se llama para crear la vista del fragmento. Inicializa la vista, el StepperView y establece el oyente del StepperView.
     */
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_stepper, container, false)
        val stepView = view.findViewById<StepView>(R.id.step_view)
        stepView.setSteps(listOf("Datos usuario", "Preferencias", "Proveedores"))
        val currentStep = arguments?.getInt("currentStep", 0) ?: 0
        stepView.go(currentStep, false)

        stepView.setOnStepClickListener { step ->
            when (currentStep) {
                0 -> {
                    when (step) {
                        1, 2 -> {
                            val registerActivity1 = activity as? RegisterActivity1
                            if (registerActivity1?.checkErrors() == false ) {

                            }else {

                                val nextActivity =
                                    if (step == 1) RegisterActivity2::class.java else RegisterActivity3::class.java
                                startActivity(Intent(context, nextActivity))
                                activity?.overridePendingTransition(
                                    R.anim.slide_in_right,
                                    R.anim.slide_out_left
                                )
                            }
                        }
                    }
                }
                1 -> {
                    when (step) {
                        0 -> {
                            startActivity(Intent(context, RegisterActivity1::class.java))
                            activity?.overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
                        }
                        2 -> {
                            startActivity(Intent(context, RegisterActivity3::class.java))
                            activity?.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
                        }
                    }
                }
                2 -> {
                    when (step) {
                        0 -> {
                            startActivity(Intent(context, RegisterActivity1::class.java))
                            activity?.overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
                        }
                        1 -> {
                            startActivity(Intent(context, RegisterActivity2::class.java))
                            activity?.overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
                        }
                    }
                }
            }
        }

        return view
    }

    /**
     * Crea una nueva instancia de `StepperFragment` con el paso actual como argumento.
     *
     * @param currentStep El paso actual.
     * @return Una nueva instancia de `StepperFragment`.
     */
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



