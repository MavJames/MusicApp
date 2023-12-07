package com.example.musicapp;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

public class MusicDatabase implements Parcelable {

    private String selection;
    private HashMap<String, Song> database = new HashMap<>();

    List<String> catlist = new ArrayList<>();


    protected MusicDatabase(Parcel in) {
        selection = in.readString();
        Object obj = in.readHashMap(getClass().getClassLoader());
        if (obj instanceof HashMap)
            database = (HashMap<String, Song>) obj;
        catlist = in.readArrayList(getClass().getClassLoader());
    }

    public MusicDatabase(Context context) {
        Song sudoku = new Song(context.getString(R.string.title1), R.raw.game, R.drawable.sudoku);
        Song ukulele = new Song(context.getString(R.string.title2), R.raw.ukulele, R.drawable.ukulele);
        Song piano = new Song(context.getString(R.string.title3), R.raw.piano, R.drawable.piano);
        Song drum = new Song(context.getString(R.string.title4), R.raw.drum, R.drawable.drumset);
        Song trumpet = new Song(context.getString(R.string.title5), R.raw.trumpet, R.drawable.trumpet);


        database.put(sudoku.getTitle(), sudoku);
        database.put(ukulele.getTitle(), ukulele);
        database.put(piano.getTitle(), piano);
        database.put(drum.getTitle(),drum);
        database.put(trumpet.getTitle(), trumpet);

        catlist.add(sudoku.getTitle());
        catlist.add(ukulele.getTitle());
        catlist.add(piano.getTitle());
        catlist.add(drum.getTitle());
        catlist.add(trumpet.getTitle());
    }

    public Song getSelection() {
        return database.get(selection);
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeString(selection);
        dest.writeMap(database);
        dest.writeList(catlist);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<MusicDatabase> CREATOR = new Creator<MusicDatabase>() {
        @Override
        public MusicDatabase createFromParcel(Parcel in) {
            return new MusicDatabase(in);
        }

        @Override
        public MusicDatabase[] newArray(int size) {
            return new MusicDatabase[size];
        }
    };

    public String[] getSongs() {
        return database.keySet().toArray(new String[0]);
    }

    public void setSelection(String selection) {
        this.selection = selection;
    }

    public int getCurrentSongIndex(String title) {
        String[] titles = getTitles();
        for (int index = 0; index < titles.length; index++) {
            if (title.equalsIgnoreCase(titles[index]))
                return index;
        }
        return -1;
    }



    public String[] getTitles() {
        return catlist.toArray(new String[0]);
    }



}

