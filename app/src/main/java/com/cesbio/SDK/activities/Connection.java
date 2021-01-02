/*
 * Copyright (c) Salah-Eddine ET-TALEBY, CESBIO 2020
 *               Zacharie BARROU DUMONT, CESBIO 2020
 *
 *
 *
 *
 * Activity page for user connection
 */

package com.cesbio.SDK.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.RequestQueue;
import com.cesbio.SDK.R;
import com.cesbio.SDK.myrequest.MyRequest;
import com.cesbio.SDK.traitements.SessionManager;
import com.cesbio.SDK.traitements.VolleySingleton;
import com.google.android.material.textfield.TextInputLayout;

public class Connection extends AppCompatActivity {



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //get activity layouts //////////////////
        setContentView(R.layout.activity_connexion);
        TextInputLayout til_username = findViewById(R.id.til_username_log);
        TextInputLayout til_password = findViewById(R.id.til_password_log);
        Button btn_send_login = findViewById(R.id.btn_send_login);
        Button btn_forgot_password = findViewById(R.id.btn_forgot_password);
        ProgressBar pb_loader_login = findViewById(R.id.pb_loader_login);



        // Session Manager ///////////////////
        //Open session manager
        SessionManager sessionManager = new SessionManager(this);


        // Volley ///////////////////////
        RequestQueue queue = VolleySingleton.getInstance(this).getRequestQueue();
        MyRequest request = new MyRequest(this, queue);
        Handler handler = new Handler();


        //Set button click listeners ////////////
        // Click listener for sending login request to the server and receiving confirmation
        btn_send_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String username = til_username.getEditText().getText().toString().trim();
                final String password = til_password.getEditText().getText().toString().trim();
                if (username.length() > 0 && password.length() > 0) {
                    pb_loader_login.setVisibility(View.VISIBLE);
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            request.login(username, password, new MyRequest.LoginCallback() {
                                //If connection is confirmed, open session and send the user to home page
                                @Override
                                public void onSuccess(String id, String username) {
                                    pb_loader_login.setVisibility(View.GONE);
                                    sessionManager.insertUser(id, username);
                                    Intent i = new Intent(getApplicationContext(), Home.class);
                                    startActivity(i);
                                    finish();
                                }
                                //If connection failed, warn the user
                                @Override
                                public void onError(String message) {
                                    pb_loader_login.setVisibility(View.GONE);
                                    Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    }, 1000);
                }
                //If login field not correctly filled, warn user
                else {
                    Toast.makeText(Connection.this, R.string.fill_fields_msg, Toast.LENGTH_SHORT).show();
                }
            }
        });

        // Click listener for accessing ForgotPassword activity
        btn_forgot_password.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), ForgotPassword.class);
                startActivity(i);
                finish();
            }
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
                if (x1 < x2) { //if finger swipes from left to right, open welcome activity
                    Intent i = new Intent(this, Welcome.class);
                    startActivity(i);
                    overridePendingTransition(R.xml.activity_back_in, R.xml.activity_back_out);
                    finish();
                }
                break;
        }
        return false;
    }
}