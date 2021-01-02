/*
 * Copyright (c) Salah-Eddine ET-TALEBY, CESBIO 2020
 *               Zacharie BARROU DUMONT, CESBIO 2020
 *
 *
 *
 *
 * Activity page for filling the FSC form
 */

package com.cesbio.SDK.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.cesbio.SDK.R;

public class Form extends AppCompatActivity {

    private RadioGroup radioGroup;
    private float x1;
    private int restoredAccuracy, restoredAltitude, FSC;
    private double restoredLatitude, restoredLongitude;
    private String username;
    private int id_user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fsc);

        radioGroup = findViewById(R.id.radioForm);


        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            // Stockage des extras dans de nouvelles variables
            restoredAccuracy = extras.getInt("savedAccuracy");
            restoredAltitude = extras.getInt("savedAltitude");
            restoredLongitude = extras.getDouble("savedLongitude");
            restoredLatitude = extras.getDouble("savedLatitude");
            int restoredIdPourcentageNeige = extras.getInt("saved_id_fsc");
            username = extras.getString("username");
            id_user = extras.getInt("id_user");
            // Restauration de l'input radio choisi

            if (restoredIdPourcentageNeige == 0) {
                restoredIdPourcentageNeige = (R.id.radio0);
            }
            radioGroup.check(restoredIdPourcentageNeige);
            RadioButton radioButton = findViewById(restoredIdPourcentageNeige);
            String fscSplit = radioButton.getText().toString().substring(0, radioButton.getText().toString().length() - 1);
            FSC = Integer.parseInt(fscSplit);


        }

        TextView tv_loggedAs = findViewById(R.id.tv_logged_as);
        tv_loggedAs.setText(getString(R.string.log_msg) + username);
    }


    public void check(View v) {
        int radioId = radioGroup.getCheckedRadioButtonId();
        RadioButton radioButton = findViewById(radioId);
        String fscSplit = radioButton.getText().toString().substring(0, radioButton.getText().toString().length() - 1);
        FSC = Integer.parseInt(fscSplit);
    }


    public boolean onTouchEvent(MotionEvent touchEvent) {
        switch (touchEvent.getAction()) {
            case MotionEvent.ACTION_DOWN: // Lorsque l'utilisateur clique sur l'écran sur l'écran
                x1 = touchEvent.getX();
                break;
            case MotionEvent.ACTION_UP: // Lorsque l'utilisateur retire son doigt de l'écran
                float x2 = touchEvent.getX();
                if (x1 < x2) {
                    Intent i = new Intent(this, GeoLocation.class);
                    // Données de la localisation
                    i.putExtra("re_savedAccuracy", restoredAccuracy);
                    i.putExtra("re_savedAltitude", restoredAltitude);
                    i.putExtra("re_savedLongitude", restoredLongitude);
                    i.putExtra("re_savedLatitude", restoredLatitude);
                    i.putExtra("id_input_fsc", radioGroup.getCheckedRadioButtonId());
                    i.putExtra("id_user", id_user);
                    i.putExtra("username", username);
                    startActivity(i); // On lance l'activité Localisation

                    overridePendingTransition(R.xml.activity_back_in, R.xml.activity_back_out);
                    finish();


                } else if (x1 > x2) {
                    Intent i = new Intent(this, SendForm.class);
                    // Données de la localisation
                    i.putExtra("savedAccuracy", restoredAccuracy);
                    i.putExtra("savedAltitude", restoredAltitude);
                    i.putExtra("savedLongitude", restoredLongitude);
                    i.putExtra("savedLatitude", restoredLatitude);
                    i.putExtra("savedfsc", FSC);
                    i.putExtra("id_input_fsc", radioGroup.getCheckedRadioButtonId());
                    i.putExtra("id_user", id_user);
                    i.putExtra("username", username);
                    startActivity(i);

                    overridePendingTransition(R.xml.activity_in, R.xml.activity_out);
                    finish();
                }
                break;
        }
        return false;
    }
}