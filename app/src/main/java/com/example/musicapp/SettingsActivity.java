package com.example.musicapp;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceManager;

public class SettingsActivity extends AppCompatActivity {

    private static final String TAG = "cptr320";


    private static final String OPT_SHUFFLE = "shuffle";

    private static boolean OPT_SHUFFLE_DEF = true;
    private static final String OPT_LOOP = "loop";

    private static boolean OPT_LOOP_DEF = true;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_preference);

        //getSupportActionBar().setTitle("Settings");
        Log.d(TAG, "Setting activity created...");
        if (findViewById(R.id.settings_container) != null) {
            if (savedInstanceState != null) {
                return;
            }
            getSupportFragmentManager().beginTransaction().add(R.id.settings_container, new SettingsFragment()).commit();
        }

    }

    public static boolean getShuffle(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context)
                .getBoolean(OPT_SHUFFLE, OPT_SHUFFLE_DEF);

    }
    public static boolean getLoop(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context)
                .getBoolean(OPT_LOOP, OPT_LOOP_DEF);

    }
}