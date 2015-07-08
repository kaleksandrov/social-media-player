package com.kaleksandrov.smp.receiver;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.KeyEvent;

import com.kaleksandrov.smp.service.PlayerService;

public class RemoteControlReceiver extends MediaPlayerReceiver {
	@Override
	public void onReceive(Context context, Intent intent) {
		if (Intent.ACTION_MEDIA_BUTTON.equals(intent.getAction())) {
			final KeyEvent event = (KeyEvent) intent.getParcelableExtra(Intent.EXTRA_KEY_EVENT);
			if (KeyEvent.KEYCODE_MEDIA_PLAY == event.getKeyCode()) {
				PlayerService player = getPlayer(context);

				int keyCode = event.getKeyCode();

				switch (keyCode) {
				case KeyEvent.KEYCODE_MEDIA_PLAY: {
					player.unpause();
					break;
				}

				case KeyEvent.KEYCODE_MEDIA_PAUSE: {
					player.pause();
					break;
				}

				case KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE: {
					player.toggle();
					break;
				}

				case KeyEvent.KEYCODE_MEDIA_FAST_FORWARD: {
					player.fastForward();
					break;
				}

				case KeyEvent.KEYCODE_MEDIA_NEXT: {
					player.next();
					break;
				}

				case KeyEvent.KEYCODE_MEDIA_PREVIOUS: {
					player.previous();
					break;
				}

				case KeyEvent.KEYCODE_MEDIA_REWIND: {
					player.rewind();
					break;
				}

				case KeyEvent.KEYCODE_MEDIA_STOP: {
					player.stop();
					break;
				}

				default: {
					Log.w("TTT", "Unknown command '" + keyCode + "'.");
					break;
				}
				}
			}
		}
	}
}
