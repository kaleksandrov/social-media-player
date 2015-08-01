package com.kaleksandrov.smp.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by kaleksandrov on 4/25/15.
 */
public class Artist extends Model {

    private List<Song> mSongs;

    private int mDuration;

    public Artist(int id, String title) {
        super(id, title);
        mSongs = new ArrayList<>();
    }

    public void addSong(Song song) {
        mSongs.add(song);
        mDuration += song.getDuration();
    }

    public List<Song> getSongs() {
        return Collections.unmodifiableList(mSongs);
    }

    public int getDuration() {
        return mDuration;
    }

}
