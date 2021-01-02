/*
 * Copyright (c) Salah-Eddine ET-TALEBY, CESBIO 2020
 */

package com.cesbio.SDK.traitements;

import androidx.annotation.NonNull;

public class Formulaire {
    private double latitude, longitude;
    private int accuracy, altitude, pourcentageNeige;
    private String date;
    private int id_user;
    private boolean selected;
    private int id_form;

    public Formulaire(int id_form, String date, double latitude, double longitude, int accuracy, int altitude, int pourcentageNeige, int id_user) {
        this.id_form = id_form;
        this.date = date;
        this.latitude = latitude;
        this.longitude = longitude;
        this.accuracy = accuracy;
        this.altitude = altitude;
        this.pourcentageNeige = pourcentageNeige;
        this.id_user = id_user;
        this.selected = false;
    }

    public String getDate() {
        return this.date;
    }

    public int getId_Form() {
        return this.id_form;
    }

    public double getLatitude() {
        return this.latitude;
    }

    public double getLongitude() {
        return this.longitude;
    }

    public int getAccurracy() {
        return this.accuracy;
    }

    public int getAltitude() {
        return this.altitude;
    }

    public int getPourcentageNeige() {
        return this.pourcentageNeige;
    }

    public int getIdUser() {
        return this.id_user;
    }

    public boolean isSelected() {
        return this.selected;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public void setAccuracy(int accuracy) {
        this.accuracy = accuracy;
    }

    public void setAltitude(int altitude) {
        this.altitude = altitude;
    }

    public void setPourcentageNeige(int pourcentageNeige) {
        this.pourcentageNeige = pourcentageNeige;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setId_user(int id_user) {
        this.id_user = id_user;
    }

    public void setId_form(int id_form) {
        this.id_form = id_form;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    @NonNull
    @Override
    public String toString() {
        return getDate() + " " + getPourcentageNeige();
    }
}

