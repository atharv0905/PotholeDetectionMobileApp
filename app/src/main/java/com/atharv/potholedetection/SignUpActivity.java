package com.atharv.potholedetection;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class SignUpActivity extends AppCompatActivity {

    private EditText usernameEditText;
    private EditText emailEditText;
    private EditText passwordEditText;
    private EditText confirmPasswordEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        usernameEditText = findViewById(R.id.username);
        emailEditText = findViewById(R.id.email);
        passwordEditText = findViewById(R.id.password2);
        confirmPasswordEditText = findViewById(R.id.name);
        ImageButton loginButton = findViewById(R.id.sign_in);
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signUp();
            }
        });
    }

    private void signUp() {
        String username = usernameEditText.getText().toString();
        String email = emailEditText.getText().toString();
        String password = passwordEditText.getText().toString();
        String confirmPassword = confirmPasswordEditText.getText().toString();

        // Perform signup validation
        if (isValidSignUp(username, email, password, confirmPassword)) {
            // Signup successful, redirect to home page
            Intent intent = new Intent(SignUpActivity.this, UserMapActivity.class);
            startActivity(intent);
            finish(); // Close the SignUpActivity
        } else {
            // Validation failed, show error message
            showToast("Invalid signup details. Please try again.");
        }
    }

    private boolean isValidSignUp(String username, String email, String password, String confirmPassword) {
        // Add your signup validation logic here
        // For simplicity, we're just checking if all fields are non-empty
        return !username.isEmpty() && !email.isEmpty() && !password.isEmpty() && password.equals(confirmPassword);
    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}
