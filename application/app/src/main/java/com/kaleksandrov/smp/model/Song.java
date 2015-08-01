package com.kaleksandrov.smp.model;

import android.net.Uri;

/**
 * Created by kaleksandrov on 5/4/15.
 */
public class Song extends Model {

    private Album mAlbum;
    private int mDuration;
    private int mTrackNumber;
    private Uri mUri;
    private boolean mIsLockRequired;

    public Song(int id, String title, Album album, int duration, int trackNumber, Uri uri) {
        super(id, title);

        //TODO [kaleksandrov] Validate input
        mAlbum = album;
        mDuration = duration;
        mTrackNumber = trackNumber;
        mUri = uri;
    }

    public Album getAlbum() {
        return mAlbum;
    }

    public int getDuration() {
        return mDuration;
    }

    public int getTrackNumber() {
        return mTrackNumber;
    }

    public Uri getUri() {
        return mUri;
    }

    public boolean isLockRequired() {
        return mIsLockRequired;
    }

    @Override
    public int compareTo(Model another) {
        if (another instanceof Song) {
            return this.getUri().compareTo(((Song) another).getUri());
        } else {
            return super.compareTo(another);
        }
    }
}
