package com.example.scanasyoushop;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class CustomListAdapter_stl extends ArrayAdapter<JSONObject> {
    int vg;
    ArrayList<JSONObject>list;
    Context context;
    public CustomListAdapter_stl(Context context, int vg, ArrayList<JSONObject>list){
        super(context, vg, list);
        this.context = context;
        this.vg=vg;
        this.list=list;
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent){
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View itemView = inflater.inflate(vg, parent, false);

        TextView tv_lv_item_name=(TextView)itemView.findViewById(R.id.tv_lv_item_name);
        TextView tv_lv_price=(TextView)itemView.findViewById(R.id.tv_lv_price);
        try {
            tv_lv_item_name.setText(list.get(position).getString("item_name"));
            tv_lv_price.setText(list.get(position).getString("price"));
        } catch (JSONException e) {

            e.printStackTrace();

        }
        if (itemView != null){
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Log.i("Variable Contents:", "It works");
                }
            });
        }
        return itemView;
    }
}
