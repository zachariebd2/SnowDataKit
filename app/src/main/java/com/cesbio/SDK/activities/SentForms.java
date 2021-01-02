/*
 * Copyright (c) Salah-Eddine ET-TALEBY, CESBIO 2020
 *               Zacharie BARROU DUMONT, CESBIO 2020
 *
 *
 *
 *
 * Activity page for requesting and displaying the forms already sent by the user to the server
 */

package com.cesbio.SDK.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.cesbio.SDK.R;
import com.cesbio.SDK.traitements.FormListAdapter;
import com.cesbio.SDK.traitements.Formulaire;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class SentForms extends AppCompatActivity {
    private FormListAdapter adapter;
    public static ArrayList<Formulaire> formArrayList = new ArrayList<Formulaire>();

    String url = "http://osr-cesbio.ups-tlse.fr/sdk/retrieve.php";
    private Formulaire form;
    private int id_user;
    private String pseudo;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //get activity layouts //////////////////
        setContentView(R.layout.activity_sent_forms);
        TextView tv_logged_as = findViewById(R.id.tv_logged_as);



        ListView listView = findViewById(R.id.list_sent_forms);
        adapter = new FormListAdapter(this, R.layout.adapter_view_layout, formArrayList);
        listView.setAdapter(adapter);

        // Bundle pour stocker les "extras", c'est-à-dire les variables (int, float, String...)
        Bundle extras = getIntent().getExtras();
        // Si le bundle n'est pas null (= contient au moins une chaîne, ou un entier...)
        if (extras != null) {
            id_user = extras.getInt("id_user");
            pseudo = extras.getString("username");
        }


        String str = getString(R.string.log_msg) + pseudo;
        tv_logged_as.setText(str);

        retrieveData();
    }

    public void retrieveData() {
        StringRequest request = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        formArrayList.clear();
                        try {
                            Log.d("RESPONSE_LIST", response); // La réponse reçue par le serveur
                            JSONObject jsonObject = new JSONObject(response);
                            String success = jsonObject.getString("success");
                            JSONArray jsonArray = jsonObject.getJSONArray("data");
                            if (success.equals("1")) {
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    JSONObject object = jsonArray.getJSONObject(i);

                                    String f_id = object.getString("f_id");
                                    int pourcentageNeige = object.getInt("f_pourcentageneige");
                                    Double latitude = object.getDouble("f_latitude");
                                    Double longitude = object.getDouble("f_longitude");
                                    int accuracy = object.getInt("f_accuracy");
                                    int altitude = object.getInt("f_altitude");
                                    String date = object.getString("f_date");

                                    form = new Formulaire(0, date, latitude, longitude, accuracy, altitude, pourcentageNeige, id_user);
                                    formArrayList.add(form);
                                    adapter.notifyDataSetChanged();
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, error -> Toast.makeText(SentForms.this, error.getMessage(), Toast.LENGTH_SHORT).show()) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> map = new HashMap<>();
                map.put("id_user", id_user + ""); // $_POST["id_user"]
                return map;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(request);
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