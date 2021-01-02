/*
 * Copyright (c) Salah-Eddine ET-TALEBY, CESBIO 2020
 *               Zacharie BARROU DUMONT, CESBIO 2020
 *
 *
 *
 *
 * starting activity page when the application open
 */

package com.cesbio.SDK.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.cesbio.SDK.R;
import com.cesbio.SDK.traitements.SessionManager;

public class Welcome extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //get activity layouts //////////////////
        setContentView(R.layout.activity_welcome);
        Button btn_login = findViewById(R.id.btn_login);
        Button btn_register = findViewById(R.id.btn_register);

        //Open session manager /////////////////////
        SessionManager sessionManager = new SessionManager(this);
        //If user is already connected, he is sent to home page
        if (sessionManager.isLogged()) {
            Intent i = new Intent(getApplicationContext(), Home.class);
            startActivity(i);
            finish();
        }

        //Set button click listeners ////////////////////////
        // Click listener for opening connection page
        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), Connection.class);
                startActivity(i);
                overridePendingTransition(R.xml.activity_in, R.xml.activity_out);
                finish();
            }
        });

        // Click listener for opening registration page
        btn_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), Registration.class);
                startActivity(i);
                overridePendingTransition(R.xml.activity_in, R.xml.activity_out);
                finish();
            }
        });
    }
}