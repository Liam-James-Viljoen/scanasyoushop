package com.example.scanasyoushop;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


public class MainMenu extends AppCompatActivity {

    //
    SharedPreferences sharedPref;
    String currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);

        sharedPref= PreferenceManager
                .getDefaultSharedPreferences(this);
        currentUser = sharedPref.getString("Username", "");

        refreshList();
    }

    public void refreshList(){
        //Intitializes read writer
        JSONFileReadWriter jsonFileReadWriter = new JSONFileReadWriter();

        JSONArray list_of_lists_JA = new JSONArray();

        ArrayList<JSONObject> list_of_lists_AL = new ArrayList<JSONObject>();

        list_of_lists_JA = jsonFileReadWriter.readFile(this, currentUser);//Add list to users list of lists

        Log.i("YYYYY", list_of_lists_JA.toString());

        try {
            for (int i=0; i<list_of_lists_JA.length(); i++){
                list_of_lists_AL.add(list_of_lists_JA.getJSONObject(i));
                Log.i("XXXXX", list_of_lists_JA.getJSONObject(i).toString());
            }
        } catch (JSONException e){
            e.printStackTrace();
        }

        final ListView lists = (ListView)findViewById(R.id.list_of_lists);
        CustomListAdapter_mm customListAdapter_stl = new CustomListAdapter_mm(this, R.layout.listview_row_mm, list_of_lists_AL);
        lists.setAdapter(customListAdapter_stl);

    }

    public void dialogBoxActivated(View view){
        AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
        builder.setTitle("Set List Name");

        View viewInflated = LayoutInflater.from(view.getContext()).inflate(R.layout.popup_name_list, (ViewGroup) findViewById(android.R.id.content), false);
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

    public void openPage(String list_name){
        startActivity(new Intent(this, Scan_To_List.class).putExtra("list_name", list_name));
    }

}
