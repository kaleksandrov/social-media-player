package com.kaleksandrov.smp.content;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.SparseArray;

import com.kaleksandrov.smp.model.Album;
import com.kaleksandrov.smp.model.Artist;
import com.kaleksandrov.smp.model.Song;
import com.kaleksandrov.smp.util.ValidationUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Loads all the data about song, albums and artists and transforms it to a proper objects
 */

public class ContentLoader {

    /* Constants */

    private static final String[] MEDIA_COLUMNS = {
            MediaStore.Audio.Media._ID,
            MediaStore.Audio.Media.ARTIST,
            MediaStore.Audio.Media.ARTIST_ID,
            MediaStore.Audio.Media.ALBUM,
            MediaStore.Audio.Media.TITLE,
            MediaStore.Audio.Media.DATA,
            MediaStore.Audio.Media.DURATION,
            MediaStore.Audio.Media.ALBUM_ID,
            MediaStore.Audio.Media.TRACK,
            MediaStore.Audio.Media.YEAR
    };

    private static final String[] ALBUM_COLUMNS = {
            MediaStore.Audio.Albums._ID,
            MediaStore.Audio.Albums.ALBUM_ART
    };

    private static final Uri MEDIA_URI = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
    private static final Uri ALBUM_URI = MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI;

    private static final String WHERE_IS_MUSIC = MediaStore.Audio.Media.IS_MUSIC + "=1";

    /* Members */

    private Context mContext;

    /* Constructor */

    public ContentLoader(Context context) {
        ValidationUtils.notNull(context);

        mContext = context.getApplicationContext();
    }

    /* Public methods */

    public Content loadContent() {
        SparseArray<String> covers = loadAlbums();

        Cursor cursor = mContext.getContentResolver().query(
                MEDIA_URI,
                MEDIA_COLUMNS,
                WHERE_IS_MUSIC,
                null,
                null);

        SparseArray<Artist> artists = new SparseArray<>();
        SparseArray<Album> albums = new SparseArray<>();
        SparseArray<Song> songs = new SparseArray<>();

        // Load all media entries
        if (cursor != null) {
            while (cursor.moveToNext()) {
                int songId = cursor.getInt(cursor.getColumnIndex(MediaStore.Audio.Media._ID));
                String artistName = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST));
                int artistId = cursor.getInt(cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST_ID));
                String albumName = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM));
                int albumId = cursor.getInt(cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM_ID));
                String songTitle = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.TITLE));
                String filePath = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATA));
                int duration = cursor.getInt(cursor.getColumnIndex(MediaStore.Audio.Media.DURATION));
                int trackNumber = cursor.getInt(cursor.getColumnIndex(MediaStore.Audio.Media.TRACK));
                int year = cursor.getInt(cursor.getColumnIndex(MediaStore.Audio.Media.YEAR));
                Uri uri = Uri.parse(filePath);

                Artist artist = artists.get(artistId);
                if (artist == null) {
                    artist = new Artist(artistId, artistName);
                    artists.put(artistId, artist);
                }

                Album album = albums.get(albumId);
                if (album == null) {
                    album = new Album(albumId, albumName, artist, year, covers.get(albumId));
                    albums.put(albumId, album);
                }

                Song song = new Song(songId, songTitle, album, duration, trackNumber, uri);
                songs.put(songId, song);

                artist.addSong(song);
                album.addSong(song);
            }
            cursor.close();
        }

        return new Content(artists, albums, songs);
    }

    private SparseArray<String> loadAlbums() {
        SparseArray<String> covers = new SparseArray<>();

        Cursor cursor = mContext.getContentResolver().query(
                ALBUM_URI,
                ALBUM_COLUMNS,
                null,
                null,
                null);

        if (cursor != null) {
            while (cursor.moveToNext()) {
                int albumId = cursor.getInt(cursor.getColumnIndex(MediaStore.Audio.Albums._ID));
                String coverPath = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Albums.ALBUM_ART));

                covers.put(albumId, coverPath);
            }
            cursor.close();
        }

        return covers;
    }
}
