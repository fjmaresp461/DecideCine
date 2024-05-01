package com.example.quevemoshoy.main


import android.content.DialogInterface
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
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class UsersActivity : AppCompatActivity() {
    private lateinit var binding: ActivityUsersBinding
    var database = FirebaseDatabase.getInstance()
    var user = FirebaseAuth.getInstance().currentUser
    var uid = user?.uid


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUsersBinding.inflate(layoutInflater)
        setContentView(binding.root)
         database = FirebaseDatabase.getInstance()
         user = FirebaseAuth.getInstance().currentUser
         uid = user?.uid
        supportActionBar?.hide()
        setAnimations()
        setListeners()

    }

    private fun setListeners() {
        binding.cntAdd.setOnClickListener {
            showAddUserDialog()
        }

        binding.cntDel.setOnClickListener {
            handleDeleteUser()
        }

        binding.cntEditPrefs.setOnClickListener {
            handleEditPreferences()
        }

        binding.cntUsersList.setOnClickListener {
            handleUsersList()
        }

        binding.cntEditProv.setOnClickListener{
            startActivity(Intent(this,ProvidersActivity::class.java))
        }
    }

    private fun showAddUserDialog() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle(getString(R.string.add_user_title))
        builder.setMessage(getString(R.string.add_user_message))

        val input = EditText(this)
        builder.setView(input)

        builder.setPositiveButton(getString(R.string.accept)) { dialog, _ ->
            handleAddUser(input, dialog)
        }
        builder.setNegativeButton(getString(R.string.cancel)) { dialog, _ ->
            dialog.cancel()
        }

        builder.show()
    }

    private fun handleAddUser(input: EditText, dialog: DialogInterface) {
        val name = input.text.toString()
        doesUserExist(name) { exists ->
            if (exists) {
                Toast.makeText(this, getString(R.string.user_exists_message), Toast.LENGTH_LONG).show()
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

    private fun handleDeleteUser() {
        val database = FirebaseDatabase.getInstance()
        val reference = database.getReference("users/$uid/preferencias")

        reference.get().addOnSuccessListener { dataSnapshot ->
            val userNames = dataSnapshot.children.map { it.key ?: "" }.filter { it.isNotEmpty() }

            if (userNames.size <= 1) {
                Toast.makeText(this, getString(R.string.minimum_user_message), Toast.LENGTH_LONG).show()
            } else {
                showDeleteUserDialog(userNames, reference)
            }
        }
    }

    private fun showDeleteUserDialog(userNames: List<String>, reference: DatabaseReference) {
        val builder = AlertDialog.Builder(this)
        builder.setTitle(getString(R.string.delete_user_title))
        builder.setItems(userNames.toTypedArray()) { _, which ->
            val selectedUser = userNames[which]

            val confirmBuilder = AlertDialog.Builder(this)
            confirmBuilder.setTitle(getString(R.string.confirm_delete_title))
            confirmBuilder.setMessage(getString(R.string.confirm_delete_message, selectedUser))
            confirmBuilder.setPositiveButton(getString(R.string.yes)) { _, _ ->
                deleteUser(selectedUser, reference)
            }
            confirmBuilder.setNegativeButton(getString(R.string.no)) { dialog, _ ->
                dialog.cancel()
            }
            confirmBuilder.show()
        }
        builder.show()
    }

    private fun deleteUser(selectedUser: String, reference: DatabaseReference) {
        reference.child(selectedUser).removeValue().addOnSuccessListener {
            Toast.makeText(this, getString(R.string.user_deleted_message, selectedUser), Toast.LENGTH_LONG).show()
        }.addOnFailureListener {
            Toast.makeText(this, getString(R.string.delete_error_message, selectedUser), Toast.LENGTH_LONG).show()
        }
    }

    private fun handleEditPreferences() {
        val reference = database.getReference("users/$uid/preferencias")

        reference.get().addOnSuccessListener { dataSnapshot ->
            val userNames = dataSnapshot.children.map { it.key ?: "" }.filter { it.isNotEmpty() }

            if (userNames.size == 1) {
                startPreferencesActivity(userNames[0])
            } else {
                showSelectUserDialog(userNames)
            }
        }
    }

    private fun showSelectUserDialog(userNames: List<String>) {
        val builder = AlertDialog.Builder(this)
        builder.setTitle(getString(R.string.select_user_title))
        builder.setItems(userNames.toTypedArray()) { _, which ->
            val selectedUser = userNames[which]
            startPreferencesActivity(selectedUser)
        }
        builder.show()
    }

    private fun startPreferencesActivity(selectedUser: String) {
        val intent = Intent(this, PreferencesActivity::class.java)
        intent.putExtra("firstTime", false)
        intent.putExtra("user", selectedUser)
        startActivity(intent)
    }

    private fun handleUsersList() {
        val reference = database.getReference("users/$uid/preferencias")

        reference.get().addOnSuccessListener { dataSnapshot ->
            val userNames = dataSnapshot.children.map { it.key ?: "" }.filter { it.isNotEmpty() }

            val builder = AlertDialog.Builder(this)
            builder.setTitle(getString(R.string.all_users))
            builder.setItems(userNames.toTypedArray()) { _, which ->
                val selectedUser = userNames[which]
                startPreferencesActivity(selectedUser)
            }
            builder.show()
        }
    }








    private fun doesUserExist(name: String, callback: (Boolean) -> Unit) {
        val database = FirebaseDatabase.getInstance("https://que-vemos-h-default-rtdb.europe-west1.firebasedatabase.app/")
        val preferencesRef = database.getReference("users/$uid/preferencias")
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