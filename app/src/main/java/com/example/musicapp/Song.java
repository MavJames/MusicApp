package com.example.musicapp;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

public class Song implements Parcelable {

   private int id;
    private String artist;
    private String title;

    private int picture;
    protected Song(Parcel in) {
        this.id = in.readInt();
        this.artist = in.readString();
        this.title = in.readString();
        this.picture = in.readInt();

    }

    public Song(String title, int id, int picture){
        this.title = title;
        this.id = id;
        this.picture = picture;
    }



    public int getPicture() {
        return picture;
    }

    public void setPicture(int picture) {
        this.picture = picture;
    }



    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public static final Creator<Song> CREATOR = new Creator<Song>() {
        @Override
        public Song createFromParcel(Parcel in) {
            return new Song(in);
        }

        @Override
        public Song[] newArray(int size) {
            return new Song[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(artist);
        dest.writeString(title);
        dest.writeInt(picture);
    }
}
