package com.atharv.potholedetection;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        EditText editTextUsername = (EditText) findViewById(R.id.username_edittext);
        EditText editTextPassword = (EditText) findViewById(R.id.password1);
        ImageButton loginButton = (ImageButton) findViewById(R.id.login1);
        TextView signUpButton = (TextView) findViewById(R.id.signUpButton);

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = editTextUsername.getText().toString();
                String password = editTextPassword.getText().toString();

                // Perform login validation (Replace this with your own logic)
                if (isValidLogin(email, password)) {
                    showToast("Login successful!");
                    Intent intent = new Intent(LoginActivity.this, UserMapActivity.class);
                    startActivity(intent);
                } else {
                    showToast("Invalid credentials. Please try again.");
                }
            }
        });

        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navigate to the sign-up activity
                Intent intent = new Intent(LoginActivity.this, SignUpActivity.class);
                startActivity(intent);
            }
        });
    }

    private boolean isValidLogin(String email, String password) {
        // Add your login validation logic here
        // For simplicity, we consider it valid if both email and password are not empty
        return !email.isEmpty() && !password.isEmpty();
    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }


}