package com.example.scanasyoushop;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Paint;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
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

public class ChecklistScan extends AppCompatActivity {

    //
    JSONArray originalListItems;
    JSONArray currentListItems = new JSONArray();

    //integer codes for GET and POST requests
    private static final int CODE_GET_REQUEST = 1024;
    private static final int CODE_POST_REQUEST = 1025;

    //
    NumberFormat formatter = new DecimalFormat("#0.00");
    Double total = 000.00;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checklist_scan);

        //Gets the list object in string form
        Bundle bundle = getIntent().getExtras();
        String str_list;
        str_list = bundle.getString("list");

        try{
            JSONObject listWithWrapper = new JSONObject(str_list); //converts string form list to JSON Object
            originalListItems = new JSONArray(listWithWrapper.get("Items").toString()); //Reads item list into JSON Array

            //Sets the list name
            TextView tV_list_name = (TextView) findViewById(R.id.tV_list_name);
            tV_list_name.setText(listWithWrapper.getString("List name"));
            tV_list_name.setPaintFlags(tV_list_name.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);

            set_original_list();

        }catch (JSONException e){
            e.printStackTrace();
        }
    }

    public void set_original_list(){
        ArrayList<JSONObject> items = new ArrayList<JSONObject>();
        try {
            if (originalListItems != null){
                for (int i=0; i<originalListItems.length(); i++){
                    items.add(originalListItems.getJSONObject(i));
                }
            }
        } catch (JSONException e){
            e.printStackTrace();
        }
        final ListView original_list = (ListView)findViewById(R.id.original_list);
        CustomListAdapter_stl customListAdapter_stl = new CustomListAdapter_stl(this, R.layout.listview_row_stl, items);
        original_list.setAdapter(customListAdapter_stl);
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
            readItems(bar_Code);
        }
    }
    public void readItems(String bar_Code){ //Sends the request to PerformNetworkRequestClass
        HashMap<String, String> params = new HashMap<>();
        params.put("bar_code", bar_Code);
        PerformNetworkRequest request = new PerformNetworkRequest(Api.URL_SELECT_ITEM, params, CODE_POST_REQUEST);
        request.execute();
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

        currentListItems.put(item);
        removeFromChecklist(item_name);
        refresh_list();
    }
    public void removeFromChecklist(String item_name){
        try {
            if (originalListItems != null){
                for (int i=0; i<originalListItems.length(); i++){
                    if (originalListItems.getJSONObject(i).get("item_name").equals(item_name)){
                        originalListItems.remove(i);
                        break;
                    }
                }
            }
        } catch (JSONException e){
            e.printStackTrace();
        }
        set_original_list();
    }
    public void refresh_list(){
        ArrayList<JSONObject> item = new ArrayList<JSONObject>();
        try {
            if (currentListItems != null){
                for (int i=0; i<currentListItems.length(); i++){
                    item.add(currentListItems.getJSONObject(i));
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
    public void deleteItem(int position)throws JSONException{
        String price = currentListItems.getJSONObject(position).get("price").toString();
        Double temp = Double.parseDouble(price);
        total = total - temp;
        String str_total = "£" .concat(formatter.format(total).toString());
        TextView tv_total_sum = (TextView) findViewById(R.id.tv_total_sum);
        tv_total_sum.setText(str_total);

        currentListItems.remove(position);
        refresh_list();
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
