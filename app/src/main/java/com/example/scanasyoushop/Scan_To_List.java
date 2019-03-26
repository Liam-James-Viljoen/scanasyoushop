package com.example.scanasyoushop;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

public class Scan_To_List extends AppCompatActivity {

    //integer codes for GET and POST requests
    private static final int CODE_GET_REQUEST = 1024;
    private static final int CODE_POST_REQUEST = 1025;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan__to__list);

        //Gets the name from the previous intent and sets the text view to its value
        Bundle bundle = getIntent().getExtras();
        String list_name = bundle.getString("list_name");
        TextView tV_list_name = (TextView) findViewById(R.id.tV_list_name);
        tV_list_name.setText(list_name);

    }

    //Scans the barcode using the third party application
    public void scanbarcode (View view){
        IntentIntegrator intentIntegrator = new IntentIntegrator(this);
        intentIntegrator.initiateScan(IntentIntegrator.ALL_CODE_TYPES);
    }
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        IntentResult scanResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, intent);
        String bar_Code = scanResult.getContents();
        if (scanResult != null) {
            Log.i("Variable Contents:", bar_Code);
        }
        // else continue with any other code you need in the method
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
                    //user = object.getJSONArray("user");

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
