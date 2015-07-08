package com.kaleksandrov.smp.exception;

import com.kaleksandrov.smp.player.OSMediaPlayerWrapper;

/**
 * A generic exception used in the {@link OSMediaPlayerWrapper} implementations.
 * 
 * @author kaleksandrov
 */
public class MediaPlayerException extends Exception {
	private static final long serialVersionUID = 1L;

	public MediaPlayerException() {
	}

	public MediaPlayerException(final String message) {
		super(message);
	}

	public MediaPlayerException(final Throwable throwable) {
		super(throwable);
	}

	public MediaPlayerException(final String message, final Throwable throwable) {
		super(message, throwable);
	}
}
