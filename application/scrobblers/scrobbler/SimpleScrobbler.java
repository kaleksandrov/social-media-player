package org.freesoft.socialmediaplayer.scrobbler;

import org.freesoft.socialmediaplayer.application.SocialMediaPlayerApplication;
import org.freesoft.socialmediaplayer.player.Media;

import android.content.Context;
import android.content.Intent;

/**
 * Implementation of the {@link Scrobbler} interfaces. Scrobbles to last.fm using the
 * <b>SimpleLastfmScrobbler</b> app. Implements the Singleton design pattern. </br></br> For more
 * information <a href="http://code.google.com/p/a-simple-lastfm-scrobbler/wiki/Developers"> SLS
 * API</a>
 * 
 * @author kaleksandrov
 * 
 */
class SimpleScrobbler extends AbsScrobbler {

	/**
	 * Different song states.
	 * 
	 * @author kaleksandrov
	 * 
	 */
	private enum State {
		START(0), RESUME(1), PAUSE(2), COMPLETE(3);

		private int mCode;

		private State(final int code) {
			mCode = code;
		}
	}

	/**
	 * The source type of the streamed media.
	 * 
	 * @author kaleksandrov
	 * 
	 */
	private enum SourceType {
		CHOSEN_BY_THE_USER("P"), NON_PERSONALIZED_RECOMMENDATION("R"), PERSONALIZED_RECOMMENDATION(
				"E"), UNKNOWN("U");

		private String mCode;

		private SourceType(final String code) {
			mCode = code;
		}
	}

	/**
	 * Prevent outer instantiation
	 */
	public SimpleScrobbler(final Context context) {
		super(context);
	}

	private static final String INTENT_ACTION = "com.adam.aslfms.notify.playstatechanged";
	private static final String EXTRA_APP_NAME = "app-name";
	private static final String EXTRA_APP_PACKAGE = "app-package";
	private static final String EXTRA_STATE = "state";
	private static final String EXTRA_ARTIST = "artist";
	private static final String EXTRA_ALBUM = "album";
	private static final String EXTRA_TITLE = "track";
	private static final String EXTRA_DURATION = "duration";
	private static final String EXTRA_TRACK_NUMBER = "track-number";
	private static final String EXTRA_SOURCE = "source";

	@Override
	protected void trackResumed(final Media media) {
		final Intent intent = new Intent(INTENT_ACTION);
		final SocialMediaPlayerApplication app = SocialMediaPlayerApplication.getInstance();

		intent.putExtra(EXTRA_STATE, State.RESUME.mCode);
		intent.putExtra(EXTRA_APP_NAME, app.getName());
		intent.putExtra(EXTRA_APP_PACKAGE, app.getPackageName());
		intent.putExtra(EXTRA_ARTIST, media.getArtist());
		intent.putExtra(EXTRA_ALBUM, media.getAlbum());
		intent.putExtra(EXTRA_TITLE, media.getTitle());
		intent.putExtra(EXTRA_DURATION, media.getDurationInSecs());

		mContext.sendBroadcast(intent);
	}

	@Override
	protected void trackPaused(final Media media) {
		final Intent intent = new Intent(INTENT_ACTION);
		final SocialMediaPlayerApplication app = SocialMediaPlayerApplication.getInstance();

		intent.putExtra(EXTRA_STATE, State.PAUSE.mCode);
		intent.putExtra(EXTRA_APP_NAME, app.getName());
		intent.putExtra(EXTRA_APP_PACKAGE, app.getPackageName());
		intent.putExtra(EXTRA_ARTIST, media.getArtist());
		intent.putExtra(EXTRA_ALBUM, media.getAlbum());
		intent.putExtra(EXTRA_TITLE, media.getTitle());
		intent.putExtra(EXTRA_DURATION, media.getDurationInSecs());

		mContext.sendBroadcast(intent);
	}

	@Override
	protected void trackStopped(final Media media) {
		final Intent intent = new Intent(INTENT_ACTION);
		final SocialMediaPlayerApplication app = SocialMediaPlayerApplication.getInstance();

		intent.putExtra(EXTRA_STATE, State.COMPLETE.mCode);
		intent.putExtra(EXTRA_APP_NAME, app.getName());
		intent.putExtra(EXTRA_APP_PACKAGE, app.getPackageName());
		intent.putExtra(EXTRA_ARTIST, media.getArtist());
		intent.putExtra(EXTRA_ALBUM, media.getAlbum());
		intent.putExtra(EXTRA_TITLE, media.getTitle());
		intent.putExtra(EXTRA_DURATION, media.getDurationInSecs());

		mContext.sendBroadcast(intent);
	}

	@Override
	protected void trackPlayed(final Media media) {
		final Intent intent = new Intent(INTENT_ACTION);
		final SocialMediaPlayerApplication app = SocialMediaPlayerApplication.getInstance();

		intent.putExtra(EXTRA_STATE, State.START.mCode);
		intent.putExtra(EXTRA_APP_NAME, app.getName());
		intent.putExtra(EXTRA_APP_PACKAGE, app.getPackageName());
		intent.putExtra(EXTRA_ARTIST, media.getArtist());
		intent.putExtra(EXTRA_ALBUM, media.getAlbum());
		intent.putExtra(EXTRA_TITLE, media.getTitle());
		intent.putExtra(EXTRA_DURATION, media.getDurationInSecs());

		mContext.sendBroadcast(intent);
	}
}
