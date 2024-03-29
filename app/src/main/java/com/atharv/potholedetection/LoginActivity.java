package com.atharv.potholedetection;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
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

public class LoginActivity extends AppCompatActivity {

    private SharedPreferences sharedPreferences;
    private SharedPreferences userSharedPreferences;
    private String TOKEN = "Token";
    private String USER_AUTHENTICATION_PREF_NAME = "USER_AUTHENTICATION";

    private String USER = "USER";
    private String USER_PREF_NAME = "USERNAME";
    String username = "";
    String password = "";

    EditText editTextUsername;
    EditText editTextPassword;
    Config config = new Config();
    private String IP = config.IP;
    private String PORT = config.PORT;
    private String userType = "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        sharedPreferences = getSharedPreferences(USER_AUTHENTICATION_PREF_NAME, MODE_PRIVATE);
        userSharedPreferences = getSharedPreferences(USER_PREF_NAME, MODE_PRIVATE);
        editTextUsername = (EditText) findViewById(R.id.username_edittext);
        editTextPassword = (EditText) findViewById(R.id.password_login);
        Button loginButton = (Button) findViewById(R.id.login1);
        TextView signUpButton = (TextView) findViewById(R.id.signUpButton);

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                username = editTextUsername.getText().toString();
                password = editTextPassword.getText().toString();

                // Perform login validation (Replace this with your own logic)
                if (isValidLogin(username, password)) {
                    // Navigate to the sign-up activity
                    JSONObject data = new JSONObject();
                    try {
                        data.put("username", username);
                        data.put("password", password);
                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    }
                    new ApiCaller().execute(data.toString());
                } else {
//                    showToast("Invalid credentials. Please try again.");
                }
            }
        });

        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(LoginActivity.this, SignUpActivity.class);
                startActivity(intent);
            }
        });
    }

    private boolean isValidLogin(String username, String password) {
        // Add your login validation logic here
        // For simplicity, we consider it valid if both email and password are not empty
        if(username.contains("@emp")){
            userType = "employee";
        }else {
            userType = "user";
        }
        return !username.isEmpty() && !password.isEmpty();
    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    private class ApiCaller extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {
            String apiUrl = "http://"+ IP + ":" + PORT + "/"+userType+"/login";
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

            // saving token
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString(TOKEN, result);
            editor.apply();

            // saving username
            SharedPreferences.Editor userEditor = userSharedPreferences.edit();
            userEditor.putString(USER, username);
            userEditor.apply();

            if(result.contains("Error")){
                TextView errorMsg1 = (TextView) findViewById(R.id.errorMsg1);
                TextView errorMsg2 = (TextView) findViewById(R.id.errorMsg2);
                editTextUsername.setText("");
                editTextPassword.setText("");
                errorMsg1.setVisibility(View.VISIBLE);
                errorMsg2.setVisibility(View.VISIBLE);
            }else{
                Intent intent = new Intent(LoginActivity.this, UserMapActivity.class);
                startActivity(intent);
            }

        }
    }
}