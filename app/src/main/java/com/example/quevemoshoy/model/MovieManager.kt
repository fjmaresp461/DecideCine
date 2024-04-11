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


    suspend fun getAllGenrePreferences(currentUser: FirebaseUser?, minScore: Int, maxScore: Int): Map<String, Int> =
        withContext(Dispatchers.IO) {
            val allGenrePreferences = mutableMapOf<String, Int>()

            val userId = checkNotNull(currentUser?.uid) { "User ID cannot be null" }

            try {
                Log.d("Firebase", "Getting user preferences for UID: $userId")
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
                                    allGenrePreferences[genreId] = genrePreference
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

            allGenrePreferences
        }















    suspend fun fetchMovies(
        userGenrePreferences: Map<String, Int>, recommendationType: String
    ): List<Movie> {
        val allMovies = mutableListOf<Movie>()
        val addedMovieIds = mutableSetOf<Int>()
        val movieIndex = mutableMapOf<String, Int>()

        try {
            while (allMovies.size < 12) {
                for ((genreId, score) in userGenrePreferences) {
                    if (allMovies.size >= 12) break
                    if ((recommendationType == "recommended" && score < 7) ||
                        (recommendationType == "surprise" && (score < 4 || score >= 7))) continue
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
                }
            }

            return allMovies
        } catch (e: Exception) {
            Log.e("MoviesManager", R.string.error_fetching.toString(), e)
            return emptyList()
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

    suspend fun fetchMovieProviders(movieId: Int): List<Providers>? {
        try {
            val response = apiService.getMovieProviders(movieId)
            return response.results.es.providers
        } catch (e: Exception) {
            Log.e("MoviesManager", R.string.error_providers.toString(), e)
            return null
        }
    }

    suspend fun resetPreferences() = withContext(Dispatchers.IO) {
        try {
            val snapshot = reference.child("preferencias").get().await()

            for (genreSnapshot in snapshot.children) {
                genreSnapshot.ref.setValue(5)
            }
        } catch (e: Exception) {
            Log.e("Firebase", R.string.error_updating_data.toString(), e)
        }
    }
}