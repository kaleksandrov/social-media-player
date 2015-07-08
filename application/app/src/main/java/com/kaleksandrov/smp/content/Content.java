package com.kaleksandrov.smp.content;

import android.util.SparseArray;

import com.kaleksandrov.smp.model.Album;
import com.kaleksandrov.smp.model.Artist;
import com.kaleksandrov.smp.model.Song;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by kaleksandrov on 4/29/15.
 */
public class Content {

    private SparseArray<Artist> mArtists;
    private SparseArray<Album> mAlbums;
    private SparseArray<Song> mSongs;

    public Content(SparseArray<Artist> artists, SparseArray<Album> albums, SparseArray<Song> songs) {
        mArtists = artists;
        mAlbums = albums;
        mSongs = songs;
    }

    public Artist getArtist(int id) {
        return mArtists.get(id);
    }

    public List<Artist> getArtists() {
        return Collections.unmodifiableList(toList(mArtists));
    }

    public Song getSong(int id) {
        return mSongs.get(id);
    }

    public List<Song> getSongs() {
        return Collections.unmodifiableList(toList(mSongs));
    }


    public Album getAlbum(int id) {
        return mAlbums.get(id);
    }

    public List<Album> getAlbums() {
        return Collections.unmodifiableList(toList(mAlbums));
    }

    private static <T> List<T> toList(SparseArray<T> original) {
        if (original == null) {
            return null;
        }

        if (original.size() == 0) {
            return new ArrayList<>();
        }

        List<T> result = new ArrayList<>(original.size());
        for (int i = 0; i < original.size(); i++) {
            result.add(original.valueAt(i));
        }

        return result;
    }
}
