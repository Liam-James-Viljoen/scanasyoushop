package com.example.scanasyoushop;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.security.SecureRandom;
import java.util.HashMap;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.Arrays;
import java.util.Optional;
import java.util.Base64;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

public class RegisterPage extends AppCompatActivity {

    //integer codes for GET and POST requests
    private static final int CODE_GET_REQUEST = 1024;
    private static final int CODE_POST_REQUEST = 1025;

    private static final int ITERATIONS = 65536;
    private static final int KEY_LENGTH = 512;
    private static final String ALGORITHM = "PBKDF2WithHmacSHA512";

    //Defining views
    EditText r_usernameText, r_passwordText, r_passwordReEnterText, r_emailText, r_phoneText;

    //
    private static final SecureRandom RAND = new SecureRandom();

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

        if(checkContentsFunction(usernameStr, passwordStr, passwordReEnterStr, emailTextStr, phoneTextStr, r_usernameText, r_passwordText, r_passwordReEnterText, r_emailText, r_phoneText)){
            return;
        }


        String salt = generateSalt(512).toString(); //Call to generate the string

        String protectedPassword = hashPassword(passwordStr, salt).toString(); //Generates the hash using the salt

        registerUser(usernameStr, protectedPassword, salt ,emailTextStr, phoneTextStr); //Sends the data to be packaged into a hash map
    }

    public boolean checkContentsFunction(String usernameStr, String passwordStr, String passwordReEnterStr, String emailTextStr, String phoneTextStr,
                                         EditText r_usernameText, EditText r_passwordText, EditText r_passwordReEnterText, EditText r_emailText, EditText r_phoneText){
        boolean flag = false;
        if (TextUtils.isEmpty(usernameStr)){
            r_usernameText.setError("Please enter username");
            r_usernameText.requestFocus();
            flag = true;
        }
        if (TextUtils.isEmpty(passwordStr)){
            r_passwordText.setError("Please enter password");
            r_passwordText.requestFocus();
            flag = true;
        }
        if (TextUtils.isEmpty(passwordReEnterStr)){
            r_passwordReEnterText.setError("Please enter password");
            r_passwordReEnterText.requestFocus();
            flag = true;
        }
        if (TextUtils.isEmpty(emailTextStr)){
            r_emailText.setError("Please enter password");
            r_emailText.requestFocus();
            flag = true;
        }
        if (TextUtils.isEmpty(phoneTextStr)){
            r_phoneText.setError("Please enter password");
            r_phoneText.requestFocus();
            flag = true;
        }
        if (passwordStr.equals(passwordReEnterStr)){

        }else {
            r_passwordReEnterText.setError("Password does not match");
            r_passwordReEnterText.requestFocus();
            flag = true;
        }

        return flag;
    }

    public void registerUser(String username, String password, String email, String phonenumber, String salt){ //Sends the request to PerformNetworkRequestClass
        HashMap<String, String> params = new HashMap<>();
        params.put("username", username);
        params.put("password", password);
        params.put("email", email);
        params.put("phonenumber", phonenumber);
        params.put("salt", salt);

        RegisterPage.PerformNetworkRequest request = new RegisterPage.PerformNetworkRequest(Api.URL_CREATE_USER, params, CODE_POST_REQUEST);
        request.execute();
    }

    //Refrence - https://dev.to/awwsmm/how-to-encrypt-a-password-in-java-42dh
    public static Optional<String> generateSalt (final int length) {

        if (length < 1) { //Checks lenght is less than 1
            System.err.println("error in generateSalt: length must be > 0");
            return Optional.empty();
        }

        byte[] salt = new byte[length];
        RAND.nextBytes(salt); //Generates salt

        return Optional.of(Base64.getEncoder().encodeToString(salt)); //Returns salt in base64
    }

    //Refrence - https://dev.to/awwsmm/how-to-encrypt-a-password-in-java-42dh
    public static Optional<String> hashPassword (String password, String salt) {

        char[] chars = password.toCharArray(); //Turns password to character array for security purposes (less paper trail)
        byte[] bytes = salt.getBytes();

        PBEKeySpec spec = new PBEKeySpec(chars, bytes, ITERATIONS, KEY_LENGTH);

        Arrays.fill(chars, Character.MIN_VALUE);

        try {
            SecretKeyFactory fac = SecretKeyFactory.getInstance(ALGORITHM);
            byte[] securePassword = fac.generateSecret(spec).getEncoded();
            return Optional.of(Base64.getEncoder().encodeToString(securePassword)); //Returns hashed password in base64

        } catch (NoSuchAlgorithmException | InvalidKeySpecException ex) {
            System.err.println("Exception encountered in hashPassword()");
            return Optional.empty();

        } finally {
            spec.clearPassword();
        }
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
