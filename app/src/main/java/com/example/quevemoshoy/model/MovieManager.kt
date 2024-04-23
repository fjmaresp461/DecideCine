package com.example.quevemoshoy.model

import android.util.Log
import com.example.quevemoshoy.R
import com.example.quevemoshoy.provider.ApiClient
import com.example.quevemoshoy.provider.MovieInterface
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class MoviesManager {

    private val database = FirebaseDatabase.getInstance()
    private val reference = database.getReference("users")
    private val apiService = ApiClient.retrofit.create(MovieInterface::class.java)


    val addedMovieIds = mutableSetOf<Int>()

    suspend fun getAllGenrePreferences(currentUser: FirebaseUser?, minScore: Int, maxScore: Int): Map<String, Int> =
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
                                if (genrePreference in minScore..maxScore) {
                                    allGenrePreferences[genreId] = maxOf(genrePreference, allGenrePreferences.getOrDefault(genreId, genrePreference))
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

            allGenrePreferences
        }



    suspend fun fetchMoviesByGenre(genreId: String, score: Int, recommendationType: String, userGenrePreferences: Map<String, Int>): List<Movie> {
        val allMovies = mutableListOf<Movie>()
        val movieIndex = mutableMapOf<String, Int>()

        if ((recommendationType == "recommended" && score < 7) ||
            (recommendationType == "surprise" && (score < 4 || score >= 6))) return emptyList()

        val index = movieIndex.getOrDefault(genreId, 0)
        val response = apiService.getMoviesByGenres(
            apiKey = "2bb0ba9a57e9cdae9dd4f957fd27140a", genres = genreId
        )
        val movies = response.movies
        for (i in index until movies.size) {
            val movie = movies[i]
            if (movie.id !in addedMovieIds) {
                allMovies.add(movie)
                addedMovieIds.add(movie.id)
                movieIndex[genreId] = i + 1
                break
            }
        }

        return allMovies
    }

    suspend fun fetchMovies(userGenrePreferences: Map<String, Int>, recommendationType: String): List<Movie> {
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
                    }
                }
            } else {
                while (allMovies.size < 20) {
                    for ((genreId, score) in userGenrePreferences) {
                        if (allMovies.size >= 20) break
                        val moviesByGenre = fetchMoviesByGenre(genreId, score, recommendationType, userGenrePreferences)
                        for (movie in moviesByGenre) {
                            val providers = fetchMovieProviders(movie.id)
                            //if (providers?.any { it.providerId == netflixProviderId } == true) {
                                allMovies.add(movie)
                            //}
                        }
                    }
                }
            }

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
De firebase debe devolver un mapa unicamente con los generos que  valen 7 o mas
Hay que tener en cuenta que hay que modificar todos los metodos

Eliminar toddo lo de  surprise
mostrar 12 peliculas en el recycler, minimo 4 peliculas con proveedor
VOY LENTO, HAY QUE APRETAR

 */