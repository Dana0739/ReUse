package com.example.playground1;

import android.content.Intent;
import android.os.Bundle;

import com.example.playground1.utils.PreferencesUtils;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;

import androidx.viewpager.widget.ViewPager;
import android.view.View.OnClickListener;
import androidx.appcompat.app.AppCompatActivity;

import android.view.View;

import com.example.playground1.ui.main.SectionsPagerAdapter;

public class MainActivity extends AppCompatActivity implements OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        SectionsPagerAdapter sectionsPagerAdapter = new SectionsPagerAdapter(this, getSupportFragmentManager());
        ViewPager viewPager = findViewById(R.id.view_pager);
        viewPager.setAdapter(sectionsPagerAdapter);
        TabLayout tabs = findViewById(R.id.tabs);
        tabs.setupWithViewPager(viewPager);
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.fab) {
            if (isLoggedIn()) {
                Intent intent = new Intent(this, CreateAdvert.class);
                startActivity(intent);
            } else {
                Snackbar.make(view, "You should log in fist!", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        } else {
            Snackbar.make(view, "Unknown button clicked!", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();
        }
    }

    private boolean isLoggedIn() {
        return !PreferencesUtils.loadId(this).isEmpty();
    }
}