package com.example.scanasyoushop;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

public class MainActivity extends AppCompatActivity {

    //Defining views
    EditText usernameText, passwordText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }
    public void mainMenuPageFunction(View view){ //If login is validated as succesfull
        startActivity(new Intent(this, mainMenu.class)); //Creates instance of the page
    }
    public void registerPageFunction(View view){ //If login is validated as succesfull
        startActivity(new Intent(this, registerPage.class)); //Creates instance of the page
    }
}

