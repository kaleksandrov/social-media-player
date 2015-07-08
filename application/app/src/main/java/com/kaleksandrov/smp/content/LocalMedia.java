package com.kaleksandrov.smp.content;

import android.net.Uri;

import com.kaleksandrov.smp.util.ValidationUtils;

class LocalMedia extends AbsMedia {

    private Uri mUri;

    public LocalMedia(Uri uri,
                      int id,
                      String artist,
                      int artistId,
                      String title,
                      String album,
                      int albumId,
                      String displayName,
                      int duration,
                      int trackNumber,
                      long size,
                      int year) {
        super(id, artist, artistId, title, album, albumId, displayName, duration, trackNumber, size, year);

        ValidationUtils.notNull(uri);

        this.mUri = uri;
    }

    @Override
    public String getPath() {
        return mUri.toString();
    }

    @Override
    public Uri getUri() {
        return mUri;
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof LocalMedia) {
            return this.mUri.equals(((LocalMedia) o).mUri);
        } else {
            return super.equals(o);
        }
    }
}
