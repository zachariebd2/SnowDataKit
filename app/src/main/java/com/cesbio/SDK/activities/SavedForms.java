/*
 * Copyright (c) Salah-Eddine ET-TALEBY, CESBIO 2020
 */

package com.cesbio.SDK.activities;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.RequestQueue;
import com.cesbio.SDK.R;
import com.cesbio.SDK.myrequest.MyRequest;
import com.cesbio.SDK.traitements.FormAdapter;
import com.cesbio.SDK.traitements.Formulaire;
import com.cesbio.SDK.traitements.VolleySingleton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;

public class SavedForms extends AppCompatActivity {
    private ListView listView;
    private int id_user;
    private String username;
    private ArrayList<Formulaire> formList;
    private MyRequest request;
    private String FILE_NAME;
    private float x1;
    private TextView tv_loggedAs;

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_saved_forms);

        // Bundle pour stocker les "extras", c'est-à-dire les variables (int, float, String...)
        Bundle extras = getIntent().getExtras();
        // Si le bundle n'est pas null (= contient au moins une chaîne, ou un entier...)
        if (extras != null) {
            id_user = extras.getInt("id_user");
            username = extras.getString("username");
        }
        tv_loggedAs = findViewById(R.id.tv_logged_as);
        tv_loggedAs.setText(getString(R.string.log_msg) + username);
        // Nom du fichier JSON
        FILE_NAME = "formulaires_" + id_user + ".json";

        // Instanciation de la requête Volley via la classe VolleySingleton (Google)
        RequestQueue queue = VolleySingleton.getInstance(this).getRequestQueue();
        request = new MyRequest(this, queue);

        listView = findViewById(R.id.list_saved_forms);
        listView.setAdapter(new FormAdapter(displayFormList(), this));

        // Selection de tout les formulaires
        Button btn_selectAll = findViewById(R.id.btn_select_all);
        btn_selectAll.setOnClickListener(new View.OnClickListener() {
                                             @Override
                                             public void onClick(View v) {
                                                 ((FormAdapter) listView.getAdapter()).selectAll();
                                             }


                                         });

        // Deselection de tout les formulaires
        Button btn_deselectAll = findViewById(R.id.btn_deselect_all);
        btn_deselectAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((FormAdapter) listView.getAdapter()).unselectAll();
            }


        });

        // Envoi des formulaires sélectionnés
        Button btn_envoyer = findViewById(R.id.btn_send);
        btn_envoyer.setOnClickListener(v -> {
            final ArrayList<Formulaire> selectedForms = ((FormAdapter) listView.getAdapter()).getSelectFormList();
            final ArrayList<Integer> positions = ((FormAdapter) listView.getAdapter()).getPositions();

            if (formList.size() <= 0) {
                Toast.makeText(this, R.string.no_form_msg, Toast.LENGTH_SHORT).show();
            } else {
                final AlertDialog dialog = new AlertDialog.Builder(SavedForms.this)
                        .setTitle(R.string.send)
                        .setMessage(R.string.confirm_send)
                        .setPositiveButton(R.string.yes, null)
                        .setNegativeButton(R.string.no, null)
                        .show();

                Button positiveButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
                positiveButton.setOnClickListener(v1 -> {
                    if (positions.size() > 0) {
                        request.insertionFormulaireHL(selectedForms, new MyRequest.InsertionFormCallback() {
                            @Override
                            public void onSuccess(String message) {
                                Toast.makeText(SavedForms.this, message, Toast.LENGTH_SHORT).show();
                                Log.d("SUPPLIST_FORMS", "tab : " + Arrays.toString(positions.toArray()));

                                // Initialisation du JSON
                                File f = new File(getFilesDir(), FILE_NAME);

                                String formsStr = null;
                                try {
                                    formsStr = lireForm(f);
                                    JSONObject obj = new JSONObject(formsStr);
                                    Log.d("JSONINITIAL_FORMS", obj.toString());
                                    JSONArray forms = obj.getJSONArray("formulaires");
                                    Log.d("JSONARRAY_FORMS", forms.toString());
                                    int j = 0;
                                    for (int i = positions.size() - 1; i >= 0; i--) {
                                        j = positions.get(i);
                                        formList.remove(j);
                                        for (int k = 0; k < forms.length(); k++) {
                                            if (forms.getJSONObject(k).getInt("id") == selectedForms.get(i).getId_Form()) {
                                                forms.remove(k);
                                            }
                                        }
                                    }
                                    ((FormAdapter) listView.getAdapter()).notifyDataSetChanged();
                                    Log.d("JSONARRAY_FORMS", forms.toString());

                                    JSONObject jsonObject = new JSONObject();
                                    jsonObject.put("formulaires", forms);
                                    Log.d("JSONFINAL_FORMS", jsonObject.toString());
                                    stockerForm(jsonObject);

                                } catch (IOException | JSONException e) {
                                    e.printStackTrace();
                                }
                            }

                            @Override
                            public void inputErrors(Map<String, String> errors) {
                                if (errors.get("req") != null) {
                                    Toast.makeText(SavedForms.this, errors.get("req"), Toast.LENGTH_SHORT).show();
                                }
                            }

                            @Override
                            public void onError(String message) {
                                Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
                            }
                        });
                    } else {
                        Toast.makeText(this, R.string.select_form_msg, Toast.LENGTH_SHORT).show();
                    }
                    dialog.dismiss();

                });
            }
        });

        Button btn_supprimer = findViewById(R.id.btn_erase);
        suppressionForms(btn_supprimer);
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void suppressionForms(Button btn_supprimer) {
        btn_supprimer.setOnClickListener(v -> {
            final ArrayList<Integer> positions = ((FormAdapter) listView.getAdapter()).getPositions();
            final ArrayList<Formulaire> selectedForms = ((FormAdapter) listView.getAdapter()).getSelectFormList();

            if (formList.size() <= 0) {
                Toast.makeText(this, R.string.no_delete_msg, Toast.LENGTH_SHORT).show();
            } else {
                final AlertDialog dialog = new AlertDialog.Builder(SavedForms.this)
                        .setTitle(R.string.delete_title)
                        .setMessage(R.string.confirm_delete_msg)
                        .setPositiveButton(R.string.yes, null)
                        .setNegativeButton(R.string.no, null)
                        .show();

                Button positiveButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
                positiveButton.setOnClickListener(v1 -> {
                    Log.d("SUPPLIST_FORMS", "tab : " + Arrays.toString(positions.toArray()));

                    // Initialisation du JSON
                    File f = new File(getFilesDir(), FILE_NAME);

                    String formsStr = null;
                    try {
                        formsStr = lireForm(f);
                        JSONObject obj = new JSONObject(formsStr);
                        Log.d("JSONINITIAL_FORMS", obj.toString());
                        JSONArray forms = obj.getJSONArray("formulaires");
                        Log.d("JSONARRAY_FORMS", forms.toString());
                        int j = 0;
                        if (positions.size() > 0) {
                            for (int i = positions.size() - 1; i >= 0; i--) {
                                j = positions.get(i);
                                formList.remove(j);
                                for (int k = 0; k < forms.length(); k++) {
                                    if (forms.getJSONObject(k).getInt("id") == selectedForms.get(i).getId_Form()) {
                                        forms.remove(k);
                                    }
                                }
                            }
                            ((FormAdapter) listView.getAdapter()).notifyDataSetChanged();
                            Toast.makeText(SavedForms.this, positions.size() + getString(R.string.form_deleted_msg), Toast.LENGTH_SHORT).show();
                            Log.d("JSONARRAY_FORMS", forms.toString());

                        } else {
                            Toast.makeText(SavedForms.this, R.string.select_delete_msg, Toast.LENGTH_SHORT).show();
                        }

                        JSONObject jsonObject = new JSONObject();
                        jsonObject.put("formulaires", forms);
                        Log.d("JSONFINAL_FORMS", jsonObject.toString());
                        stockerForm(jsonObject);

                    } catch (IOException | JSONException e) {
                        e.printStackTrace();
                    }
                    dialog.dismiss();
                });
            }
        });
    }

    private ArrayList<Formulaire> displayFormList() {
        formList = new ArrayList<>();

        // Initialisation du JSON
        File f = new File(getFilesDir(), FILE_NAME);

        // Vérification
        if (!f.exists())
            Toast.makeText(this, R.string.no_form, Toast.LENGTH_SHORT).show();
        else {
            try {
                String formsStr = lireForm(f);
                // Fetch du JSON
                JSONObject obj = new JSONObject(formsStr);
                JSONArray formulaires = obj.getJSONArray("formulaires");
                for (int i = 0; i < formulaires.length(); i++) {
                    JSONObject form = formulaires.getJSONObject(i);
                    int id_form = form.getInt("id");
                    int pourcentageNeige = form.getInt("pourcentageNeige");
                    double latitude = form.getDouble("latitude");
                    double longitude = form.getDouble("longitude");
                    int accuracy = form.getInt("accuracy");
                    int altitude = form.getInt("altitude");
                    String date = form.getString("date");
                    formList.add(new Formulaire(id_form, date, latitude, longitude, accuracy, altitude, pourcentageNeige, id_user));
                }
            } catch (IOException | JSONException e) {
                e.printStackTrace();
            }
        }
        return formList;

    }

    // Lire le contenu du fichier JSON et retourner le résultat dans une chaîne (String)
    private String lireForm(File file) throws IOException {
        FileReader fileReader = new FileReader(file);
        BufferedReader bufferedReader = new BufferedReader(fileReader);
        StringBuilder stringBuilder = new StringBuilder();
        String line = bufferedReader.readLine();
        while (line != null) {
            stringBuilder.append(line).append("\n");
            line = bufferedReader.readLine();
        }
        bufferedReader.close();
        return stringBuilder.toString();
    }

    // Écrire le contenu dans le fichier JSON
    private void stockerForm(JSONObject jsonObject) throws IOException {
        String formStr = jsonObject.toString();
        File file = new File(getFilesDir(), FILE_NAME);
        FileWriter fileWriter = new FileWriter(file);
        BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
        bufferedWriter.write(formStr);
        bufferedWriter.close();
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