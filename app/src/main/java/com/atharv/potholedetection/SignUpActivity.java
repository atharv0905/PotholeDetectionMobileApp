package com.atharv.potholedetection;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class SignUpActivity extends AppCompatActivity {

    private EditText usernameEditText;
    private EditText passwordEditText;
    private EditText confirmPasswordEditText;
    private TextView usernameErrorTextView;
    private TextView passwordErrorTextView;
    private TextView confirmPasswordErrorTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        usernameEditText = findViewById(R.id.username);
        passwordEditText = findViewById(R.id.password2);
        confirmPasswordEditText = findViewById(R.id.re_enter);
        usernameErrorTextView = findViewById(R.id.username_error);
        passwordErrorTextView = findViewById(R.id.password_error);
        confirmPasswordErrorTextView = findViewById(R.id.confirm_password_error);

        // Add focus change listener to each EditText field
        usernameEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus && isErrorMessageVisible()) resetSignUpPage();
            }
        });

        passwordEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus && isErrorMessageVisible()) resetSignUpPage();
            }
        });

        confirmPasswordEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus && isErrorMessageVisible()) resetSignUpPage();
            }
        });

        ImageButton signUpButton = findViewById(R.id.sign_in);
        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signUp();
            }
        });
    }

    private void signUp() {
        String username = usernameEditText.getText().toString();
        String password = passwordEditText.getText().toString();
        String confirmPassword = confirmPasswordEditText.getText().toString();

        // Reset error messages and the page
        resetSignUpPage();

        // Validate username
        if (username.isEmpty()) {
            showErrorMessage(usernameErrorTextView, "Please fill in your username");
            return;
        }

        // Validate password
        if (password.isEmpty()) {
            showErrorMessage(passwordErrorTextView, "Please fill in your password");
            return;
        }

        // Validate confirm password
        if (confirmPassword.isEmpty()) {
            showErrorMessage(confirmPasswordErrorTextView, "Please fill in your confirm password");
            return;
        }

        // Check if username and password are the same
        if (username.equals(password)) {
            showErrorMessage(passwordErrorTextView, "Username and password cannot be the same");
            return;
        }

        // Check if password matches confirm password
        if (!password.equals(confirmPassword)) {
            showErrorMessage(confirmPasswordErrorTextView, "Passwords do not match");
            return;
        }

        // Perform signup if all fields are valid
        showToast("Signup successful!"); // Replace this with your signup logic

        // Redirect to the login page after sign up
        Intent intent = new Intent(SignUpActivity.this, LoginActivity.class);
        startActivity(intent);

        // Reset all fields and error messages
        resetSignUpPage();

        // Call your API for signup here
        new ApiCaller().execute("your parameters"); // Replace "your parameters" with actual data to be passed to the API
    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    private void resetSignUpPage() {
        // Clear all EditText fields
        usernameEditText.setText("");
        passwordEditText.setText("");
        confirmPasswordEditText.setText("");

        // Hide all error messages
        usernameErrorTextView.setVisibility(View.GONE);
        passwordErrorTextView.setVisibility(View.GONE);
        confirmPasswordErrorTextView.setVisibility(View.GONE);
    }

    private void showErrorMessage(TextView errorTextView, String errorMessage) {
        errorTextView.setVisibility(View.VISIBLE);
        errorTextView.setText(errorMessage);
    }

    private boolean isErrorMessageVisible() {
        // Check if any error message is visible
        return usernameErrorTextView.getVisibility() == View.VISIBLE ||
                passwordErrorTextView.getVisibility() == View.VISIBLE ||
                confirmPasswordErrorTextView.getVisibility() == View.VISIBLE;
    }


    private class ApiCaller extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {
            String apiUrl = "http://example.com/signup"; // Your signup API endpoint
            String postData = params[0];
            try {
                URL url = new URL(apiUrl);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("POST");
                connection.setRequestProperty("Content-Type", "application/json");
                connection.setDoOutput(true);

                // Write data to the server
                connection.getOutputStream().write(postData.getBytes());

                // Read response from the server
                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                StringBuilder response = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }
                reader.close();

                // Return the response from the server
                return response.toString();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return apiUrl;
        }
    }
}
