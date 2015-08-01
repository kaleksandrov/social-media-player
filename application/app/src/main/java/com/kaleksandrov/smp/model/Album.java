package com.kaleksandrov.smp.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by kaleksandrov on 4/25/15.
 */
public class Album extends Model {

    private Artist mArtist;
    private List<Song> mSongs;
    private int mYear;
    private int mDuration;
    private String mCoverPath;

    public Album(int id, String title, Artist artist, int year, String coverPath) {
        super(id, title);
        mSongs = new ArrayList<>();

        //TODO [kaleksandrov] Validate input
        mArtist = artist;
        mYear = year;
        mCoverPath = coverPath;
    }

    public void addSong(Song song) {
        mSongs.add(song);
        mDuration += song.getDuration();
    }

    public List<Song> getSongs() {
        return Collections.unmodifiableList(mSongs);
    }

    public Artist getArtist() {
        return mArtist;
    }

    public int getYear() {
        return mYear;
    }

    public int getDuration() {
        return mDuration;
    }

    public String getCoverPath() {
        return mCoverPath;
    }

}
