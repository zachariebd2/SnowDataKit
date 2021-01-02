/*
 * Copyright (c) Salah-Eddine ET-TALEBY, CESBIO 2020
 *               Zacharie BARROU DUMONT, CESBIO 2020
 *
 *
 *
 *
 * Activity page for user registration
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

public class Registration extends AppCompatActivity {

    private TextInputLayout til_username, til_email, til_password, til_password2;
    private ProgressBar pb_loader;
    private MyRequest request;
    private float x1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);


        Button btn_send_register = findViewById(R.id.btn_send_register);
        til_username = findViewById(R.id.til_username_register);
        til_email = findViewById(R.id.til_email_register);
        til_password = findViewById(R.id.til_password_register);
        til_password2 = findViewById(R.id.til_password2_register);
        pb_loader = findViewById(R.id.pb_loader_register);

        RequestQueue queue = VolleySingleton.getInstance(this).getRequestQueue();
        request = new MyRequest(this, queue);

        btn_send_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String pseudo = til_username.getEditText().getText().toString().trim();
                String email = til_email.getEditText().getText().toString().trim();
                String password = til_password.getEditText().getText().toString().trim();
                String password2 = til_password2.getEditText().getText().toString().trim();
                if (pseudo.length() > 0 && email.length() > 0 && password.length() > 0 && password2.length() > 0) {
                    pb_loader.setVisibility(View.VISIBLE);
                    request.register(pseudo, email, password, password2, new MyRequest.RegisterCallback() {
                        @Override
                        public void onSuccess(String message) {
                            pb_loader.setVisibility(View.GONE);
                            Intent i = new Intent(getApplicationContext(), Welcome.class);
                            i.putExtra("REGISTER", message);
                            startActivity(i);
                            finish();
                        }


                        @Override
                        public void inputErrors(Map<String, String> errors) {
                            Log.d("ERRORS", errors.toString());
                            pb_loader.setVisibility(View.GONE);
                            if (errors.get("username") != null) {
                                til_username.setError(errors.get("pseudo"));
                            } else {
                                til_username.setErrorEnabled(false);
                            }
                            if (errors.get("email") != null) {
                                til_email.setError(errors.get("email"));
                            } else {
                                til_email.setErrorEnabled(false);
                            }
                            if (errors.get("password") != null) {
                                til_password.setError(errors.get("password"));
                            } else {
                                til_password.setErrorEnabled(false);
                            }
                            if (errors.get("password2") != null) {
                                til_password2.setError(errors.get("password2"));
                            } else {
                                til_password2.setErrorEnabled(false);
                            }
                        }

                        @Override
                        public void onError(String message) {
                            pb_loader.setVisibility(View.GONE);
                            Toast.makeText(Registration.this, message, Toast.LENGTH_SHORT).show();
                        }
                    });
                } else {
                    Toast.makeText(Registration.this, R.string.fill_fields_msg, Toast.LENGTH_SHORT).show();
                    pb_loader.setVisibility(View.GONE);
                }
            }
        });
    }



    public boolean onTouchEvent(MotionEvent touchEvent) {
        switch (touchEvent.getAction()) {
            case MotionEvent.ACTION_DOWN:
                x1 = touchEvent.getX();
                break;
            case MotionEvent.ACTION_UP:
                float x2 = touchEvent.getX();

                // swipe to the left
                if (x1 < x2) {
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