package com.atharv.potholedetection;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class WelcomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        Button get_started = findViewById(R.id.get_started_button);
        SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        String token = sharedPreferences.getString("Token", "");
        if (token != "") {
            // Token is present, proceed with your app logic
            Toast.makeText(getApplicationContext(), "Token retrieved successfully: " + token, Toast.LENGTH_LONG).show();
            Intent intent = new Intent(WelcomeActivity.this, UserMapActivity.class);
            startActivity(intent);
        } else {
            // Token is not present, handle accordingly
            Toast.makeText(getApplicationContext(), "Token not found", Toast.LENGTH_LONG).show();
            Intent intent = new Intent(WelcomeActivity.this, SignUpActivity.class);
            startActivity(intent);
        }
        // Set OnClickListener for the "get_started" button
        get_started.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), "Button Clicked", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(WelcomeActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });


    }
}

