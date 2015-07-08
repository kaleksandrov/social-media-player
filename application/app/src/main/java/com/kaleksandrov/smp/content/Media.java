package com.kaleksandrov.smp.content;

import android.net.Uri;

interface Media extends Comparable<Media> {

    int getId();

	String getArtist();

    int getArtistId();

	String getTitle();

	String getAlbum();

	int getAlbumId();

	int getDuration();

	int getDurationInSecs();

	String getDisplayName();

	int getTrackNumber();

	int getYear();

	String getPath();

	Uri getUri();

	boolean isLockRequired();
}
