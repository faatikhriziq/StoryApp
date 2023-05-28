package com.faatikhriziq.storyapp.ui.map

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import com.faatikhriziq.storyapp.R
import com.faatikhriziq.storyapp.databinding.ActivityMapBinding
import com.faatikhriziq.storyapp.helper.ViewModelFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.faatikhriziq.storyapp.data.repository.Result
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions

class MapActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMapBinding
    private lateinit var viewModel: MapViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMapBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        setupViewModel()
    }

    private fun setupAction() {
        viewModel.getLogin().observe(this) { user ->
            executeGetAllStoriesWithLocation(user.token)
        }
    }

    private fun setupViewModel() {
        viewModel = ViewModelProvider(
            this,
            ViewModelFactory.getInstance(this)
        )[MapViewModel::class.java]
    }

    private fun executeGetAllStoriesWithLocation(token: String) {
        viewModel.getAllStoriesWithLocation(token).observe(this) { result ->
            if (result != null) {
                when (result) {
                    is Result.Loading -> {}
                    is Result.Success -> {
                        result.data.forEach { story ->
                            val location = LatLng(story.lat!!, story.lon!!)
                            mMap.addMarker(MarkerOptions().position(location).title(story.name))
                            mMap.moveCamera(CameraUpdateFactory.newLatLng(location))
                        }
                    }
                    is Result.Error -> {}
                }
            }
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        setupAction()
    }








}