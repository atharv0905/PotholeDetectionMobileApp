package com.atharv.potholedetection;

import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;


public class UserMapActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private LocationHelper locationHelper;

    ImageButton current_location_button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_map);

        locationHelper = new LocationHelper(this, this);

        // Check location permission when the activity starts
        if (locationHelper.checkLocationPermission()) {
            // Permission already granted, check if location is enabled
            if (locationHelper.isLocationEnabled()) {
                // Get user's current location and add marker
                // Move camera to the current location
                // Implement this part
            } else {
                // Location is not enabled, show dialog to enable it
                locationHelper.showEnableLocationDialog();
            }
        } else {
            // Request location permission if not granted
            locationHelper.requestLocationPermission();
        }

        // finding map by fragment id
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.gmap);
        mapFragment.getMapAsync(this);

        // finding current location button
        current_location_button = findViewById(R.id.currentLocationBtn);
        current_location_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle button click to get current location (if permissions are already granted)
                if (locationHelper.checkLocationPermission()) {
                    if (locationHelper.isLocationEnabled()) {
                        locationHelper.getCurrentLocation(mMap);
                    } else {
                        locationHelper.showEnableLocationDialog();
                    }
                } else {
                    locationHelper.requestLocationPermission();
                }
            }
        });
    }

    // All map related functionalities will be done here
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker and move the camera to a specific location
        LatLng specificLatLng = new LatLng(19.076090, 72.877426);
        mMap.addMarker(new MarkerOptions().position(specificLatLng).title("Marker Title"));
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(specificLatLng, 13.0f));

    }

    // Handle permission request result
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LocationHelper.LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted, do your operations
            } else {
                Toast.makeText(this, "Location permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }
}

