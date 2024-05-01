package com.example.quevemoshoy.model

import android.content.Context
import android.content.Intent
import android.util.Log
import com.example.quevemoshoy.R
import com.example.quevemoshoy.RecyclerActivity
import com.example.quevemoshoy.database.DatabaseManager
import com.example.quevemoshoy.main.MainActivity2
import com.example.quevemoshoy.provider.ApiClient
import com.example.quevemoshoy.provider.MovieInterface
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers

import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext


class MoviesManager {

    private val database = FirebaseDatabase.getInstance()
    private val reference = database.getReference("users")
    private val apiService = ApiClient.retrofit.create(MovieInterface::class.java)
    val auth = FirebaseAuth.getInstance()
    val currentUser = auth.currentUser


    companion object {
        var moviesCache: List<Movie>? = null
        var genrePreferencesCache: List<String>? = null
        var latestMoviesCache: List<Movie>? = null
    }


    suspend fun getAllGenrePreferences(currentUser: FirebaseUser?): List<String> =
        withContext(Dispatchers.IO) {
            val allGenrePreferences = mutableMapOf<String, Int>()

            val userId = checkNotNull(currentUser?.uid) { "User ID cannot be null" }

            try {
                val userSnapshot = reference.child(userId).child("preferencias").get().await()
                if (userSnapshot != null) {
                    if (userSnapshot.exists()) {

                        for (usernameSnapshot in userSnapshot.children) {
                            for (genreSnapshot in usernameSnapshot.children) {
                                val genreId = genreSnapshot.key ?: continue
                                val genrePreference =
                                    genreSnapshot.getValue(Int::class.java) ?: continue

                                if (genrePreference >= 6) {
                                    allGenrePreferences[genreId] = maxOf(
                                        genrePreference,
                                        allGenrePreferences.getOrDefault(genreId, genrePreference)
                                    )
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

            // Ordena los géneros por puntuación en orden descendente
            val sortedGenrePreferences = allGenrePreferences.toList().sortedByDescending { it.second }.map { it.first }

            sortedGenrePreferences
        }

    suspend fun getPreferredProviders(currentUser: FirebaseUser?): List<Int> =
        withContext(Dispatchers.IO) {
            val preferredProviderIds = mutableListOf<Int>()

            // Mapeo de nombres de proveedores a IDs
            val providerNameToId = mapOf(
                "amazonPrimeVideo" to 119,
                "appleTv" to 2,
                "crunchyroll" to 283,
                "disneyPlus" to 337,
                "googlePlayMovies" to 3,
                "hboMax" to 384,
                "movistarPlus" to 339,
                "netflix" to 8,
                "rakutenTv" to 35,
                "skyshowtime" to 1773

            )

            val userId = checkNotNull(currentUser?.uid) { "User ID cannot be null" }

            try {
                val userSnapshot = reference.child(userId).child("proveedores").get().await()
                if (userSnapshot != null) {
                    if (userSnapshot.exists()) {

                        for (providerSnapshot in userSnapshot.children) {
                            val providerName = providerSnapshot.key ?: continue
                            val isPreferred =
                                providerSnapshot.getValue(Boolean::class.java) ?: continue
                            if (isPreferred) {
                                val providerId = providerNameToId[providerName]
                                if (providerId != null) {
                                    preferredProviderIds.add(providerId)
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

            return@withContext preferredProviderIds
        }


    suspend fun fetchMoviesByGenre(): List<Movie> {
        val currentUserGenrePreferences = getAllGenrePreferences(currentUser)
        val preferredProviderIds = getPreferredProviders(currentUser)

        if (genrePreferencesCache == currentUserGenrePreferences && !moviesCache.isNullOrEmpty()) {
            return moviesCache!!
        }
        genrePreferencesCache = currentUserGenrePreferences
        val allMovies = mutableListOf<Movie>()
        val genreMovies = mutableMapOf<String, MutableList<Movie>>()

        for (genreId in currentUserGenrePreferences) {
            val response = apiService.getMoviesByGenres(genres = genreId)
            val movies = response.movies.take(10).shuffled() // Mezcla las películas
            genreMovies[genreId] = movies.toMutableList()
        }

        var genreCount = 0
        for (genreId in currentUserGenrePreferences) {
            val movies = genreMovies[genreId]
            var index = 0
            while (!movies.isNullOrEmpty() && index < movies.size && allMovies.size < 12) {
                val movie = movies[index]
                val providers = fetchMovieProviders(movie.id)
                if (providers?.any { it.providerId in preferredProviderIds } == true) {
                    allMovies.add(movie)
                    if (++genreCount == 4) {
                        break
                    }
                }
                index++
            }
            if (allMovies.size == 12 || genreCount == 4) {
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

        moviesCache = allMovies

        return allMovies
    }




    suspend fun fetchMovies(recommendationType: String): List<Movie> {
        val allMovies = mutableListOf<Movie>()
        val favMovies = mutableListOf<Movie>()

        try {
            if (recommendationType == "latest") {
                if (!latestMoviesCache.isNullOrEmpty()) {
                    return latestMoviesCache!!
                }

                val response = apiService.getLatestMovies()
                for (movie in response.movies) {
                    val providers = fetchMovieProviders(movie.id)
                    if (providers?.any { it.providerId in getPreferredProviders(currentUser) } == true) {
                        allMovies.add(movie)
                        if (allMovies.size == 12) {
                            break
                        }
                    }
                }
            } else if (recommendationType == "myList") {
                favMovies.addAll(MainActivity2.favoriteMoviesList.filterNotNull())
                return favMovies
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

            // Actualiza la caché de las últimas películas
            latestMoviesCache = allMovies

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
            response.results.es.providers
        } catch (e: Exception) {
            Log.e("MoviesManager", R.string.error_providers.toString(), e)
            null
        }
    }


    suspend fun fetchMovieById(movieId: Int): Movie? {
        return try {
            val response = apiService.getMovieDetails(movieId, apiKey = ApiClient.API_KEY)
            response
        } catch (e: Exception) {
            Log.e("MoviesManager", R.string.error_fetching.toString(), e)
            null
        }
    }

    fun fetchAndStartActivity(context: Context, genreId: String) {
        CoroutineScope(Dispatchers.IO).launch {
            val movies = fetchMoviesByOneGenre(genreId)
            val isFromAllGenres= true
            withContext(Dispatchers.Main) {
                val intent = Intent(context, RecyclerActivity::class.java).apply {
                    putExtra("movies", ArrayList(movies))
                    putExtra("isFromAllGenres", isFromAllGenres)
                }
                context.startActivity(intent)
            }
        }
    }

    suspend fun fetchMoviesByOneGenre(genreId: String): List<Movie> {
        val response = apiService.getMoviesByGenres(genres = genreId)
        return response.movies.take(20) // Toma solo las primeras 20 películas
    }
}

data class UserPreferences(
    var genres: Map<String, Int> = mutableMapOf()
)
// empezar a buscar errores  testeando
// la interfaz main estaria lista
//empezar con las opciones
//boton login sin email, da error