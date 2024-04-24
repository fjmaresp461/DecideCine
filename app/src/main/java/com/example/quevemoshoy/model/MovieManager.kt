package com.example.quevemoshoy.model

import android.util.Log
import com.example.quevemoshoy.R
import com.example.quevemoshoy.provider.ApiClient
import com.example.quevemoshoy.provider.MovieInterface
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class MoviesManager {

    private val database = FirebaseDatabase.getInstance()
    private val reference = database.getReference("users")
    private val apiService = ApiClient.retrofit.create(MovieInterface::class.java)
    val auth = FirebaseAuth.getInstance()
    val currentUser = auth.currentUser

    val addedMovieIds = mutableSetOf<Int>()

    suspend fun getAllGenrePreferences(currentUser: FirebaseUser?): List<String> =
        withContext(Dispatchers.IO) {
            val allGenrePreferences = mutableMapOf<String, Int>()

            val userId = checkNotNull(currentUser?.uid) { "User ID cannot be null" }

            try {
                val userSnapshot = reference.child(userId).child("preferencias").get().await()
                if (userSnapshot != null) {
                    if (userSnapshot.exists()) {
                        Log.d("Firebase", "User snapshot retrieved successfully")
                        for (usernameSnapshot in userSnapshot.children) {
                            for (genreSnapshot in usernameSnapshot.children) {
                                val genreId = genreSnapshot.key ?: continue
                                val genrePreference = genreSnapshot.getValue(Int::class.java) ?: continue
                                Log.d("Firebase", "Genre ID: $genreId, Preference: $genrePreference")
                                if (genrePreference >= 7) {
                                    allGenrePreferences[genreId] = maxOf(genrePreference, allGenrePreferences.getOrDefault(genreId, genrePreference))
                                    Log.d("general", allGenrePreferences.toString())

                                }
                            }
                        }
                    } else {
                        Log.w("Firebase", "Usuario no encontrado con UID: $userId")
                    }
                }
            } catch (e: Exception) {
                Log.e("Firebase", "Error getting data:", e)
            }

            Log.d("Firebase", "All genre preferences: $allGenrePreferences")

            allGenrePreferences.keys.toList()
        }




    suspend fun fetchMoviesByGenre(): List<Movie> {
        val allMovies = mutableListOf<Movie>()
        val genreIds = getAllGenrePreferences(currentUser)
        val genreMovies = mutableMapOf<String, MutableList<Movie>>()
        val netflixProviderId = 8 // Asumiendo que 8 es el ID del proveedor de Netflix

        for (genreId in genreIds) {
            val response = apiService.getMoviesByGenres(genres = genreId)
            val movies = response.movies.take(10).shuffled() // Mezcla las películas
            genreMovies[genreId] = movies.toMutableList()
        }

        for (genreId in genreIds) {
            val movies = genreMovies[genreId]
            var index = 0
            while (!movies.isNullOrEmpty() && index < movies.size && allMovies.size < 12) {
                val movie = movies[index]
                    val providers = fetchMovieProviders(movie.id) // Obtiene los proveedores de la película
                    if (providers?.any { it.providerId == netflixProviderId } == true) {
                        allMovies.add(movie)
                        addedMovieIds.add(movie.id)
                    }

                index++
            }
            if (allMovies.size == 12) {
                break
            }
        }
        if (allMovies.size < 12) {
            val response = apiService.getLatestMovies()
            for (movie in response.movies) {
                if (movie.id !in allMovies.map { it.id }) {
                    allMovies.add(movie)
   if (allMovies.size == 12) {
                        break
                    }
                }
            }
        }

        return allMovies
    }








    suspend fun fetchMovies(recommendationType: String): List<Movie> {
        val allMovies = mutableListOf<Movie>()
        val netflixProviderId = 8
        val hboProviderId = 384

        try {
            if (recommendationType == "latest") {
                val response = apiService.getLatestMovies()
                for (movie in response.movies) {
                    val providers = fetchMovieProviders(movie.id)
                    if (providers?.any { it.providerId == netflixProviderId } == true) {
                        allMovies.add(movie)
                        if (allMovies.size == 12) {
                            break
                        }
                    }
                }
            } else {
                // En proceso
            }

            // Si no hay suficientes películas de los proveedores seleccionados, llena con otras películas
            if (allMovies.size < 12) {
                val response = apiService.getLatestMovies()
                for (movie in response.movies) {
                    if (movie.id !in allMovies.map { it.id }) {
                        allMovies.add(movie)
                        if (allMovies.size == 12) {
                            break
                        }
                    }
                }
            }
allMovies.shuffle()
            return allMovies
        } catch (e: Exception) {
            Log.e("MoviesManager", R.string.error_fetching.toString(), e)
            return emptyList()
        }
    }







    suspend fun fetchMovieProviders(movieId: Int): List<Providers>? {
        return try {
            val response = apiService.getMovieProviders(movieId)
            Log.d("proveedores",response.toString())
            response.results.es?.providers
        } catch (e: Exception) {
            Log.e("MoviesManager", R.string.error_providers.toString(), e)
            null
        }
    }



    suspend fun fetchMovieById(movieId: Int): Movie? {
        try {
            val response = apiService.getMovieDetails(movieId, apiKey = ApiClient.API_KEY)
            return response
        } catch (e: Exception) {
            Log.e("MoviesManager", R.string.error_fetching.toString(), e)
            return null
        }
    }




}

/*


 */