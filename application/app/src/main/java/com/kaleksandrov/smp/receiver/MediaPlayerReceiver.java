package com.kaleksandrov.smp.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.kaleksandrov.smp.service.PlayerService;
import com.kaleksandrov.smp.service.PlayerServiceImpl;
import com.kaleksandrov.smp.service.PlayerServiceImpl.MediaPlayerServiceBinder;

public abstract class MediaPlayerReceiver extends BroadcastReceiver {

    protected PlayerService getPlayer(Context context) {
        Intent playerIntent = new Intent(context, PlayerServiceImpl.class);
        MediaPlayerServiceBinder serviceBinder = (MediaPlayerServiceBinder) peekService(context,
                playerIntent);
        if (serviceBinder != null) {
            return serviceBinder.getService();
        } else {
            return null;
        }
    }
}
