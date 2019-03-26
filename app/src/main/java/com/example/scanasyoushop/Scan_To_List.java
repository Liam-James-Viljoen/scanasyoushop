package com.example.scanasyoushop;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

public class Scan_To_List extends AppCompatActivity {

    //integer codes for GET and POST requests
    private static final int CODE_GET_REQUEST = 1024;
    private static final int CODE_POST_REQUEST = 1025;

    //
    JSONObject list_JSON_obj = new JSONObject();
    JSONArray list_items_JSON_array = new JSONArray();

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

        if (scanResult != null) {
            String bar_Code = scanResult.getContents();
            bar_Code.replaceAll("[^\\d]", "" );
            Log.i("Variable Contents:", bar_Code);
            readItems(bar_Code);

        }
        // else continue with any other code you need in the method
    }

    public void add_item(JSONArray returnedInfo) throws JSONException {
        JSONObject item = new JSONObject();
        String item_name = returnedInfo.getJSONObject(0).get("item_name").toString();
        String price = returnedInfo.getJSONObject(0).get("price").toString();
        item.put("item_name", item_name);
        item.put("price", price);

        list_items_JSON_array.put(item);
        Log.i("Variable Contents:", list_items_JSON_array.toString());
    }

    public void readItems(String bar_Code){ //Sends the request to PerformNetworkRequestClass
        HashMap<String, String> params = new HashMap<>();
        params.put("bar_code", bar_Code);
        PerformNetworkRequest request = new PerformNetworkRequest(Api.URL_SELECT_ITEM, params, CODE_POST_REQUEST);
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
                JSONObject object = new JSONObject(s);

                if (!object.getBoolean("error")) {
                    Toast.makeText(getApplicationContext(), object.getString("message"), Toast.LENGTH_SHORT).show(); //pop-up message
                    Log.i("Variable Contents:", object.getJSONArray("item").toString());
                    add_item(object.getJSONArray("item"));

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
