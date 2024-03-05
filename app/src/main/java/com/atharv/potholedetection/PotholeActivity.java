package com.atharv.potholedetection;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.content.DialogInterface;
import androidx.appcompat.app.AlertDialog;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class PotholeActivity extends AppCompatActivity implements OnMapReadyCallback, GoogleMap.OnMarkerDragListener {
    private GoogleMap mMap;
    private FrameLayout gmapContainer;
    private LocationHelper locationHelper;
    private Marker currentLocationMarker;
    double currentLatitude = 0, currentLongitude = 0;

    LatLng currentLatLng;
    private SharedPreferences userSharedPreferences;
    private String USER = "USER";
    private String USER_PREF_NAME = "USERNAME";
    private String username = "";
    private final int CAMERA_REQUEST_CODE = 100;
    ImageView image;
    Bitmap imageBitmap;
    Button openCamera;
    Button sendBtn;
    String isPothole = "";
    Config config = new Config();
    private String IP = config.IP;
    private String PORT = config.PORT;

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

                currentLocationMarker.setDraggable(true);
                currentLocationMarker.setIcon(bitmapDescriptor(getApplicationContext(), R.drawable.pinpoint_marker));
                mMap.moveCamera(CameraUpdateFactory.newLatLng(currentLatLng));
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 20.0f));
            }
        };

        // Call the getCurrentLocation method of LocationHelper2 and pass the locationListener
        locationHelper.getCurrentLocation(locationListener);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pothole);

        userSharedPreferences = getSharedPreferences(USER_PREF_NAME, MODE_PRIVATE);
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

        image = (ImageView) findViewById(R.id.roadImage);
        openCamera = (Button) findViewById(R.id.openCamera);
        gmapContainer = findViewById(R.id.gmapContainer);
        sendBtn = (Button) findViewById(R.id.sendBtn);

        openCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent iCamera = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(iCamera, CAMERA_REQUEST_CODE);
            }
        });

        sendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                username = userSharedPreferences.getString(USER, "");
                new AddPotholeData().uploadImage(imageBitmap, ""+currentLatitude, ""+currentLongitude, username);
                gmapContainer.setVisibility(View.GONE);
                Uri uri = Uri.parse("android.resource://com.atharv.potholedetection/" + R.drawable.image_drop);
                image.setImageURI(uri);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {

            switch (requestCode) { // Getting data from camera intent
                case CAMERA_REQUEST_CODE:
                    imageBitmap = (Bitmap) (data.getExtras().get("data"));
                    image.setImageBitmap(imageBitmap); // setting image on image view
                    new PredictPothole().execute(imageBitmap);
                    break;
            }
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap){
        mMap = googleMap;
        mMap.setOnMarkerDragListener(this);
        getCurrentLocation();
        currentLatLng = new LatLng(currentLatitude, currentLongitude);
        mMap.moveCamera(CameraUpdateFactory.newLatLng(currentLatLng));
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 21.0f));

        // Disable panning (scrolling) gesture on the map
        googleMap.getUiSettings().setScrollGesturesEnabled(false);

        mMap.setMinZoomPreference(21.0f);
    }

    // for custom marker
    private BitmapDescriptor bitmapDescriptor(Context context, int vectorResId) {
        Drawable vectorDrawable = ContextCompat.getDrawable(context, vectorResId);
        assert vectorDrawable != null;
        vectorDrawable.setBounds(0, 0, vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight());
        Bitmap bitmap = Bitmap.createBitmap(vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        vectorDrawable.draw(canvas);
        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }

    @Override
    public void onMarkerDrag(Marker marker) {

    }

    @Override
    public void onMarkerDragEnd(Marker marker) {
        currentLatLng = marker.getPosition();
        currentLatitude = currentLatLng.latitude;
        currentLongitude = currentLatLng.longitude;

        LatLng originalPosition = new LatLng(currentLatitude, currentLongitude);
        LatLng newPosition = marker.getPosition();

        // Calculate the distance between original and new position
        float[] results = new float[1];
        Location.distanceBetween(originalPosition.latitude, originalPosition.longitude,
                newPosition.latitude, newPosition.longitude, results);
        float distanceInMeters = results[0];
        // If distance exceeds 1 meter, revert the marker back to original position
        if (distanceInMeters > 3) {
            marker.setPosition(originalPosition);
            Toast.makeText(this, "Limit is 3 meter", Toast.LENGTH_SHORT).show();
        } else {
            currentLatLng = marker.getPosition();
            currentLatitude = currentLatLng.latitude;
            currentLongitude = currentLatLng.longitude;
        }

    }

    @Override
    public void onMarkerDragStart(Marker marker) {

    }

    public class PredictPothole extends AsyncTask<Bitmap, Void, String> {

        @Override
        protected String doInBackground(Bitmap... bitmaps) {
            Bitmap bitmap = (Bitmap) bitmaps[0];
            String apiUrl = "http://20.235.245.88:5000/predict";
            try {
                URL url = new URL(apiUrl);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("POST");
                connection.setRequestProperty("Content-Type", "application/octet-stream");
                connection.setDoOutput(true);

                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
                byte[] byteArray = stream.toByteArray();

                OutputStream outputStream = connection.getOutputStream();
                outputStream.write(byteArray);
                outputStream.flush();
                outputStream.close();

                int responseCode = connection.getResponseCode();
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                    String inputLine;
                    StringBuilder response = new StringBuilder();

                    while ((inputLine = in.readLine()) != null) {
                        response.append(inputLine);
                    }
                    in.close();

                    return response.toString();
                } else {
                    return "Error: " + responseCode;
                }
            } catch (Exception e) {
                e.printStackTrace();
                return "Error: " + e.getMessage();
            }
        }

        @Override
        protected void onPostExecute(String res) {
            isPothole = res;
            if(isPothole.equals("true")){
                gmapContainer.setVisibility(View.VISIBLE);
            }else {
                Toast.makeText(getApplicationContext(), "It's not a pothole", Toast.LENGTH_LONG).show();
                showAlert();
            }

        }

    }


    private void showAlert(){

    }
}
