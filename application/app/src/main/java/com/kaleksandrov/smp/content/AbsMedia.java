package com.kaleksandrov.smp.content;

abstract class AbsMedia implements Media {

    protected int mId;
    protected String mArtist;
    protected int mArtistId;
    protected String mTitle;
    protected String mAlbum;
    protected int mAlbumId;
    protected String mDisplayName;
    protected int mDuration;
    protected int mTrackNumber;
    protected long mSize;
    protected int mYear;

    public AbsMedia(final int id,
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
        // TODO Validate
        mId = id;
        mArtist = artist;
        mArtistId = artistId;
        mAlbum = album;
        mTitle = title;
        mAlbumId = albumId;
        mDisplayName = displayName;
        mDuration = duration;
        mTrackNumber = trackNumber;
        mSize = size;
        mYear = year;
    }

    @Override
    public int compareTo(Media that) {
        if (this == that) {
            return 0;
        }

        return this.getPath().compareTo(that.getPath());
    }

	/* Getters */

    @Override
    public int getId() {
        return mId;
    }

    @Override
    public String getArtist() {
        return mArtist;
    }

    public int getArtistId() {
        return mArtistId;
    }

    @Override
    public String getTitle() {
        return mTitle;
    }

    @Override
    public String getAlbum() {
        return mAlbum;
    }

    public int getAlbumId() {
        return mAlbumId;
    }

    @Override
    public int getDuration() {
        return mDuration;
    }

    @Override
    public int getDurationInSecs() {
        return mDuration / 1000;
    }

    @Override
    public String getDisplayName() {
        return mDisplayName;
    }

    @Override
    public int getTrackNumber() {
        return mTrackNumber;
    }

    @Override
    public int getYear() {
        return mYear;
    }

    @Override
    public boolean isLockRequired() {
        return false;
    }
}
