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

        EditText editTextUsername = findViewById(R.id.username_edittext);
        EditText editTextPassword = findViewById(R.id.password_login);
        ImageButton loginButton = findViewById(R.id.login1);
        TextView signUpButton = findViewById(R.id.signUpButton);
        TextView usernameError = findViewById(R.id.username_error1);
        TextView passwordError = findViewById(R.id.password_error_login);
        TextView sameCredentialsError = findViewById(R.id.same_credentials_error);

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = editTextUsername.getText().toString();
                String password = editTextPassword.getText().toString();

                if (email.isEmpty()) {
                    usernameError.setVisibility(View.VISIBLE);
                } else {
                    usernameError.setVisibility(View.GONE);
                }

                if (password.isEmpty()) {
                    passwordError.setVisibility(View.VISIBLE);
                } else {
                    passwordError.setVisibility(View.GONE);
                }

                // Check if username and password are the same
                if (!email.isEmpty() && !password.isEmpty()) {
                    if (email.equals(password)) {
                        sameCredentialsError.setVisibility(View.VISIBLE);
                        return;
                    } else {
                        sameCredentialsError.setVisibility(View.GONE);
                    }
                }

                // If both username and password are not empty, proceed with login
                if (!email.isEmpty() && !password.isEmpty()) {
                    // Perform login validation (Replace this with your own logic)
                    if (isValidLogin(email, password)) {
                        showToast("Login successful!");
                        Intent intent = new Intent(LoginActivity.this,UserMapActivity.class);
                        startActivity(intent);
                    } else {
                        showToast("Invalid credentials. Please try again.");
                    }
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
