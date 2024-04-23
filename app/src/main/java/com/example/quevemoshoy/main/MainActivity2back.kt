package com.example.quevemoshoy.main

import androidx.appcompat.app.AppCompatActivity


class MainActivity2back : AppCompatActivity() {
   /* private lateinit var binding: ActivityMain2Binding
    private lateinit var auth: FirebaseAuth
    private val dbManager = DatabaseManager()
    private val moviesManager = MoviesManager()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMain2Binding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.title = ""
        auth = Firebase.auth
        setListeners()
setAnimations()
    }

    private fun setAnimations() {
        val optionsFragment =
            supportFragmentManager.findFragmentById(R.id.fragmentContainerView) as OptionsFragment
        optionsFragment.view?.findViewById<ImageButton>(R.id.ib_settings)?.setOnClickListener {
            startActivity(Intent(this, SettingsActivity::class.java))
            this.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
        }
        optionsFragment.view?.findViewById<ImageButton>(R.id.ib_users)?.setOnClickListener {
            startActivity(Intent(this, UsersActivity::class.java))
            this.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
        }
        optionsFragment.view?.findViewById<ImageButton>(R.id.ib_list)?.isClickable=false

    }

    private fun setListeners() {

        binding.ibRecomended.setOnClickListener {
            lifecycleScope.launch(Dispatchers.IO) {
                // Check if currentUser is not null before accessing preferences
                val currentUser = FirebaseAuth.getInstance().currentUser
                if (currentUser != null) {
                    val userGenrePreferences = moviesManager.getAllGenrePreferences(currentUser, 7, 10)
                    getRecommendedMovies(userGenrePreferences, "recommended")
                } else {
                    // Handle the case where no user is signed in (e.g., show a message)
                    Log.w("MoviesManager", "No user signed in, cannot retrieve preferences")
                }
            }
        }

        binding.ibSurprise.setOnClickListener {
            lifecycleScope.launch(Dispatchers.IO) {
                // Check if currentUser is not null before accessing preferences
                val currentUser = FirebaseAuth.getInstance().currentUser
                if (currentUser != null) {
                    val userGenrePreferences = moviesManager.getAllGenrePreferences(currentUser, 4, 7)
                    getRecommendedMovies(userGenrePreferences, "surprise")
                } else {
                    // Handle the case where no user is signed in (e.g., show a message)
                    Log.w("MoviesManager", "No user signed in, cannot retrieve preferences")
                }
            }
        }

    }



    suspend fun getRecommendedMovies(userGenrePreferences: Map<String, Int>, recommendationType: String) {
        withContext(Dispatchers.IO) {
            try {
                val movies = moviesManager.fetchMovies(userGenrePreferences, recommendationType)
                withContext(Dispatchers.Main) {
                    if (movies.isNotEmpty()) {
                        val intent =
                            Intent(this@MainActivity2back, RecyclerActivity::class.java).apply {
                                putExtra("movies", ArrayList(movies))
                            }
                        startActivity(intent)
                    } else {
                        Toast.makeText(this@MainActivity2back, R.string.no_matching_movies, Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: Exception) {
                Log.e("MainActivity", R.string.error_fetching.toString(), e)
            }
        }
    }


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_options, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.item_edit_profile_preferences -> {
                val intent = Intent(this, PreferencesActivity::class.java)
                intent.putExtra("skipFragment", false)
                startActivity(intent)
            }

            R.id.item_restore -> {
                AlertDialog.Builder(this)
                    .setTitle("Restaurar a valores predeterminados")
                    .setMessage("¿Estás seguro de que quieres restaurar tus preferencias a los valores predeterminados?")
                    .setPositiveButton("Sí") { _, _ ->
                        dbManager.deleteAll()
                        lifecycleScope.launch {
                            Dispatchers.Main
                            moviesManager.resetPreferences()
                        }

                    }
                    .setNegativeButton("No", null)
                    .show()
            }

            R.id.item_exit -> {
                finishAffinity()
            }

            R.id.item_sign_out -> {
                auth.signOut()
                finish()
                startActivity(Intent(this, LoginActivity::class.java))
            }

            R.id.about_author -> {
                startActivity(Intent(this, AboutAppActivity::class.java))
            }

            R.id.item_map -> {
                startActivity(Intent(this, MapActivity::class.java))
            }
        }
        return super.onOptionsItemSelected(item)
    }*/
}
