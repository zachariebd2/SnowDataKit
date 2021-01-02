/*
 * Copyright (c) Salah-Eddine ET-TALEBY, CESBIO 2020
 *               Zacharie BARROU DUMONT, CESBIO 2020
 *
 * Activity page for account management
 */

package com.cesbio.SDK.activities;
import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import com.cesbio.SDK.R;
import com.cesbio.SDK.traitements.SessionManager;

public class Account extends AppCompatActivity {

    private String username;
    private int id_user;
    private SessionManager sessionManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        //get activity layouts //////////////////
        setContentView(R.layout.activity_account);
        Button btn_update_password = findViewById(R.id.btn_update_password);
        Button btn_disconnect = findViewById(R.id.btn_disconnection);

        // Session Manager ///////////////////
        //Open session manager
        sessionManager = new SessionManager(this);
        //Check if the user is already logged
        //If already logged, recuperate the username and user id number
        if (sessionManager.isLogged()) {
            username = sessionManager.getUsername();
            id_user = Integer.parseInt(sessionManager.getId());
        }
        //display logged user name
        TextView tv_logged_As = findViewById(R.id.tv_logged_as);
        tv_logged_As.setText(getString(R.string.log_msg) + username);


        //Set button click listeners ////////////
        // Click listener for opening the Update password activity
        btn_update_password.setOnClickListener(v -> {
            Intent i = new Intent(getApplicationContext(), UpdatePassword.class);
            i.putExtra("username", username);
            i.putExtra("id_user", id_user);
            startActivity(i);
            finish();
        });

        // Click listener for logging the user out
        btn_disconnect.setOnClickListener(v -> {
            sessionManager.logout();
            Intent i = new Intent(getApplicationContext(), Welcome.class);
            startActivity(i);
            finish();
        });
    }

    //function for swiping left
    public boolean onTouchEvent(MotionEvent touchEvent) {
        float x1 = 0; //finger position when it starts touching the screen
        float x2 = 0; //finger position when it stops touching the screen
        switch (touchEvent.getAction()) {
            case MotionEvent.ACTION_DOWN: //when finger starts touching the screen
                x1 = touchEvent.getX();
                break;
            case MotionEvent.ACTION_UP: //when finger stops touching the screen
                x2 = touchEvent.getX();
                if (x1 < x2) { //if finger swipes from left to right, open home activity
                    Intent i = new Intent(this, Home.class);
                    startActivity(i);
                    overridePendingTransition(R.xml.activity_back_in, R.xml.activity_back_out);
                    finish();
                }
                break;
        }
        return false;
    }
}