package com.example.scanasyoushop;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

public class RegisterPage extends AppCompatActivity {

    //integer codes for GET and POST requests
    private static final int CODE_GET_REQUEST = 1024;
    private static final int CODE_POST_REQUEST = 1025;

    //Defining views
    EditText r_usernameText, r_passwordText, r_passwordReEnterText, r_emailText, r_phoneText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_page);
    }

    public void mainRegisterUserFunction(View view){
        r_usernameText = (EditText)findViewById(R.id.r_usernameText);
        String usernameStr = r_usernameText.getText().toString().trim();

        r_passwordText = (EditText)findViewById(R.id.r_passwordText);
        String passwordStr = r_passwordText.getText().toString().trim();

        r_passwordReEnterText = (EditText)findViewById(R.id.r_passwordReEnterText);
        String passwordReEnterStr = r_passwordReEnterText.getText().toString().trim();

        r_emailText = (EditText)findViewById(R.id.r_emailText);
        String emailTextStr = r_emailText.getText().toString().trim();

        r_phoneText = (EditText)findViewById(R.id.r_phoneText);
        String phoneTextStr = r_phoneText.getText().toString().trim();


    }

    public void registerUser(String username, String password, String email, String phonenumber){ //Sends the request to PerformNetworkRequestClass
        HashMap<String, String> params = new HashMap<>();
        params.put("username", username);
        params.put("password", password);
        params.put("email", email);
        params.put("phonenumber", phonenumber);

        RegisterPage.PerformNetworkRequest request = new RegisterPage.PerformNetworkRequest(Api.URL_SELECT_USER, params, CODE_POST_REQUEST);
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


        //constructor to initialize values
        PerformNetworkRequest(String url, HashMap<String, String> params, int requestCode) {
            this.url = url;
            this.params = params;
            this.requestCode = requestCode;
        }

        /* this method will give the response from the request */
        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            try {
                JSONObject object = new JSONObject(s); //Seems to turn returned data into a JSON object
                if (!object.getBoolean("error")) {
                    Toast.makeText(getApplicationContext(), object.getString("message"), Toast.LENGTH_SHORT).show(); //pop-up message

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
