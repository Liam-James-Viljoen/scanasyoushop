package com.example.scanasyoushop;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class CustomListAdapter_mm extends ArrayAdapter<JSONObject> {
    int vg;
    ArrayList<JSONObject> list;
    Context context;
    public CustomListAdapter_mm(Context context, int vg, ArrayList<JSONObject>list){
        super(context, vg, list);
        this.context = context;
        this.vg=vg;
        this.list=list;
    }
    @Override
    public View getView(final int position, View convertView, ViewGroup parent){
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View itemView = inflater.inflate(vg, parent, false);

        TextView tv_lv_list_name=(TextView)itemView.findViewById(R.id.tv_lv_list_name);


        try {
            tv_lv_list_name.setText(list.get(position).getString("List name"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return itemView;
    }
}
