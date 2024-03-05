package com.atharv.potholedetection;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;

import com.codebyashish.googledirectionapi.AbstractRouting;
import com.codebyashish.googledirectionapi.ErrorHandling;
import com.codebyashish.googledirectionapi.RouteDrawing;
import com.codebyashish.googledirectionapi.RouteInfoModel;
import com.codebyashish.googledirectionapi.RouteListener;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.location.LocationServices;

import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.maps.model.RoundCap;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;


public class UserMapActivity extends AppCompatActivity implements OnMapReadyCallback, RouteListener {

    private SharedPreferences userSharedPreferences;
    SharedPreferences sharedPreferences;
    private String TOKEN = "Token";
    private String USER_AUTHENTICATION_PREF_NAME = "USER_AUTHENTICATION";
    private String USER = "USER";
    private String USER_PREF_NAME = "USERNAME";
    private GoogleMap mMap;
    private LocationHelper locationHelper;

    private AbstractRouting.TravelMode travelMode = AbstractRouting.TravelMode.DRIVING;

    private Button driving, biking, walking;
    private ImageButton current_location_button;
    private Button logout;
    private Button addPotholeBtn;
    private Button startNavigationBtn;
    double currentLatitude = 0, currentLongitude = 0;
    LatLng destinationLocation;
    LatLng currentLatLng;
    private Marker currentLocationMarker;
    private Marker destinationLocationMarker;
    PolylineOptions polylineOptions;
    ArrayList<Polyline> polylines;


    // get current location of user
    private void getCurrentLocation(){
        // Implement the OnLocationListener interface
        LocationHelper.OnLocationListener locationListener = new LocationHelper.OnLocationListener() {
            @Override
            public void onLocationReceived(double latitude, double longitude) {
                // Handle the received latitude and longitude
                currentLatitude = latitude;
                currentLongitude = longitude;
                currentLatLng = new LatLng(currentLatitude, currentLongitude);
                if(currentLocationMarker == null){
                    MarkerOptions newCurrentLocationMarker = new MarkerOptions();
                    newCurrentLocationMarker.position(currentLatLng);

                    currentLocationMarker = mMap.addMarker(newCurrentLocationMarker);
                }else{
                    currentLocationMarker.setPosition(currentLatLng);
                }

//                mMap.moveCamera(CameraUpdateFactory.newLatLng(currentLatLng));
//                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 20.0f));
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

                LatLng selectedLatLng = place.getLatLng();
                if (selectedLatLng != null) {
                    if (destinationLocationMarker == null) {
                        // If no marker exists, create a new one
                        MarkerOptions newMarker = new MarkerOptions();
                        newMarker.position(selectedLatLng);
                        destinationLocationMarker = mMap.addMarker(newMarker);
                    } else {
                        // If marker exists, move it to the new location
                        destinationLocationMarker.setPosition(selectedLatLng);
                    }
                    destinationLocation = selectedLatLng;
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(selectedLatLng, 14));
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(selectedLatLng, 14));
                }
            }

            @Override
            public void onError(com.google.android.gms.common.api.Status status) {
                // Handle the error.
                Toast.makeText(getApplicationContext(), "" + status, Toast.LENGTH_LONG).show();
            }

        });
    }

    // generate route between current location and destination
    private void getRoute(LatLng userLocation, LatLng destinationLocation) {

        RouteDrawing routeDrawing = new RouteDrawing.Builder()
                .context(UserMapActivity.this)  // pass your activity or fragment's context
                .travelMode(travelMode)
                .withListener(this).alternativeRoutes(true)
                .waypoints(userLocation, destinationLocation)
                .build();
        routeDrawing.execute();
        Toast.makeText(UserMapActivity.this, travelMode.toString(),Toast.LENGTH_SHORT).show();
    }

    private void navigate(){
        if (locationHelper.checkLocationPermission()) {
            if (locationHelper.isLocationEnabled()) {
                locationHelper.startLocationUpdates(new LocationHelper.OnLocationListener() {
                    @Override
                    public void onLocationReceived(double latitude, double longitude) {
                        // Handle continuous location updates here
                        currentLatitude = latitude;
                        currentLongitude = longitude;
                        LatLng currentLatLng = new LatLng(currentLatitude, currentLongitude);
                        if (currentLocationMarker == null) {
                            MarkerOptions newCurrentLocationMarker = new MarkerOptions();
                            newCurrentLocationMarker.position(currentLatLng);
                            currentLocationMarker = mMap.addMarker(newCurrentLocationMarker);
                        } else {
                            currentLocationMarker.setPosition(currentLatLng);
                        }
                        mMap.moveCamera(CameraUpdateFactory.newLatLng(currentLatLng));
                        getRoute(currentLatLng, destinationLocation);
                        Toast.makeText(getApplicationContext(), "Location Updated", Toast.LENGTH_SHORT).show();
                    }
                });
            } else {
                locationHelper.showEnableLocationDialog();
            }
        } else {
            locationHelper.requestLocationPermission();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_map);

        locationHelper = new LocationHelper(this, this);
        getCurrentLocation();

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
                        mMap.moveCamera(CameraUpdateFactory.newLatLng(currentLatLng));
                        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 20.0f));
                    } else {
                        locationHelper.showEnableLocationDialog();
                    }
                } else {
                    locationHelper.requestLocationPermission();
                }
            }
        });

        logout = (Button) findViewById(R.id.logoutBtn);
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sharedPreferences = getSharedPreferences(USER_AUTHENTICATION_PREF_NAME, MODE_PRIVATE);
                userSharedPreferences = getSharedPreferences(USER_PREF_NAME, MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.clear();
                editor.apply();
                SharedPreferences.Editor user = userSharedPreferences.edit();
                user.clear();
                user.apply();
                Intent intent = new Intent(UserMapActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });

        addPotholeBtn = (Button) findViewById(R.id.addPothole);
        addPotholeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(UserMapActivity.this, PotholeActivity.class);
                startActivity(intent);
            }
        });

        driving = (Button) findViewById(R.id.driving);
        biking = (Button) findViewById(R.id.biking);
        walking = (Button) findViewById(R.id.walking);

        driving.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                travelMode = AbstractRouting.TravelMode.DRIVING;
            }
        });
        biking.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                travelMode = AbstractRouting.TravelMode.BIKING;
            }
        });
        walking.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                travelMode = AbstractRouting.TravelMode.WALKING;
            }
        });

        startNavigationBtn = (Button) findViewById(R.id.startNavigationBtn);
        startNavigationBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(currentLatLng != null && destinationLocation != null){
                    navigate();
                }
            }
        });
    }

    // All map related functionalities will be done here
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
//        googleMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(this, R.raw.night_mode));
        // Add a marker and move the camera to a specific location
        LatLng specificLatLng = new LatLng(19.076090, 72.877426);
        mMap.moveCamera(CameraUpdateFactory.newLatLng(specificLatLng));
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(specificLatLng, 13.0f));

        if (mMap != null) {
            // Set up a OnMapClickListener to handle clicks on the map
            mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
                @Override
                public void onMapClick(LatLng latLng) {
                    // When the map is clicked, add a marker at the clicked position
                    if(destinationLocationMarker == null){
                        MarkerOptions newDestinationMarker = new MarkerOptions();
                        newDestinationMarker.position(latLng);

                        destinationLocationMarker = mMap.addMarker(newDestinationMarker);
                    }else {
                        destinationLocationMarker.setPosition(latLng);
                    }
                    destinationLocation = latLng;
                    mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 14));
                }
            });
        } else {
            Log.e("MapActivity", "GoogleMap object is null");
        }
    }


    @Override
    public void onRouteFailure(ErrorHandling e) {
        Toast.makeText(this, "Route Failed", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onRouteStart() {
        Toast.makeText(this, "Route Started", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onRouteSuccess(ArrayList<RouteInfoModel> list, int indexing) {
        Toast.makeText(this, "Route Succeed", Toast.LENGTH_SHORT).show();
        polylineOptions = new PolylineOptions();
        polylines = new ArrayList<>();

        for (int i = 0; i < list.size(); i++) {
            if (i == indexing) {
                Log.e("TAG", "onRoutingSuccess: routeIndexing" + indexing);
                polylineOptions.color(Color.BLACK);
                polylineOptions.width(12);
                polylineOptions.addAll(list.get(indexing).getPoints());
                polylineOptions.startCap(new RoundCap());
                polylineOptions.endCap(new RoundCap());
                Polyline polyline = mMap.addPolyline(polylineOptions);
                polylines.add(polyline);
            }
        }
    }

    @Override
    public void onRouteCancelled() {
        Toast.makeText(this, "Route Cancelled", Toast.LENGTH_SHORT).show();
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

