package com.example.quevemoshoy

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.quevemoshoy.databinding.ActivityMapBinding
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions

class MapActivity : AppCompatActivity(), OnMapReadyCallback,
    GoogleMap.OnMyLocationButtonClickListener, GoogleMap.OnMyLocationClickListener {
    private lateinit var binding: ActivityMapBinding
    private lateinit var mapa: GoogleMap
    val LOCATION_PERMISSION = 1000

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMapBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val desiredLocation = LatLng(36.8503, -2.4650)
        cargarMap(savedInstanceState, desiredLocation)
    }


    private fun cargarMap(savedInstanceState: Bundle?, desiredLocation: LatLng) {
        binding.mapView.onCreate(savedInstanceState)
        binding.mapView.getMapAsync { googleMap ->
            mapa = googleMap
            mapa.uiSettings.isZoomControlsEnabled = true
            val cameraUpdate = CameraUpdateFactory.newLatLngZoom(desiredLocation, 14f)
            mapa.animateCamera(cameraUpdate, 4000, null)
            setLocalization()
            ponerMarcador()
            mapa.setOnMyLocationClickListener(this)
            mapa.setOnMyLocationButtonClickListener(this)
        }
    }



    override fun onMapReady(p0: GoogleMap) {
        mapa = p0
        mapa.uiSettings.isZoomControlsEnabled = true
        val desiredLocation = LatLng(36.8503, -2.4650)
        val cameraUpdate = CameraUpdateFactory.newLatLngZoom(desiredLocation, 19f)
        mapa.animateCamera(cameraUpdate, 4000, null)
        setLocalization()
        ponerMarcador()
        mapa.setOnMyLocationClickListener(this)
        mapa.setOnMyLocationButtonClickListener(this)
    }



    private fun ponerMarcador() {
        val cinemaLocations = listOf(
            Cinema("Terrazas Aguadulce", 36.780323, -2.608337, "De lunes a domingo: de 10:00 a 22:00"),
            Cinema("Cine Sala Federico García Lorca", 36.834994, -2.460229, "De lunes a domingo: de 10:00 a 22:00"),
            Cinema("Cines Monumental", 36.841888, -2.426322, "De lunes a domingo: de 10:00 a 22:00"),
            Cinema("Museo de Almería (Filmoteca)", 36.845748, -2.456583, "De lunes a domingo: de 10:00 a 22:00"),
            Cinema("Teatro Cervantes", 36.834642, -2.459398, "De lunes a domingo: de 10:00 a 22:00"),
            Cinema("Yelmo Cines Torrecárdenas", 36.845363, -2.403944, "De lunes a domingo: de 10:00 a 22:00"),
            Cinema("Ocine Copo", 36.774402, -2.652742, "De lunes a domingo: de 10:00 a 22:00"),
            Cinema("Yelmo Cines Roquetas De Mar", 36.734142, -2.604148, "De lunes a domingo: de 10:00 a 22:00"),
            Cinema("Cine Teatro Óvalo", 37.248611, -2.151944, "De lunes a domingo: de 10:00 a 22:00")
        )


        for (cinema in cinemaLocations) {
            val marker = mapa.addMarker(
                MarkerOptions()
                    .position(LatLng(cinema.latitude, cinema.longitude)) // Use latitude and longitude
                    .title(cinema.name)
            )
            if (marker != null) {
                marker.setTag(cinema)
            }
            if (marker != null) {
                marker.setInfoWindowAnchor(0.5f, 0.5f)
            }

        }
    }



    private fun setLocalization() {
        if (!::mapa.isInitialized) return
        if (ContextCompat.checkSelfPermission(
                this, Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(
                this, Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            mapa.isMyLocationEnabled = true
        } else {
            askPermissions()
        }
    }

    private fun askPermissions() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(
                this, Manifest.permission.ACCESS_COARSE_LOCATION
            ) || ActivityCompat.shouldShowRequestPermissionRationale(
                this, Manifest.permission.ACCESS_FINE_LOCATION
            )
        ) {
            showExplication()
        } else {
            // Se corrige el error al no comprobar el resultado de la solicitud de permisos
            ActivityCompat.requestPermissions(
                this, arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION
                ), LOCATION_PERMISSION
            )
        }
    }


    private fun takePermissions() {
        ActivityCompat.requestPermissions(
            this, arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION
            ), LOCATION_PERMISSION
        )
    }

    @SuppressLint("MissingPermission")
    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<out String>, grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == LOCATION_PERMISSION) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                mapa.isMyLocationEnabled = true
            }
        } else {

        }
    }

    private fun showExplication() {

    }


    override fun onMyLocationButtonClick(): Boolean {

        return false
    }

    override fun onMyLocationClick(p0: Location) {

    }

    override fun onStart() {
        super.onStart()
        binding.mapView.onStart()
    }

    override fun onResume() {
        super.onResume()
        binding.mapView.onResume()
    }

    override fun onPause() {
        binding.mapView.onPause()
        super.onPause()
    }

    override fun onStop() {
        binding.mapView.onStop()
        super.onStop()
    }

    override fun onDestroy() {
        binding.mapView.onDestroy()
        super.onDestroy()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        binding.mapView.onLowMemory()
    }
}
data class Cinema(
    val name: String,
    val latitude: Double,
    val longitude: Double,
    val schedule: String)
