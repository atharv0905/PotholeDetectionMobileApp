package com.atharv.potholedetection;

import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;

import java.util.Arrays;


public class UserMapActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private LocationHelper locationHelper;

    private ImageButton current_location_button;
    double currentLatitude = 0, currentLongitude = 0;
    private Marker currentLocationMarker;
    private Marker selectedMarker;


    // get current location of user
    private void getCurrentLocation(){
        // Implement the OnLocationListener interface
        LocationHelper.OnLocationListener locationListener = new LocationHelper.OnLocationListener() {
            @Override
            public void onLocationReceived(double latitude, double longitude) {
                // Handle the received latitude and longitude
                currentLatitude = latitude;
                currentLongitude = longitude;
                LatLng currentLatLng = new LatLng(currentLatitude, currentLongitude);
                if(currentLocationMarker == null){
                    MarkerOptions newCurrentLocationMarker = new MarkerOptions();
                    newCurrentLocationMarker.position(currentLatLng);

                    currentLocationMarker = mMap.addMarker(newCurrentLocationMarker);
                }else{
                    currentLocationMarker.setPosition(currentLatLng);
                }

                mMap.moveCamera(CameraUpdateFactory.newLatLng(currentLatLng));
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 20.0f));
            }
        };

        // Call the getCurrentLocation method of LocationHelper2 and pass the locationListener
        locationHelper.getCurrentLocation(locationListener);
    }

    // implements place search bar
    private void searchLocation(){
        // Initialize Places.
        Places.initialize(getApplicationContext(), "AIzaSyADAtPLIQGT-jFe81VVgJIyb0UBi4nR7so");

        // Initialize the AutocompleteSupportFragment.
        AutocompleteSupportFragment autocompleteFragment = (AutocompleteSupportFragment)
                getSupportFragmentManager().findFragmentById(R.id.placeSearchBar);

        // Specify the types of place data to return.
        autocompleteFragment.setPlaceFields(Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG));

        // Set up a PlaceSelectionListener to handle the response.
        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                // Handle the selected place.
//            Toast.makeText(MapsActivity.this, "Place: " + place.getName(), Toast.LENGTH_LONG).show();

                LatLng selectedLatLng = place.getLatLng();
                if(selectedLatLng == null){
                    Toast.makeText(getApplicationContext(), "Lat Lng is null", Toast.LENGTH_LONG).show();
                }
                if (selectedLatLng != null) {
                    if (selectedMarker == null) {
                        // If no marker exists, create a new one
                        MarkerOptions newMarker = new MarkerOptions();
                        newMarker.position(selectedLatLng);
                        selectedMarker = mMap.addMarker(newMarker);
                    } else {
                        // If marker exists, move it to the new location
                        selectedMarker.setPosition(selectedLatLng);
                    }
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(selectedLatLng, 15));
                }
            }

            @Override
            public void onError(com.google.android.gms.common.api.Status status) {
                // Handle the error.
                Toast.makeText(getApplicationContext(), "" + status, Toast.LENGTH_LONG).show();
            }
        });
    }
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

        // enabling search bar
        searchLocation();

        // finding current location button
        current_location_button = findViewById(R.id.currentLocationBtn);
        current_location_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle button click to get current location (if permissions are already granted)
                if (locationHelper.checkLocationPermission()) {
                    if (locationHelper.isLocationEnabled()) {
                        getCurrentLocation();
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
        mMap.moveCamera(CameraUpdateFactory.newLatLng(specificLatLng));
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

