package com.example.harsh.quickshare.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.example.harsh.quickshare.fragment.SettingsFragment;

public class SettingsActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Display the fragment as main content
        getFragmentManager().beginTransaction()
                .replace(android.R.id.content, new SettingsFragment())
                .commit();
    }
}
