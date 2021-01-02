/*
 * Copyright (c) Salah-Eddine ET-TALEBY, CESBIO 2020
 */

package com.cesbio.SDK.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.RequestQueue;
import com.cesbio.SDK.R;
import com.cesbio.SDK.myrequest.MyRequest;
import com.cesbio.SDK.traitements.VolleySingleton;
import com.google.android.material.textfield.TextInputLayout;

import java.util.Map;


public class UpdatePassword extends AppCompatActivity {

    // Variables nécessaires
    private Button btn_update;
    private TextInputLayout til_password, til_password2;
    private ProgressBar pb_loader;
    private RequestQueue queue;
    private MyRequest request;
    private int id_user;
    private String pseudo;
    private float x1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_password);

        // Instanciation des variables
        btn_update = findViewById(R.id.btn_update_password);
        til_password = findViewById(R.id.til_password_update);
        til_password2 = findViewById(R.id.til_password2_update);
        pb_loader = findViewById(R.id.pb_loader_update);

        // Bundle pour stocker les "extras", c'est-à-dire les variables (int, float, String...)
        Bundle extras = getIntent().getExtras();
        // Si le bundle n'est pas null (= contient au moins une chaîne, ou un entier...)
        if (extras != null) {
            id_user = extras.getInt("id_user");
            pseudo = extras.getString("username");
        }

        queue = VolleySingleton.getInstance(this).getRequestQueue();
        request = new MyRequest(this, queue);

        btn_update.setOnClickListener(v -> {
            String password = til_password.getEditText().getText().toString().trim();
            String password2 = til_password2.getEditText().getText().toString().trim();
            if (password.length() > 0 && password2.length() > 0) {
                pb_loader.setVisibility(View.VISIBLE);
                request.update_password(password, password2, id_user, new MyRequest.UpdateCallback() {
                    @Override
                    public void onSuccess(String message) {
                        pb_loader.setVisibility(View.GONE);
                        Toast.makeText(UpdatePassword.this, message, Toast.LENGTH_SHORT).show();
                        Intent i = new Intent(getApplicationContext(), Home.class);
                        startActivity(i);
                        finish();

                    }

                    // Gestion des erreurs retournées par Volley
                    @Override
                    public void inputErrors(Map<String, String> errors) {
                        Log.d("ERRORS", errors.toString());
                        pb_loader.setVisibility(View.GONE);
                        if (errors.get("password") != null) {
                            til_password.setError(errors.get("password"));
                        } else {
                            til_password.setErrorEnabled(false);
                        }
                    }

                    @Override
                    public void onError(String message) {
                        pb_loader.setVisibility(View.GONE);
                        Toast.makeText(UpdatePassword.this, message, Toast.LENGTH_SHORT).show();
                    }
                });
            } else {
                Toast.makeText(UpdatePassword.this, R.string.fill_fields_msg, Toast.LENGTH_SHORT).show();
                pb_loader.setVisibility(View.GONE);
            }
        });
    }

    // Méthode pour Swipe
    public boolean onTouchEvent(MotionEvent touchEvent) {
        switch (touchEvent.getAction()) {
            case MotionEvent.ACTION_DOWN:
                x1 = touchEvent.getX();
                break;
            case MotionEvent.ACTION_UP:
                float x2 = touchEvent.getX();

                // Swipe vers la gauche
                if (x1 < x2) {
                    Intent i = new Intent(this, Account.class);
                    startActivity(i);
                    overridePendingTransition(R.xml.activity_back_in, R.xml.activity_back_out);
                    finish();
                }
                break;
        }
        return false;
    }
}