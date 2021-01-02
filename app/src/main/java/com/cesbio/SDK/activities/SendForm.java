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
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.RequestQueue;
import com.cesbio.SDK.R;
import com.cesbio.SDK.myrequest.MyRequest;
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
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

public class SendForm extends AppCompatActivity {
    private static String FILE_NAME;
    private int accuracy, altitude;
    private double latitude, longitude;
    private int fsc;
    private float x1;
    private int saved_id_pourcentageNeige;
    private MyRequest request;
    private String username;
    private int id_user;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_form);

        // Bundle pour stocker les extras
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            // Stockage des extras dans de nouvelles variables
            accuracy = extras.getInt("savedAccuracy");
            altitude = extras.getInt("savedAltitude");
            latitude = extras.getDouble("savedLatitude");
            longitude = extras.getDouble("savedLongitude");
            fsc = extras.getInt("savedfsc");
            saved_id_pourcentageNeige = extras.getInt("id_input_fsc");
            username = extras.getString("username");
            id_user = extras.getInt("id_user");
            FILE_NAME = "formulaires_" + id_user + ".json";
        }

        TextView tv_loggedAs = findViewById(R.id.tv_logged_as);
        tv_loggedAs.setText(getString(R.string.log_msg)+ username);

        // Instanciation de la requête Volley via la classe VolleySingleton (Google)
        RequestQueue queue = VolleySingleton.getInstance(this).getRequestQueue();
        request = new MyRequest(this, queue);

        Button btn_retourAccueil = findViewById(R.id.btn_welcome);

        // Clic sur le bouton "Sauvegarder hors-ligne"
        findViewById(R.id.btn_save_form).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                JSONObject form;
                try {
                    File file = new File(getFilesDir(), FILE_NAME);
                    // Si le fichier n'existe pas, on en crée un, sinon on ajoute l'objet au fichier JSON existant
                    form = !file.exists() ? ajouterForm() : addFormToJson(lireForm(file));
                    stockerForm(form);
                    Toast.makeText(SendForm.this, getString(R.string.form_saved_msg), Toast.LENGTH_SHORT).show();
                    Intent i = new Intent(getApplicationContext(), Home.class);
                    i.putExtra("username", username);
                    i.putExtra("id_user", id_user);
                    startActivity(i);
                    finish();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

        // On récupère la date du jour
        String date = getDateDuJour();

        // Création du formulaire
        final Formulaire formulaire = new Formulaire(0, date, latitude, longitude, accuracy, altitude, fsc, id_user);

        // Bouton pour envoyer les données dans la BD
        final Button btn_envoyer = findViewById(R.id.btn_send_form);
        btn_envoyer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                request.insertionFormulaire(formulaire, new MyRequest.InsertionFormCallback() {
                    @Override
                    public void onSuccess(String message) {
                        Toast.makeText(SendForm.this, message, Toast.LENGTH_SHORT).show();
                        Intent i = new Intent(getApplicationContext(), Home.class);
                        i.putExtra("username", username);
                        i.putExtra("id_user", id_user);
                        startActivity(i);
                        finish();
                    }

                    @Override
                    public void inputErrors(Map<String, String> errors) {
                        if (errors.get("req") != null) {
                            Toast.makeText(SendForm.this, errors.get("req"), Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onError(String message) {
                        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        // Retour à l'accueil
        btn_retourAccueil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), Home.class);
                i.putExtra("username", username);
                i.putExtra("id_user", id_user);
                startActivity(i);
                finish();
            }
        });
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

    // Ajouter un objet JSON à un fichier existant
    private JSONObject addFormToJson(String jsonStr) throws JSONException, IOException {
        JSONObject objetPrecedent = new JSONObject(jsonStr);
        JSONArray array = objetPrecedent.getJSONArray("formulaires");
        JSONObject form = dataJson();
        array.put(form);
        JSONObject objetJsonActuel = new JSONObject();
        objetJsonActuel.put("formulaires", array);
        return objetJsonActuel;
    }

    // Insertion des données dans un objet JSON qui sera retourné
    private JSONObject dataJson() throws JSONException, IOException {
        File file = new File(getFilesDir(), FILE_NAME);
        JSONObject form = new JSONObject();
        String date = getDateDuJour();


        // Si le fichier n'existe pas, on met l'ID à 1
        if (!file.exists()) {
            form.put("id", 1);
        } else { // Sinon, si le fichier existe, on récupère son contenu (array formulaires contenant tous les formulaires)
            String formsStr = lireForm(file);
            JSONObject obj = new JSONObject(formsStr);
            JSONArray forms = obj.getJSONArray("formulaires");
            if (forms.length() <= 0) { // Si cet array est vide, alors on met l'ID à 1
                form.put("id", 1);
            } else { // Sinon, on incrémente l'ID
                form.put("id", recupererId(formsStr) + 1);
            }
        }

        // On insère les données
        form.put("id_user", id_user);
        form.put("pseudo", username);
        form.put("date", date);
        form.put("latitude", latitude);
        form.put("longitude", longitude);
        form.put("accuracy", accuracy);
        form.put("altitude", altitude);
        form.put("pourcentageNeige", fsc);

        return form;
    }

    private String getDateDuJour() {
        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        Date d = new Date();
        return dateFormat.format(d);
    }

    // Ajouter un nouvel objet JSON
    private JSONObject ajouterForm() throws JSONException, IOException {
        JSONObject formWrapper = new JSONObject();
        JSONObject form = dataJson();
        formWrapper.put("formulaires", new JSONArray()
                .put(form));
        return formWrapper;
    }

    // Récupérer l'ID du dernier objet dans le fichier "formulaires.json"
    private int recupererId(String jsonStr) throws JSONException {
        JSONObject obj = new JSONObject(jsonStr);

        // Tableau formulaires
        JSONArray forms = obj.getJSONArray("formulaires");

        // Récupérer le dernier objet
        JSONObject last_form = forms.getJSONObject(forms.length() - 1);

        return last_form.optInt("id");
    }

    // Swipe vers la gauche ou vers la droite
    public boolean onTouchEvent(MotionEvent touchEvent) {
        switch (touchEvent.getAction()) {
            case MotionEvent.ACTION_DOWN:
                x1 = touchEvent.getX();
                break;
            case MotionEvent.ACTION_UP:
                float x2 = touchEvent.getX();
                if (x1 < x2) {
                    Intent i = new Intent(this, Form.class);
                    // Données à renvoyer
                    i.putExtra("savedAccuracy", accuracy);
                    i.putExtra("savedAltitude", altitude);
                    i.putExtra("savedLongitude", longitude);
                    i.putExtra("savedLatitude", latitude);
                    i.putExtra("savedfsc", fsc);
                    i.putExtra("saved_id_fsc", saved_id_pourcentageNeige);
                    i.putExtra("id_user", id_user);
                    i.putExtra("username", username);

                    startActivity(i);
                    overridePendingTransition(R.xml.activity_back_in, R.xml.activity_back_out);
                    finish();
                }
                break;
        }
        return false;
    }
}