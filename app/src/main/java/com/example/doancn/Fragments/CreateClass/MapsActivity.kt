package com.example.doancn.Fragments.CreateClass

import android.annotation.SuppressLint
import android.content.Intent
import android.location.Location
import android.os.Bundle
import android.os.Handler
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.example.doancn.R
import com.example.doancn.databinding.ActivityMapsBinding
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.net.PlacesClient

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMapsBinding
    private val TAG = "MapsActivity"
    private var mPlacesClient: PlacesClient? = null
    private var mFusedLocationProviderClient: FusedLocationProviderClient? = null
    private var mLastKnownLocation: Location? = null
    private val DEFAULT_ZOOM = 18F
    private lateinit var apiKey: String

    val vietnam = LatLng(13.079471745664604, 109.30418718606234)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
        apiKey = getString(R.string.google_maps_key)
        Places.initialize(this, apiKey)
        mPlacesClient = Places.createClient(this)
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)

        setupMyLocation()
    }

    private fun setupMyLocation() {

        binding.floatingActionButton.setOnClickListener {
            getDeviceLocation()
        }
    }

    @SuppressLint("MissingPermission")
    private fun getDeviceLocation() {
        mFusedLocationProviderClient?.lastLocation
            ?.addOnCompleteListener(this) {
                if (it.isSuccessful) {
                    //mMap.animateCamera(CameraUpdateFactory.zoomOut())
                    val handler = Handler()
                    handler.postDelayed(Runnable {
                        mMap.animateCamera(
                            CameraUpdateFactory.newLatLngZoom(
                                LatLng(
                                    it.result.latitude,
                                    it.result.longitude
                                ), DEFAULT_ZOOM
                            )
                        )
                    }, 500)

                } else {
                    Log.d(TAG, "Current location is null")
                    mMap.moveCamera(
                        CameraUpdateFactory
                            .newLatLngZoom(vietnam, DEFAULT_ZOOM)
                    );
                    Log.e(TAG, "Exception: %s", it.exception)
                }
            }
    }


    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        getDeviceLocation()
        googleMap.setOnCameraIdleListener {
            val center: LatLng = googleMap.cameraPosition.target
            Log.d("Camera move", center.toString())
            binding.confirmBtn.setOnClickListener {
                val intent = Intent()
                intent.putExtra("latitude", center.latitude)
                intent.putExtra("longitude", center.longitude)
                setResult(RESULT_OK, intent)
                finish()
            }
        }
    }
}