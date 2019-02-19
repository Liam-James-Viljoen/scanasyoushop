package com.example.scanasyoushop;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;


public class MainActivity extends AppCompatActivity {

    //Defining views
    EditText usernameText, passwordText;

    //
    JSONArray user = new JSONArray();

    //integer codes for GET and POST requests
    private static final int CODE_GET_REQUEST = 1024;
    private static final int CODE_POST_REQUEST = 1025;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }
    public void mainMenuPageFunction(View view) throws JSONException { //Triggered on login button
        usernameText = (EditText)findViewById(R.id.usernameText);
        String usernameStr = usernameText.getText().toString().trim();

        passwordText = (EditText)findViewById(R.id.passwordText);
        String passwordStr = passwordText.getText().toString().trim();


        //Android studio doesn't like me using this for some unknown reason

        if (TextUtils.isEmpty(usernameStr) && TextUtils.isEmpty(passwordStr)){
            usernameText.setError("Please enter username");
            usernameText.requestFocus();
            passwordText.setError("Please enter password");
            passwordText.requestFocus();
            return;
        }
        if (TextUtils.isEmpty(usernameStr)){
            usernameText.setError("Please enter username");
            usernameText.requestFocus();
            return;
        }
        if (TextUtils.isEmpty(passwordStr)){
            passwordText.setError("Please enter password");
            passwordText.requestFocus();
            return;
        }

        readUsers(usernameStr, passwordStr);
    }
    public void registerPageFunction(View view){
        startActivity(new Intent(this, RegisterPage.class)); //Creates instance of the page
    }
    public void startMenuFunction(){
        startActivity(new Intent(this, MainMenu.class)); //Creates instance of the page
    }

    public void readUsers(String username, String password){ //Sends the request to PerformNetworkRequestClass
        HashMap<String, String> params = new HashMap<>();
        params.put("username", username);
        PerformNetworkRequest request = new PerformNetworkRequest(Api.URL_SELECT_USER, params, CODE_POST_REQUEST, username, password);
        request.execute();
    }


    // REFRENCE ------------> https://www.simplifiedcoding.net/android-mysql-tutorial-to-perform-basic-crud-operation/
    //Inner class to perform network requests
    private class PerformNetworkRequest extends AsyncTask<Void, Void, String> {
        //the url where we need to send the request
        String url;

        //the parameters
        HashMap<String, String> params;

        //the request code to define whether it is a GET or POST
        int requestCode;

        //User entered username and password
        String userentrUsername;
        String userentrPassword;

        //constructor to initialize values
        PerformNetworkRequest(String url, HashMap<String, String> params, int requestCode, String userentrUsername, String userentrPassword) {
            this.url = url;
            this.params = params;
            this.requestCode = requestCode;
            this.userentrUsername = userentrUsername;
            this.userentrPassword = userentrPassword;
        }

        /* this method will give the response from the request */
        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            try {
                Log.i("Variable Contents:", s);
                JSONObject object = new JSONObject(s); //Seems to turn returned data into a JSON object

                if (!object.getBoolean("error")) {
                    Toast.makeText(getApplicationContext(), object.getString("message"), Toast.LENGTH_SHORT).show(); //pop-up message
                    user = object.getJSONArray("user");

                    String lclUsername;
                    String lclPassword;
                    lclUsername = user.getJSONObject(0).get("username").toString(); // Assigns username from JSON object to string
                    lclPassword = user.getJSONObject(0).get("password").toString(); // Assigns password from JSON object to string

                    //Log.i("Variable Contents 2", );
                    if (userentrPassword.equals(lclPassword)){ //Checks to see if password equals inputed password
                        startMenuFunction(); //Call to function that opens next page
                    }

                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        //the network operation will be performed in background
        @Override
        protected String doInBackground(Void... voids) {
            RequestHandler requestHandler = new RequestHandler();

            if (requestCode == CODE_POST_REQUEST)
                return requestHandler.sendPostRequest(url, params);


            if (requestCode == CODE_GET_REQUEST)
                return requestHandler.sendGetRequest(url);

            return null;
        }
    }
}


