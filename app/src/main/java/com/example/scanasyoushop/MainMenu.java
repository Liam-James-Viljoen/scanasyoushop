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
import android.widget.AdapterView;
import android.widget.Button;
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

    //
    JSONArray list_of_lists_JA = new JSONArray();
    ArrayList<JSONObject> list_of_lists_AL = new ArrayList<JSONObject>();

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

        list_of_lists_JA = jsonFileReadWriter.readFile(this, currentUser);//Add list to users list of lists

        try {
            if (list_of_lists_JA != null){
                for (int i=0; i<list_of_lists_JA.length(); i++){
                    list_of_lists_AL.add(list_of_lists_JA.getJSONObject(i));
                }
            }
        } catch (JSONException e){
            e.printStackTrace();
        }

        final ListView lists = (ListView)findViewById(R.id.list_of_lists);
        CustomListAdapter_mm customListAdapter_stl = new CustomListAdapter_mm(this, R.layout.listview_row_mm, list_of_lists_AL);
        lists.setAdapter(customListAdapter_stl);
        final JSONArray finalList_of_lists_JA = list_of_lists_JA;
        lists.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, final int position, long l) {
                AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());

                View viewInflated = LayoutInflater.from(view.getContext()).inflate(R.layout.popup_start_checklist, (ViewGroup) findViewById(android.R.id.content), false);

                builder.setView(viewInflated);

                Button b_start_checklist = (Button) viewInflated.findViewById(R.id.b_start_checklist);
                b_start_checklist.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        //Start checklist - Need to pass data into here
                        try {
                            JSONObject selectedList = new JSONObject();
                            selectedList = list_of_lists_JA.getJSONObject(position);
                            openChecklistScanPage(selectedList);
                        }catch (JSONException e){
                            e.printStackTrace();
                        }
                    }
                });

                Button b_delete_list = (Button) viewInflated.findViewById(R.id.b_delete_list);
                b_delete_list.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        //Delete List
                        list_of_lists_JA.remove(position);
                        saveListState();
                    }
                });
                builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                // Set up the buttons
                /*
                builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        try {
                            JSONObject selectedList = new JSONObject();
                            selectedList = list_of_lists_JA.getJSONObject(position);
                            openChecklistScanPage(selectedList);
                        }catch (JSONException e){
                            e.printStackTrace();
                        }
                        dialog.dismiss();
                    }
                });

                */
                builder.show();
            }
        });

    }
    public void saveListState(){
        //Intitializes read writer
        JSONFileReadWriter jsonFileReadWriter = new JSONFileReadWriter();
        jsonFileReadWriter.writeFile(this, currentUser, list_of_lists_JA);
        list_of_lists_AL.clear();

        refreshList();
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
                openScanToListPage(list_name);
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
    public void openScanToListPage(String list_name){
        startActivity(new Intent(this, ScanToList.class).putExtra("list_name", list_name));
    }
    public void openChecklistScanPage(JSONObject selectedList){
        startActivity(new Intent(this, ChecklistScan.class).putExtra("list", selectedList.toString()));
    }
    public void openUnNamedList(View view){
        startActivity(new Intent(this, UnNamedScanToList.class));
    }
    public void test(View view){
        Log.i("xxxxxxxxxxx", "This is a test");
    }
}
