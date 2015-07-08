package org.freesoft.socialmediaplayer.scrobbler;

import org.freesoft.socialmediaplayer.player.Media;

import android.content.Context;

/**
 * Basic implementation of the {@link Scrobbler} interfaces.
 * 
 * @author kaleksandrov
 * 
 */
abstract class AbsScrobbler implements Scrobbler {

	private boolean mIsEnabled = true;

	protected final Context mContext;

	public AbsScrobbler(Context context) {
		mContext = context.getApplicationContext();
	}

	@Override
	public final void enable(boolean isEnabled) {
		mIsEnabled = isEnabled;
	}

	@Override
	public final boolean isEnabled() {
		return mIsEnabled;
	}

	@Override
	public final void played(final Media media) {
		if (mIsEnabled) {
			trackPlayed(media);
		}
	}

	@Override
	public final void stopped(final Media media) {
		if (mIsEnabled) {
			trackStopped(media);
		}
	}

	@Override
	public final void paused(final Media media) {
		if (mIsEnabled) {
			trackPaused(media);
		}
	}

	@Override
	public final void resumed(final Media media) {
		if (mIsEnabled) {
			trackResumed(media);
		}
	}

	/* Protected methods */

	protected abstract void trackPlayed(final Media media);

	protected abstract void trackStopped(final Media media);

	protected abstract void trackPaused(final Media media);

	protected abstract void trackResumed(final Media media);

}
