package com.example.scanasyoushop;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;

public class UnNamedScanToList extends AppCompatActivity {

    //integer codes for GET and POST requests
    private static final int CODE_GET_REQUEST = 1024;
    private static final int CODE_POST_REQUEST = 1025;

    //
    JSONObject list_JSON_obj = new JSONObject();
    JSONArray list_items_JSON_array = new JSONArray();

    //
    NumberFormat formatter = new DecimalFormat("#0.00");
    Double total = 000.00;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_un_named_scan_to_list);
    }
    //Scans the barcode using the third party application
    public void scanbarcode (View view){
        IntentIntegrator intentIntegrator = new IntentIntegrator(this);
        intentIntegrator.initiateScan(IntentIntegrator.ALL_CODE_TYPES);
    }
    public void readItems(String bar_Code){ //Sends the request to PerformNetworkRequestClass
        HashMap<String, String> params = new HashMap<>();
        params.put("bar_code", bar_Code);
        UnNamedScanToList.PerformNetworkRequest request = new UnNamedScanToList.PerformNetworkRequest(Api.URL_SELECT_ITEM, params, CODE_POST_REQUEST);
        request.execute();
    }
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        IntentResult scanResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, intent);

        if (scanResult != null) {
            String bar_Code = scanResult.getContents();
            bar_Code.replaceAll("[^\\d]", "" );
            readItems(bar_Code);
        }
    }
    public void add_item(JSONArray returnedInfo) throws JSONException {
        JSONObject item = new JSONObject();
        String item_name = returnedInfo.getJSONObject(0).get("item_name").toString();
        String price = returnedInfo.getJSONObject(0).get("price").toString();
        item.put("item_name", item_name);
        item.put("price", price);

        Double temp = Double.parseDouble(price);
        total = temp + total;
        String str_total = "£" .concat(formatter.format(total).toString());
        TextView tv_total_sum = (TextView) findViewById(R.id.tv_total_sum);
        tv_total_sum.setText(str_total);

        list_items_JSON_array.put(item);
        refresh_list();
    }
    public void deleteItem(int position)throws JSONException{
        String price = list_items_JSON_array.getJSONObject(position).get("price").toString();
        Double temp = Double.parseDouble(price);
        total = total - temp;
        String str_total = "£" .concat(formatter.format(total).toString());
        TextView tv_total_sum = (TextView) findViewById(R.id.tv_total_sum);
        tv_total_sum.setText(str_total);

        list_items_JSON_array.remove(position);
        refresh_list();
    }
    public void refresh_list(){
        ArrayList<JSONObject> item = new ArrayList<JSONObject>();
        try {
            if (list_items_JSON_array != null){
                for (int i=0; i<list_items_JSON_array.length(); i++){
                    item.add(list_items_JSON_array.getJSONObject(i));
                }
            }
        } catch (JSONException e){
            e.printStackTrace();
        }
        final ListView list_of_items = (ListView)findViewById(R.id.scaned_items_list);
        CustomListAdapter_stl customListAdapter_stl = new CustomListAdapter_stl(this, R.layout.listview_row_stl, item);
        list_of_items.setAdapter(customListAdapter_stl);
        list_of_items.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, final int position, long l) {
                AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());

                View viewInflated = LayoutInflater.from(view.getContext()).inflate(R.layout.popup_remove_list_item, (ViewGroup) findViewById(android.R.id.content), false);
                // Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
                builder.setView(viewInflated);

                // Set up the buttons
                builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        try {
                            deleteItem(position);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
                builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

                builder.show();
            }
        });
    }
    public void checkout(View view){
        startActivity(new Intent(this, PaymentCheckout.class));
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

