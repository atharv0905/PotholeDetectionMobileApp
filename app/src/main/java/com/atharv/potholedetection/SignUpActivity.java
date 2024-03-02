package com.atharv.potholedetection;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class SignUpActivity extends AppCompatActivity {

    private EditText usernameEditText;
    private EditText passwordEditText;
    private EditText confirmPasswordEditText;

    private String IP = "192.168.0.118";
    private String PORT = "3000";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        usernameEditText = findViewById(R.id.username);
        passwordEditText = findViewById(R.id.password2);
        confirmPasswordEditText = findViewById(R.id.re_enter);
        ImageButton loginButton = findViewById(R.id.sign_in);
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    signUp();
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
            }
        });
    }

    private void signUp() throws JSONException {
        String username = usernameEditText.getText().toString();
        String password = passwordEditText.getText().toString();
        String confirmPassword = confirmPasswordEditText.getText().toString();

        // Perform signup validation
        if (isValidSignUp(username, password, confirmPassword)) {
            // Signup successful, redirect to home page
            JSONObject data = new JSONObject();
            data.put("username", username);
            data.put("password", password);
            new ApiCaller().execute(data.toString());
            Intent intent = new Intent(SignUpActivity.this, LoginActivity.class);
            startActivity(intent);
            finish(); // Close the SignUpActivity
        } else {
            // Validation failed, show error message
//          showToast("Invalid signup details. Please try again.");
        }
    }

    private boolean isValidSignUp(String username, String password, String confirmPassword) {
        // Add your signup validation logic here
        // For simplicity, we're just checking if all fields are non-empty
        return !username.isEmpty() && !password.isEmpty() && !confirmPassword.isEmpty();
    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    private class ApiCaller extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {
            String apiUrl = "http://"+ IP + ":" + PORT + "/user/create";
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
            if (result != null) {
//                Toast.makeText(getApplicationContext(), result, Toast.LENGTH_LONG).show();
            } else {
//                Toast.makeText(getApplicationContext(), "Error: No response from server", Toast.LENGTH_LONG).show();
            }
        }
    }
}
