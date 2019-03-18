package com.example.neno.rentacar;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by neno on 22.11.2015.
 * This class is responsible for sending login credentials to server.
 */
public class LoginActivity extends ActionBarActivity {

    private static final String TAG_USERNAME = "username";
    private static final String TAG_PASS = "pass";
    private static final String TAG_SUCCESS = "success";
    private static final String TAG_MESSAGE = "message";
    private static final String LOGIN_SCRIPT = Constants.SERVER_IP_ADDRESS + Constants.LOGIN_SCRIPT_PATH;

    JSONParser jsonParser = new JSONParser();
    private ProgressDialog pDialog;
    EditText username;
    EditText password;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        username = (EditText) findViewById(R.id.username);
        password = (EditText) findViewById(R.id.password);
        Button loginButton = (Button) findViewById(R.id.login);
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new postLoginCredentialToServer().execute();
            }
        });
    }

    class postLoginCredentialToServer extends AsyncTask<String, String, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(LoginActivity.this);
            pDialog.setMessage("Pokušaj logina ...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }

        protected String doInBackground(String... args) {

            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair(TAG_USERNAME, username.getText().toString()));
            params.add(new BasicNameValuePair(TAG_PASS, password.getText().toString()));
            JSONObject json = jsonParser.makeHttpRequest(LOGIN_SCRIPT, "POST", params);

            try {
                int success = json.getInt(TAG_SUCCESS);
                if (success == 1) {
                    Log.d("Response", "Login successful.");
                    Intent intent = new Intent(getApplicationContext(), HomePageActivity.class);
                    finish();
                    startActivity(intent);
                    return json.getString(TAG_MESSAGE);
                } else {
                    Log.d("Response", "Login credentials wrong!");
                    return json.getString(TAG_MESSAGE);////message varijabla iz php skripte
                }
            } catch (JSONException e) {
                e.printStackTrace();
                Log.d("Debug", e.toString());
            }
            return null;
        }

        protected void onPostExecute(String message) {
            pDialog.dismiss();
            if (message != null) {
                Toast.makeText(LoginActivity.this, message, Toast.LENGTH_LONG).show(); //message varijablu pretvori u toast
            }
        }
    }
}
