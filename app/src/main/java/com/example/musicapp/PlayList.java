package com.example.musicapp;

import android.content.Context;
import android.widget.ArrayAdapter;

import androidx.annotation.NonNull;

public class PlayList extends ArrayAdapter<String> {
    public PlayList(@NonNull Context context, int resource) {
        super(context, resource);
    }

    public PlayList(@NonNull Context context, int resource, String[] array) {
        super(context, resource, array);
    }
}
