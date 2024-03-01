package com.atharv.potholedetection;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class WelcomeActivity extends AppCompatActivity {

    private SharedPreferences userSharedPreferences;
    SharedPreferences sharedPreferences;
    private String TOKEN = "Token";
    private String USER_AUTHENTICATION_PREF_NAME = "USER_AUTHENTICATION";
    private String USER = "USER";
    private String USER_PREF_NAME = "USERNAME";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        Button get_started = findViewById(R.id.get_started_button);
        sharedPreferences = getSharedPreferences(USER_AUTHENTICATION_PREF_NAME, MODE_PRIVATE);
        userSharedPreferences = getSharedPreferences(USER_PREF_NAME, MODE_PRIVATE);

        String token = sharedPreferences.getString("Token", "");
        if (token != "") {
//            Toast.makeText(getApplicationContext(), "Token retrieved successfully: " + token, Toast.LENGTH_LONG).show();
            JSONObject data = new JSONObject();
            try {
                data.put("token", token);
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }
            new ApiCaller().execute(data.toString());
        } else {
//            Toast.makeText(getApplicationContext(), "Token not found", Toast.LENGTH_LONG).show();
        }


        get_started.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Toast.makeText(getApplicationContext(), "Button Clicked", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(WelcomeActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });


    }
    private class ApiCaller extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {
            String apiUrl = "http://192.168.0.118:3000/protected";
            String postData = params[0];
            try {
                URL url = new URL(apiUrl);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("POST");
                connection.setRequestProperty("Content-Type", "application/json");
                connection.setDoOutput(true);

                OutputStream outputStream = connection.getOutputStream();
                outputStream.write(postData.getBytes());
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
        protected void onPostExecute(String result) {
//            Toast.makeText(getApplicationContext(), result, Toast.LENGTH_LONG).show();
            SharedPreferences.Editor userEditor = userSharedPreferences.edit();
            userEditor.putString(USER, result);
            userEditor.apply();
            if(result != ""){
                Intent intent = new Intent(WelcomeActivity.this, UserMapActivity.class);
                startActivity(intent);
            }else {
                Intent intent = new Intent(WelcomeActivity.this, SignUpActivity.class);
                startActivity(intent);
            }
        }
    }
}

