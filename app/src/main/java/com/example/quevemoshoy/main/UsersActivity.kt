package com.example.quevemoshoy.main


import android.content.Intent
import android.os.Bundle
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.quevemoshoy.PreferencesActivity
import com.example.quevemoshoy.R
import com.example.quevemoshoy.databinding.ActivityUsersBinding
import com.example.quevemoshoy.preferences.ProvidersActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class UsersActivity : AppCompatActivity() {
    private lateinit var binding: ActivityUsersBinding



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUsersBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()
        setAnimations()
        setListeners()

    }

    private fun setListeners() {
        binding.cntAdd.setOnClickListener {
            val builder = AlertDialog.Builder(this)
            builder.setTitle("Añadir usuario")
            builder.setMessage("Por favor, introduce un nombre")

            val input = EditText(this)
            builder.setView(input)

            builder.setPositiveButton("Aceptar") { dialog, _ ->
                val name = input.text.toString()
                doesUserExist(name) { exists ->
                    if (exists) {
                        Toast.makeText(this, "El nombre de usuario ya existe. Por favor, introduce un nombre diferente.", Toast.LENGTH_LONG).show()
                        input.text.clear()
                    } else {


                        val intent = Intent(this, PreferencesActivity::class.java)
                        intent.putExtra("firstTime", true)
                        intent.putExtra("user", name)
                        startActivity(intent)
                        dialog.dismiss()
                    }
                }
            }
            builder.setNegativeButton("Cancelar") { dialog, _ ->
                dialog.cancel()
            }

            builder.show()
        }

        binding.cntDel.setOnClickListener {
            val database = FirebaseDatabase.getInstance()
            val reference = database.getReference("users/6320iBIYtiVYymyrFMqRG7TcPKd2/preferencias")

            reference.get().addOnSuccessListener { dataSnapshot ->
                val userNames = dataSnapshot.children.map { it.key ?: "" }.filter { it.isNotEmpty() }

                val builder = AlertDialog.Builder(this)
                builder.setTitle("Eliminar usuario")
                builder.setItems(userNames.toTypedArray()) { _, which ->
                    val selectedUser = userNames[which]

                    val confirmBuilder = AlertDialog.Builder(this)
                    confirmBuilder.setTitle("Confirmar eliminación")
                    confirmBuilder.setMessage("¿Estás seguro de que quieres eliminar a $selectedUser?")
                    confirmBuilder.setPositiveButton("Sí") { _, _ ->
                        // Aquí se elimina el nodo y todo su contenido
                        reference.child(selectedUser).removeValue().addOnSuccessListener {
                            Toast.makeText(this, "$selectedUser ha sido eliminado.", Toast.LENGTH_LONG).show()
                        }.addOnFailureListener {
                            Toast.makeText(this, "Hubo un error al intentar eliminar a $selectedUser.", Toast.LENGTH_LONG).show()
                        }
                    }
                    confirmBuilder.setNegativeButton("No") { dialog, _ ->
                        dialog.cancel()
                    }
                    confirmBuilder.show()
                }
                builder.show()
            }



    }
        binding.cntEditPrefs.setOnClickListener {
            val database = FirebaseDatabase.getInstance()
            val userId= FirebaseAuth.getInstance().currentUser?.uid
            val reference = database.getReference("users/$userId/preferencias")

            reference.get().addOnSuccessListener { dataSnapshot ->
                val userNames = dataSnapshot.children.map { it.key ?: "" }.filter { it.isNotEmpty() }

                val builder = AlertDialog.Builder(this)
                builder.setTitle("Seleccionar usuario")
                builder.setItems(userNames.toTypedArray()) { _, which ->
                    val selectedUser = userNames[which]

                    val intent = Intent(this, PreferencesActivity::class.java)
                    intent.putExtra("firstTime", false)
                    intent.putExtra("user", selectedUser)
                    startActivity(intent)
                }
                builder.show()
            }
        }
        binding.cntEditProv.setOnClickListener{
            startActivity(Intent(this,ProvidersActivity::class.java))
        }


    }

    private fun doesUserExist(name: String, callback: (Boolean) -> Unit) {
        val database = FirebaseDatabase.getInstance("https://que-vemos-h-default-rtdb.europe-west1.firebasedatabase.app/")
        val preferencesRef = database.getReference("users/6320iBIYtiVYymyrFMqRG7TcPKd2/preferencias")
       preferencesRef.child(name).get().addOnSuccessListener { dataSnapshot ->
            callback(dataSnapshot.exists())
        }.addOnFailureListener {

        }
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




