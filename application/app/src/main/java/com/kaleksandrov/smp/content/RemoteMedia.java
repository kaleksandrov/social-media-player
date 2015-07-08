package com.kaleksandrov.smp.content;

import android.net.Uri;

import com.kaleksandrov.smp.util.ValidationUtils;

class RemoteMedia extends AbsMedia {

    private String mUri;

    public RemoteMedia(final String uri,
                       final int id,
                       final String artist,
                       final int artistId,
                       final String title,
                       final String album,
                       final int albumId,
                       final String displayName,
                       final int duration,
                       final int trackNumber,
                       final long size,
                       final int year) {
        super(id, artist, artistId, title, album, albumId, displayName, duration, trackNumber, size, year);

        ValidationUtils.notEmpty(uri);

        this.mUri = uri;
    }

    @Override
    public String getPath() {
        return mUri;
    }

    @Override
    public Uri getUri() {
        return Uri.parse(mUri);
    }

    @Override
    public boolean isLockRequired() {
        return true;
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof RemoteMedia) {
            return this.mUri.equals(((RemoteMedia) o).mUri);
        } else {
            return super.equals(o);
        }
    }
}
