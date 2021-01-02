/*
 * Copyright (c) Salah-Eddine ET-TALEBY, CESBIO 2020
 *               Zacharie BARROU DUMONT, CESBIO 2020
 *
 *
 *
 *
 * Activity page for requesting a password reset
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

public class ForgotPassword extends AppCompatActivity {



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        //get activity layouts //////////////////
        TextInputLayout til_email = findViewById(R.id.til_username_log);
        Button btn_welcome = findViewById(R.id.btn_welcome);
        Button btn_resetPassword = findViewById(R.id.btn_reset_pwd);
        ProgressBar pb_loader = findViewById(R.id.pb_loader_update);

        // Volley ///////////////////////
        RequestQueue queue = VolleySingleton.getInstance(this).getRequestQueue();
        MyRequest request = new MyRequest(this, queue);


        // Set button click listeners //////////////////
        // Click listener for going back to welcome activity
        btn_welcome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), Welcome.class);
                startActivity(i);
                finish();
            }
        });

        // Click listener for sending a password reset request
        btn_resetPassword.setOnClickListener(v -> {
            String str_email = til_email.getEditText().getText().toString().trim();
            if (str_email.length() > 0 ) {
                pb_loader.setVisibility(View.VISIBLE);
                request.reset_password(str_email, new MyRequest.ResetCallback() {
                    @Override
                    public void onSuccess(String message) {
                        pb_loader.setVisibility(View.GONE);
                        til_email.setErrorEnabled(false);
                        Toast.makeText(ForgotPassword.this, message, Toast.LENGTH_SHORT).show();
                        Intent i = new Intent(getApplicationContext(), Welcome.class);
                        startActivity(i);
                        finish();
                    }
                    @Override
                    public void inputErrors(Map<String, String> errors) {
                        Log.d("ERRORS", errors.toString());
                        pb_loader.setVisibility(View.GONE);
                        if (errors.get("email") != null) {
                            til_email.setError(getString(R.string.email_error));
                        } else {
                            til_email.setErrorEnabled(false);
                        }
                    }
                    @Override
                    public void onError(String message) {
                        pb_loader.setVisibility(View.GONE);
                        Toast.makeText(ForgotPassword.this, message, Toast.LENGTH_SHORT).show();
                    }
                });
            } else {
                Toast.makeText(ForgotPassword.this, getString(R.string.fill_fields_msg), Toast.LENGTH_SHORT).show();
                pb_loader.setVisibility(View.GONE);
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
                if (x1 < x2) { //if finger swipes from left to right, open connection activity
                    Intent i = new Intent(this, Connection.class);
                    startActivity(i);
                    finish();
                }
                break;
        }
        return false;
    }
}