package com.kaleksandrov.smp.notification.builder;

import android.app.Notification.Builder;
import android.app.PendingIntent;
import android.content.Context;

public class AbstractNotificationBuilder extends Builder {
	public AbstractNotificationBuilder(final Context context,
			final int smallIcon,
			final PendingIntent contentInten) {
		super(context);

		setContentIntent(contentInten);
		setSmallIcon(smallIcon);
	}
}
