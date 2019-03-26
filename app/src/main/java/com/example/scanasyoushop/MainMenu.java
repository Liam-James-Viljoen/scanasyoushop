package com.example.scanasyoushop;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


public class MainMenu extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);
    }

    public void dialogBoxActivated(View view){
        AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
        builder.setTitle("Set List Name");

        View viewInflated = LayoutInflater.from(view.getContext()).inflate(R.layout.test, (ViewGroup) findViewById(android.R.id.content), false);
        // Set up the input
        final EditText input = (EditText) viewInflated.findViewById(R.id.inputListName);
        // Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
        builder.setView(viewInflated);

        // Set up the buttons
        builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                String list_name = input.getText().toString();
                openPage(list_name);
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
    //Opens page
    public void openPage(String list_name){
        startActivity(new Intent(this, Scan_To_List.class).putExtra("list_name", list_name));

    }
    //test
    public JSONObject encaseJson(JSONArray info) throws JSONException {
        JSONObject encasement = new JSONObject();
        encasement.put("List name", "test");
        encasement.put("List", info);
        Log.i("Variable Contents 1", encasement.toString());
        return encasement;
    }
}
