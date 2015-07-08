package org.freesoft.socialmediaplayer.scrobbler;

import org.freesoft.socialmediaplayer.player.Media;

import android.content.Context;
import android.content.Intent;

/**
 * {@link Scrobbler} implementation that uses the ScrobbleDroid API to scrobble to last.fm
 * scrobbler.</br></br> Implements the <b>Singleton</b> design pattern.
 * 
 * For more information <a
 * href="http://code.google.com/p/scrobbledroid/wiki/DeveloperAPI">ScrobbleDroid API</a>
 * 
 * @author kaleksandrov
 * 
 */
class ScrobbleDroid extends AbsScrobbler {

	/**
	 * Prevent outer instantiation
	 */
	public ScrobbleDroid(final Context context) {
		super(context);
	}

	private static final String INTENT_ACTION = "net.jjc1138.android.scrobbler.action.MUSIC_STATUS";
	private static final String EXTRA_PLAYING = "playing";
	private static final String EXTRA_ID = "id";
	private static final String EXTRA_SOURCE = "source";
	private static final String EXTRA_ARTIST = "artist";
	private static final String EXTRA_TRACK = "track";
	private static final String EXTRA_DURATION = "secs";
	private static final String EXTRA_ALBUM = "album";
	private static final String EXTRA_TRACK_NUMBER = "tracknumber";

	@Override
	protected void trackPlayed(final Media media) {
		final Intent intent = new Intent(INTENT_ACTION);

		intent.putExtra(EXTRA_PLAYING, true);
		intent.putExtra(EXTRA_ARTIST, media.getArtist());
		intent.putExtra(EXTRA_ALBUM, media.getAlbum());
		intent.putExtra(EXTRA_DURATION, media.getDuration());
		intent.putExtra(EXTRA_TRACK, media.getTitle());

		mContext.sendBroadcast(intent);
	}

	@Override
	protected void trackStopped(final Media media) {
		final Intent intent = new Intent(INTENT_ACTION);

		intent.putExtra(EXTRA_PLAYING, false);

		mContext.sendBroadcast(intent);
	}

	@Override
	protected void trackPaused(Media media) {
		final Intent intent = new Intent(INTENT_ACTION);

		intent.putExtra(EXTRA_PLAYING, false);

		mContext.sendBroadcast(intent);
	}

	@Override
	protected void trackResumed(final Media media) {
		final Intent intent = new Intent(INTENT_ACTION);

		intent.putExtra(EXTRA_PLAYING, true);
		intent.putExtra(EXTRA_ARTIST, media.getArtist());
		intent.putExtra(EXTRA_ALBUM, media.getAlbum());
		intent.putExtra(EXTRA_DURATION, media.getDuration());
		intent.putExtra(EXTRA_TRACK, media.getTitle());

		mContext.sendBroadcast(intent);
	}
}
