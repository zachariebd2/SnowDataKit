/*
 * Copyright (c) Salah-Eddine ET-TALEBY, CESBIO 2020
 *               Zacharie BARROU DUMONT, CESBIO 2020
 *
 *
 * Activity home page from which the user can access the other features of the application
 */

package com.cesbio.SDK.activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.cesbio.SDK.R;
import com.cesbio.SDK.traitements.SessionManager;

public class Home extends AppCompatActivity {

    private String username;
    private int id_user;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        String extraUsername = "username";
        String extraId = "id_user";


        //get activity layouts //////////////////
        setContentView(R.layout.activity_home);
        Button btn_saved_forms = findViewById(R.id.btn_saved_forms);
        Button btn_sent_forms = findViewById(R.id.btn_sent_forms);
        Button btn_new_form = findViewById(R.id.btn_new_form);
        Button btn_help = findViewById(R.id.btn_help);
        Button btn_statistics = findViewById(R.id.btn_statistics);
        Button btn_account = findViewById(R.id.btn_account);


        // Session Manager ///////////////////
        //Open session manager
        SessionManager sessionManager = new SessionManager(this);
        //Check if the user is already logged
        //If already logged, recuperate the username and user id number
        if (sessionManager.isLogged()) {
            username = sessionManager.getUsername();
            id_user = Integer.parseInt(sessionManager.getId());
        }
        //display logged user name
        TextView tv_logged_as = findViewById(R.id.tv_logged_as);
        tv_logged_as.setText(getString(R.string.log_msg) + username);




        //Set button click listeners ////////////
        // Click listener for opening the "saved forms" activity
        btn_saved_forms.setOnClickListener(v -> {
            Intent i = new Intent(getApplicationContext(), SavedForms.class);
            i.putExtra(extraUsername, username);
            i.putExtra(extraId, id_user);
            startActivity(i);
            finish();

        });

        // Click listener for opening the "sent forms" activity
        btn_sent_forms.setOnClickListener(v -> {
            Intent i = new Intent(getApplicationContext(), SentForms.class);
            i.putExtra(extraUsername, username);
            i.putExtra(extraId, id_user);
            startActivity(i);
            finish();
        });


        // Click listener for opening the "localisation" activity
        btn_new_form.setOnClickListener(v -> {
            Intent i = new Intent(getApplicationContext(), GeoLocation.class);
            i.putExtra(extraUsername, username);
            i.putExtra(extraId, id_user);
            startActivity(i);
            finish();
        });

        // Click listener for opening the "help" activity
        btn_help.setOnClickListener(v -> {
            Intent i = new Intent(getApplicationContext(), Help.class);
            startActivity(i);
            finish();

        });



        // Click listener for opening the "statistics" activity
        btn_statistics.setOnClickListener(v -> {
            Intent i = new Intent(getApplicationContext(), Statistics.class);
            i.putExtra(extraUsername, username);
            i.putExtra(extraId, id_user);
            startActivity(i);
            finish();

        });

        // Click listener for opening the "Account" activity
        btn_account.setOnClickListener(v -> {
            Intent i = new Intent(getApplicationContext(), Account.class);
            i.putExtra(extraUsername, username);
            i.putExtra(extraId, id_user);
            startActivity(i);
            finish();

        });
    }
}