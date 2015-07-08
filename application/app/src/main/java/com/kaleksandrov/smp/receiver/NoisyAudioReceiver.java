package com.kaleksandrov.smp.receiver;

import android.content.Context;
import android.content.Intent;

import com.kaleksandrov.smp.service.PlayerService;

public class NoisyAudioReceiver extends MediaPlayerReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(android.media.AudioManager.ACTION_AUDIO_BECOMING_NOISY)) {
            PlayerService player = getPlayer(context);
            if (player != null) {
                player.pause();
            }
        }
    }
}
