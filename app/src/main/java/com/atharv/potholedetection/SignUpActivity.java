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

    private TextView usernameError;
    private TextView passwordError;
    private TextView confirmPasswordError;

    Config config = new Config();
    private String IP = config.IP;
    private String PORT = config.PORT;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        usernameEditText = findViewById(R.id.username);
        passwordEditText = findViewById(R.id.password2);
        confirmPasswordEditText = findViewById(R.id.re_enter);
        usernameError =(TextView) findViewById(R.id.username_error);
        passwordError = (TextView) findViewById(R.id.password_error);
        confirmPasswordError = (TextView) findViewById(R.id.confirm_password_error);
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
        } else {
            // Validation failed, show error message
//          showToast("Invalid signup details. Please try again.");
        }
    }

    private boolean isValidSignUp(String username, String password, String confirmPassword) {
        if(!username.isEmpty() && !password.isEmpty() && !confirmPassword.isEmpty()){
            if(username.equals(password)){
                usernameError.setText("Username and password shouldn't be same");
                passwordError.setText("Username and password shouldn't be same");
                usernameError.setVisibility(View.VISIBLE);
                passwordError.setVisibility(View.VISIBLE);
                usernameEditText.setText("");
                passwordEditText.setText("");
                confirmPasswordEditText.setText("");
                return false;
            } else if (password.length() < 8) {
                passwordError.setText("password should be more than 8 characters");
                passwordError.setVisibility(View.VISIBLE);
                usernameEditText.setText("");
                passwordEditText.setText("");
                confirmPasswordEditText.setText("");
                return false;
            } else if (!password.equals(confirmPassword)) {
                passwordError.setText("password and confirm password doesn't match");
                confirmPasswordError.setText("password and confirm password doesn't match");
                confirmPasswordError.setVisibility(View.VISIBLE);
                passwordError.setVisibility(View.VISIBLE);
                usernameEditText.setText("");
                passwordEditText.setText("");
                confirmPasswordEditText.setText("");
                return false;
            }else {
                return true;
            }
        }
        return false;
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
            if (result.contains("Error: 301")) {
                usernameError.setText("username already taken");
                usernameError.setVisibility(View.VISIBLE);
                usernameEditText.setText("");
                passwordEditText.setText("");
                confirmPasswordEditText.setText("");
            } else {
                Intent intent = new Intent(SignUpActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        }
    }
}
