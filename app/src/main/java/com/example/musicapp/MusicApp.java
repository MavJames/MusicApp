package com.example.musicapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

public class MusicApp extends AppCompatActivity {

    private final String TAG = "CPTR320";

    public static final String EXTRA_MESSAGE = "STRING_EXTRA";
    public static final String EXTRA_SHUFFLE_MODE = "SHUFFLE_MODE";
    public static final String EXTRA_LOOPING_MODE = "EXTRA_LOOPING";

    private View currentSelection = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        MusicDatabase dbase = new MusicDatabase(this);
        ListView listView = findViewById(R.id.play_list);
        String[] array = dbase.getTitles();
        //String[] array = {"Sudoku song"};
        PlayList playList = new PlayList(this, android.R.layout.simple_list_item_1, array);
        listView.setAdapter(playList);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                view.animate().setDuration(2000).alpha(0).withEndAction(new Runnable() {
                    @Override
                    public void run() {
                        String content = (String) parent.getItemAtPosition(position);
                        currentSelection = view;
                        dbase.setSelection(content);
                        Log.d(TAG, "Index clicked is " + position + " set to " + content);
                        Intent intent = new Intent(getApplicationContext(), MusicPlayer.class);
                        intent.putExtra(EXTRA_MESSAGE, dbase);
                        intent.putExtra(EXTRA_SHUFFLE_MODE, SettingsActivity.getShuffle(MusicApp.this));
                        intent.putExtra(EXTRA_LOOPING_MODE, SettingsActivity.getLoop(MusicApp.this));
                        //intent.putExtra()
                        startActivity(intent);

                    }
                });
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.settings) {
            Intent intent = new Intent(this, SettingsActivity.class);
            Log.d(TAG, "settings selected");
            startActivity(intent);
            return true;
        }
        if (id == R.id.about) {
            Intent intent = new Intent(this, AboutActivity.class);
            Log.d(TAG, "about selected");
            startActivity(intent);
            return true;

        }
        return true;
    }


    @Override
    protected void onResume() {
        super.onResume();
        if (currentSelection != null) {
            currentSelection.setAlpha(1.0f);
        }
    }
}