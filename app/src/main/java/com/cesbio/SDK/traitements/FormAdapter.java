/*
 * Copyright (c) Salah-Eddine ET-TALEBY, CESBIO 2020
 */

package com.cesbio.SDK.traitements;

import android.content.Context;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.RequiresApi;

import com.cesbio.SDK.R;

import java.util.ArrayList;

public class FormAdapter extends BaseAdapter {
    ArrayList<Formulaire> formList;
    Context context;
    LayoutInflater vi;

    public FormAdapter(ArrayList<Formulaire> formList, Context context) {
        this.context = context;
        this.vi = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.formList = formList;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = vi.inflate(R.layout.single_listview_item, null);

            holder.formDate = convertView.findViewById(R.id.tv_date);
            holder.latlng = convertView.findViewById(R.id.tv_latlng);
            holder.checkBox = convertView.findViewById(R.id.checkbox);
            holder.accuracy = convertView.findViewById(R.id.tv_accuracy);
            holder.pourcentageNeige = convertView.findViewById(R.id.tv_FSC);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        double latArrondie = (double) Math.round(formList.get(position).getLatitude() * 100) / 100;
        double lngArrondie = (double) Math.round(formList.get(position).getLongitude() * 100) / 100;
        holder.formDate.setText("Date : " + formList.get(position).getDate());
        holder.latlng.setText("Latitude - Longitude : " + latArrondie + " - " + lngArrondie + " | Altitude : " + formList.get(position).getAltitude() + "m");
        holder.accuracy.setText(context.getString(R.string.accuracy) + formList.get(position).getAccurracy() + "m");
        holder.pourcentageNeige.setText(context.getString(R.string.snow_frac) + formList.get(position).getPourcentageNeige() + "%");
        holder.checkBox.setChecked(formList.get(position).isSelected());

        holder.checkBox.setOnClickListener(v -> {
            boolean isSelected = ((CheckBox) v).isChecked();
            formList.get(position).setSelected(isSelected);
        });

        return convertView;

    }

    private static class ViewHolder {
        public TextView formDate;
        public TextView pourcentageNeige;
        public TextView accuracy;
        public TextView latlng;
        public CheckBox checkBox;
    }

    @Override
    public int getCount() {
        return formList.size();
    }

    public ArrayList<Integer> getPositions() {
        ArrayList<Integer> list = new ArrayList<>();
        for (int i = 0; i < formList.size(); i++) {
            if (formList.get(i).isSelected())
                list.add(i);
        }
        return list;
    }


    @Override
    public Formulaire getItem(int position) {
        return formList.get(position);
    }


    @Override
    public long getItemId(int position) {
        return position;
    }

    public ArrayList<Formulaire> getSelectFormList() {
        ArrayList<Formulaire> list = new ArrayList<>();
        for (int i = 0; i < formList.size(); i++) {
            if (formList.get(i).isSelected())
                list.add(formList.get(i));
        }
        return list;
    }

    public void selectAll() {
        for (int i = 0; i < formList.size(); i++) {
            formList.get(i).setSelected(true);
        }
        notifyDataSetChanged();
    }

    public void unselectAll() {
        for (int i = 0; i < formList.size(); i++) {
            formList.get(i).setSelected(false);
        }
        notifyDataSetChanged();
    }
}