package com.kaleksandrov.smp.notification.builder;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.graphics.Bitmap;
import android.widget.RemoteViews;

import com.kaleksandrov.smp.R;

public class PlayerNotificationBuilder extends AbstractNotificationBuilder {
	private static final int NOTIFICATION_LAYOUT_SMALL_ID = R.layout.notification_media_player_small;

	private RemoteViews contentView;

	public PlayerNotificationBuilder(final Context context,
			final int smallIcon,
			final PendingIntent contentInten) {
		super(context, smallIcon, contentInten);

		contentView = new RemoteViews(context.getPackageName(), NOTIFICATION_LAYOUT_SMALL_ID);
		setContent(contentView);
	}

	@Override
	public Notification build() {
		final Notification notification = super.build();
		notification.contentView = contentView;
		return notification;
	}

	public PlayerNotificationBuilder setToggleButton(final PendingIntent buttonIntent) {
		contentView.setOnClickPendingIntent(R.id.play, buttonIntent);
		return this;
	}

	public PlayerNotificationBuilder setToggleButtonIcon(final int resourceId) {
		contentView.setImageViewResource(R.id.play, resourceId);
		return this;
	}

	public PlayerNotificationBuilder setNextIconButton(final int resourceId) {
		contentView.setImageViewResource(R.id.next, resourceId);
		return this;
	}

	public PlayerNotificationBuilder setNextButton(final PendingIntent buttonIntent) {
		contentView.setOnClickPendingIntent(R.id.next, buttonIntent);
		return this;
	}

	public PlayerNotificationBuilder setDescription(final String description) {
		contentView.setTextViewText(R.id.artist, description);
		return this;
	}

	public PlayerNotificationBuilder setTitle(final String title) {
		contentView.setTextViewText(R.id.title, title);
		return this;
	}

	public PlayerNotificationBuilder setIcon(final int icon) {
		contentView.setImageViewResource(R.id.icon, icon);
		return this;
	}

	public PlayerNotificationBuilder setIcon(final Bitmap icon) {
		contentView.setImageViewBitmap(R.id.icon, icon);
		return this;
	}
}
