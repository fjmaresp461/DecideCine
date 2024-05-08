package com.example.quevemoshoy.provider

import com.example.quevemoshoy.model.Movie
import com.example.quevemoshoy.model.MovieResponse
import com.example.quevemoshoy.model.ProvidersResponse
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

/**
 * `MovieInterface` es una interfaz que define las llamadas a la API de películas.
 *
 * Esta interfaz utiliza Retrofit para definir las llamadas HTTP a la API de películas.
 */
interface MovieInterface {
    /**
     * Obtiene las películas por géneros.
     */
    @GET("/3/discover/movie")
    suspend fun getMoviesByGenres(
        @Query("api_key") apiKey: String = ApiClient.API_KEY,
        @Query("with_genres") genres: String,
        @Query("language") language: String = "es-ES",
        @Query("sort_by") sortBy: String = "vote_average.desc",
        @Query("vote_count.gte") voteCount: Int = 100,
        @Query("page") page: Int = 3

    ): MovieResponse

    /**
     * Obtiene los detalles de una película por su ID.
     */
    @GET("/3/movie/{movie_id}")
    suspend fun getMovieDetails(
        @Path("movie_id") movieId: Int,
        @Query("api_key") apiKey: String = ApiClient.API_KEY,
        @Query("language") language: String = "es-ES"
    ): Movie

    /**
     * Obtiene los proveedores de una película por su ID.
     */
    @GET("/3/movie/{movie_id}/watch/providers")
    suspend fun getMovieProviders(
        @Path("movie_id") movieId: Int,
        @Query("api_key") apiKey: String = ApiClient.API_KEY,
        @Query("region") region: String = "ES"
    ): ProvidersResponse

    /**
     * Obtiene las últimas películas.
     */
    @GET("/3/discover/movie")
    suspend fun getLatestMovies(
        @Query("api_key") apiKey: String = ApiClient.API_KEY,
        @Query("language") language: String = "es-ES",
        @Query("region") region: String = "ES",


        ): MovieResponse


}
