package com.example.quevemoshoy.model

import com.google.gson.annotations.SerializedName
import java.io.Serializable

/**
 * `Movie` representa una película con sus detalles.
 *
 * @property id El ID de la película.
 * @property title El título de la película.
 * @property overview Una descripción general de la película.
 * @property voteAverage La puntuación media de la película.
 * @property releaseDate La fecha de lanzamiento de la película.
 * @property posterPath La ruta del póster de la película.
 * @property genres Los géneros de la película.
 * @property tagline El eslogan de la película.
 * @property runtime La duración de la película.
 * @property providers Los proveedores de la película.
 */
data class Movie(
    @SerializedName("id") val id: Int,
    @SerializedName("title") val title: String,
    @SerializedName("overview") val overview: String,
    @SerializedName("vote_average") val voteAverage: Double,
    @SerializedName("release_date") val releaseDate: String,
    @SerializedName("poster_path") val posterPath: String,
    @SerializedName("genres") val genres: List<Genre>,
    @SerializedName("tagline") val tagline: String,
    @SerializedName("runtime") val runtime: Int,
    @SerializedName("providers") val providers: List<Providers>
) : Serializable

/**
 * `SimpleMovie` representa una versión simplificada de una película.
 *
 * @property id El ID de la película.
 * @property title El título de la película.
 */
data class SimpleMovie(
    val id: Int, val title: String
)

/**
 * `MovieResponse` representa una respuesta de la API que contiene una lista de películas.
 *
 * @property movies La lista de películas devueltas por la API.
 */
data class MovieResponse(
    @SerializedName("results") val movies: List<Movie>
) : Serializable

/**
 * `Genre` representa un género de película.
 *
 * @property name El nombre del género.
 */
data class Genre(
    @SerializedName("name") val name: String
) : Serializable

/**
 * `Providers` representa un proveedor de películas.
 *
 * @property logoPath La ruta del logotipo del proveedor.
 * @property providerId El ID del proveedor.
 * @property providerName El nombre del proveedor.
 */
data class Providers(
    @SerializedName("logo_path") val logoPath: String,
    @SerializedName("provider_id") val providerId: Int,
    @SerializedName("provider_name") val providerName: String
) : Serializable

/**
 * `ProvidersResponse` representa una respuesta de la API que contiene una lista de proveedores.
 *
 * @property results Los resultados de la respuesta de la API.
 */
data class ProvidersResponse(
    @SerializedName("results") val results: Results
) : Serializable

/**
 * `Results` representa los resultados de una respuesta de la API.
 *
 * @property es Los proveedores de películas para España.
 */
data class Results(
    @SerializedName("ES") val es: CountryProviders
) : Serializable

/**
 * `CountryProviders` representa los proveedores de películas para un país específico.
 *
 * @property providers La lista de proveedores para el país.
 */
data class CountryProviders(
    @SerializedName("flatrate") val providers: List<Providers>
) : Serializable



