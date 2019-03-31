package com.example.scanasyoushop;

import android.content.Context;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class JSONFileReadWriter {


    public JSONObject readFile(Context context, String username){
        try {
            File file = new File(context.getFilesDir().getPath() +  "/" + username);
            FileInputStream fileInputStream = new FileInputStream(file);
            int size = fileInputStream.available();
            byte[] buffer = new byte[size];
            fileInputStream.read(buffer);
            fileInputStream.close();
            String mResponse = new String(buffer);
            JSONObject returnList = new JSONObject(mResponse);
            return returnList;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }
    public void writeFile(Context context, String username, JSONObject list){
        try {
            FileWriter file = new FileWriter(context.getFilesDir().getPath() +  "/" + username);
            file.write(list.toString());
            file.flush();
            file.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
