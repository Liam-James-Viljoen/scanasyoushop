package com.example.scanasyoushop;

import android.content.Intent;
import android.graphics.Paint;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;

public class ChecklistScan extends AppCompatActivity {

    //
    JSONArray originalListItems;
    JSONArray currentListItems;

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
            //readItems(bar_Code);
        }
    }
}
