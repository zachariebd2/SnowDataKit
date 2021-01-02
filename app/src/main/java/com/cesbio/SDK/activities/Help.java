/*
 * Copyright (c) Salah-Eddine ET-TALEBY, CESBIO 2020
 *               Zacharie BARROU DUMONT, CESBIO 2020
 *
 *
 *
 *
 * Activity page for displaying a tutorial slidescreen
 */

package com.cesbio.SDK.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.cesbio.SDK.MyPagerAdapter;
import com.cesbio.SDK.R;

import android.content.Intent;
import android.os.Bundle;

import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;


public class Help extends AppCompatActivity {

    private ViewPager viewPager;
    private LinearLayout layoutDot;
    private TextView[] dotstv;
    private Button btnSkip;
    private int[]layouts;
    private float x1;
    private float x2;
    private MyPagerAdapter pagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help);

        viewPager = findViewById(R.id.help);
        layoutDot = findViewById(R.id.dotLayout);
        btnSkip = findViewById(R.id.btn_skip);

        btnSkip.setOnClickListener(view -> {
            Intent i = new Intent(Help.this, Home.class);
            startActivity(i);
            finish();
        });
        layouts = new int[]{R.layout.slider_1,R.layout.slider_2,R.layout.slider_3,R.layout.slider_4,R.layout.slider_5,R.layout.slider_6,R.layout.slider_7,R.layout.slider_8};
        pagerAdapter = new MyPagerAdapter(layouts, getApplicationContext());
        viewPager.setAdapter(pagerAdapter);

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }
            @Override
            public void onPageSelected(int position) {
            }
            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
    }

}