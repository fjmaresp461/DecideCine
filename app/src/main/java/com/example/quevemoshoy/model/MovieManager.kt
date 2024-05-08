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

/**
 * `MoviesManager` es una clase que gestiona las operaciones relacionadas con las películas.
 *
 * Esta clase se encarga de interactuar con la API de películas y la base de datos de Firebase para obtener y gestionar las películas y las preferencias del usuario.
 *
 * @property database La base de datos de Firebase.
 * @property reference La referencia a la base de datos de Firebase.
 * @property apiService El servicio de la API de películas.
 * @property auth La autenticación de Firebase.
 * @property currentUser El usuario actual autenticado con Firebase.
 * @property allGenres La lista de todos los géneros de películas.
 *
 * @constructor Crea una instancia de `MoviesManager`.
 */
class MoviesManager {

    private val database = FirebaseDatabase.getInstance()
    private val reference = database.getReference("users")
    private val apiService = ApiClient.retrofit.create(MovieInterface::class.java)
    val auth = FirebaseAuth.getInstance()
    val currentUser = auth.currentUser
    val allGenres = listOf(
        "28",
        "12",
        "16",
        "35",
        "80",
        "99",
        "18",
        "10751",
        "14",
        "36",
        "27",
        "10402",
        "9648",
        "10749",
        "878",
        "10770",
        "53",
        "10752",
        "37"
    )


    companion object {
        var moviesCache: List<Movie>? = null

        var genrePreferencesCache: List<String>? = null
        var latestMoviesCache: List<Movie>? = null
    }

    /**
     * Obtiene todas las preferencias de género del usuario.
     *
     * @param currentUser El usuario actual.
     * @return Una lista de las preferencias de género del usuario.
     */
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

            val sortedGenrePreferences =
                allGenrePreferences.toList().sortedByDescending { it.second }.map { it.first }
            Log.d("Firebaserl", "Lista de preferencias de género ordenada: $sortedGenrePreferences")

            if (genrePreferencesCache != sortedGenrePreferences) {
                genrePreferencesCache = sortedGenrePreferences
            }

            genrePreferencesCache!!
        }

    /**
     * Obtiene los proveedores preferidos del usuario.
     *
     * @param currentUser El usuario actual.
     * @return Una lista de los IDs de los proveedores preferidos del usuario.
     */
    suspend fun getPreferredProviders(currentUser: FirebaseUser?): List<Int> =
        withContext(Dispatchers.IO) {
            val preferredProviderIds = mutableListOf<Int>()

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

    /**
     * Obtiene las películas por género y proveedor.
     *
     * @return Una lista de películas.
     */
    suspend fun fetchMoviesByGenreAndProvider(): List<Movie> {
        val currentUserGenrePreferences = getAllGenrePreferences(currentUser).toMutableList()

        if (genrePreferencesCache != currentUserGenrePreferences) {
            moviesCache = null
        }

        if (moviesCache != null && moviesCache!!.isNotEmpty()) {
            return moviesCache!!
        }

        val preferredProviderIds = getPreferredProviders(currentUser)

        if (currentUserGenrePreferences.isEmpty()) {
            currentUserGenrePreferences.addAll(allGenres.shuffled().take(3))
        }

        val allMovies = mutableListOf<Movie>()
        val genreMovies = mutableMapOf<String, MutableList<Movie>>()

        for (genreId in currentUserGenrePreferences) {
            val response = apiService.getMoviesByGenres(genres = genreId)
            val movies = response.movies

            for (movie in movies) {
                val providers = fetchMovieProviders(movie.id)
                if (providers?.any { it.providerId in preferredProviderIds } == true) {
                    genreMovies.getOrPut(genreId) { mutableListOf() }.add(movie)
                }
            }

            if (allMovies.size < 12) {
                for (movie in movies) {
                    if (movie !in allMovies) {
                        allMovies.add(movie)
                    }
                    if (allMovies.size >= 12) {
                        break
                    }
                }
            }
        }

        allMovies.shuffle()


        moviesCache = allMovies

        return allMovies
    }

    /**
     * Obtiene las películas basándose en el tipo de recomendación.
     *
     * @param recommendationType El tipo de recomendación ("latest" para las últimas películas, "myList" para las películas favoritas del usuario).
     * @return Una lista de películas basada en el tipo de recomendación.
     */


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
                        if (allMovies.size == 20) {
                            break
                        }
                    }
                }

                if (allMovies.size < 20) {
                    val additionalMovies =
                        response.movies.filter { it !in allMovies }.take(20 - allMovies.size)
                    allMovies.addAll(additionalMovies)
                }


                allMovies.shuffle()
                latestMoviesCache = allMovies.take(12)
            } else if (recommendationType == "myList") {
                favMovies.addAll(MainActivity2.favoriteMoviesList.filterNotNull())
                return favMovies
            }

            return latestMoviesCache ?: emptyList()
        } catch (e: Exception) {
            Log.e("MoviesManager", R.string.error_fetching.toString(), e)
            return emptyList()
        }
    }


    /**
     * Obtiene los proveedores de una película.
     *
     * @param movieId El ID de la película.
     * @return Una lista de proveedores de la película.
     */
    suspend fun fetchMovieProviders(movieId: Int): List<Providers>? {
        return try {
            val response = apiService.getMovieProviders(movieId)
            response.results.es.providers
        } catch (e: Exception) {
            Log.e("MoviesManager", R.string.error_providers.toString(), e)
            null
        }
    }


    /**
     * Obtiene una película por su ID.
     *
     * @param movieId El ID de la película.
     * @return La película con el ID proporcionado.
     */
    suspend fun fetchMovieById(movieId: Int): Movie? {
        return try {
            val response = apiService.getMovieDetails(movieId, apiKey = ApiClient.API_KEY)
            response
        } catch (e: Exception) {
            Log.e("MoviesManager", R.string.error_fetching.toString(), e)
            null
        }
    }

    /**
     * Inicia una actividad y obtiene las películas por un género.
     *
     * @param context El contexto de la aplicación.
     * @param genreId El ID del género.
     */
    fun fetchAndStartActivity(context: Context, genreId: String) {
        CoroutineScope(Dispatchers.IO).launch {
            val movies = fetchMoviesByOneGenre(genreId)
            val isFromAllGenres = true
            withContext(Dispatchers.Main) {
                val intent = Intent(context, RecyclerActivity::class.java).apply {
                    putExtra("movies", ArrayList(movies))
                    putExtra("isFromAllGenres", isFromAllGenres)
                }
                context.startActivity(intent)
            }
        }
    }

    /**
     * Obtiene las películas por un género.
     *
     * @param genreId El ID del género.
     * @return Una lista de películas del género proporcionado.
     */
    suspend fun fetchMoviesByOneGenre(genreId: String): List<Movie> {
        val response = apiService.getMoviesByGenres(genres = genreId)
        return response.movies.take(20)
    }


}

/**
 * `UserPreferences` representa las preferencias de género de un usuario.
 *
 * @property genres Un mapa de los géneros y sus preferencias.
 */
data class UserPreferences(
    var genres: Map<String, Int> = mutableMapOf()
)
