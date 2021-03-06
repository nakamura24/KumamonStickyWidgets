﻿/*
 * Copyright (C) 2012 M.Nakamura
 *
 * This software is licensed under a Creative Commons
 * Attribution-NonCommercial-ShareAlike 2.1 Japan License.
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 		http://creativecommons.org/licenses/by-nc-sa/2.1/jp/legalcode
 */
package jp.widgets.kumamon.sticky.ics;

import jp.widgets.kumamon.lib.*;
import jp.widgets.kumamon.sticky.R;
import static jp.widgets.kumamon.sticky.StickyWidgetConstant.*;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.RemoteViews;

public class KumamonStickyWidget extends WidgetBase {
	private static final String TAG = "KumamonStickyWidget";

	@Override
	public void onUpdate(Context context, AppWidgetManager appWidgetManager,
			int[] appWidgetIds) {
		super.onUpdate(context, appWidgetManager, appWidgetIds);
		Log.i(TAG, "onUpdate");
		try {
			context = context.getApplicationContext();
			for (int i = 0; i < appWidgetIds.length; i++) {
				updateWidget(context, appWidgetIds[i]);
				Log.i(TAG, "mAppWidgetId=" + String.valueOf(appWidgetIds[i]));
			}
		} catch (Exception e) {
			ExceptionLog.Log(TAG, e);
		}
	}

	@Override
	public void onReceive(Context context, Intent intent) {
		super.onReceive(context, intent);
		Log.i(TAG, "onReceive - " + intent.getAction());
		try {
			context = context.getApplicationContext();
			if (CONFIG_DONE.equals(intent.getAction())) {
				Bundle extras = intent.getExtras();
				if (extras != null) {
					int appWidgetId = extras.getInt(
							AppWidgetManager.EXTRA_APPWIDGET_ID,
							AppWidgetManager.INVALID_APPWIDGET_ID);
					Log.d(TAG, "appWidgetId=" + String.valueOf(appWidgetId));
					if (appWidgetId != AppWidgetManager.INVALID_APPWIDGET_ID) {
						String comment = extras.getString(COMMENT);
						int icon = extras.getInt(ICON, 0);
						int back = extras.getInt(BACK, 0);

						StaticHash hash = new StaticHash(context);
						hash.put(COMMENT, String.valueOf(appWidgetId), comment);
						hash.put(ICON, String.valueOf(appWidgetId), icon);
						hash.put(BACK, String.valueOf(appWidgetId), back);

						updateWidget(context, appWidgetId);
					}
				}
			}
		} catch (Exception e) {
			ExceptionLog.Log(TAG, e);
		}
	}

	@Override
	public void onDeleted(Context context, int[] appWidgetIds) {
		super.onDeleted(context, appWidgetIds);
		Log.i(TAG, "onDeleted");
	}

	@Override
	public void onDisabled(Context context) {
		super.onDisabled(context);
		Log.i(TAG, "onDisabled");
		try {
			StaticHash hash = new StaticHash(context);
			hash.remove(COMMENT);
			hash.remove(ICON);
			hash.remove(BACK);
		} catch (Exception e) {
			ExceptionLog.Log(TAG, e);
		}
	}

	private void updateWidget(Context context, int appWidgetId) {
		int[] icons = { R.drawable.sticky_icon0, R.drawable.sticky_icon1,
				R.drawable.sticky_icon2, R.drawable.sticky_icon3 };
		context = context.getApplicationContext();
		StaticHash hash = new StaticHash(context);
		int icon = hash.get(ICON, String.valueOf(appWidgetId), 0);
		Log.d(TAG, "updateWidget icon=" + String.valueOf(icon));
		try {
			RemoteViews remoteViews = new RemoteViews(context.getPackageName(),
					R.layout.widget_sticky_ics);
			// remoteViews.setTextViewText(R.id.textView, comment);
			remoteViews.setImageViewResource(R.id.sticky_icon_ImageView, icons[icon]);
			// remoteViews.setImageViewResource(R.id.imageView1, backs[back]);

			Intent stackViewIntent = new Intent(context,
					StickyWidgetService.class);
			stackViewIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID,
					appWidgetId);
			// When intents are compared, the extras are ignored, so we need to
			// embed the extras
			// into the data so that the extras will not be ignored.
			stackViewIntent.setData(Uri.parse(stackViewIntent
					.toUri(Intent.URI_INTENT_SCHEME)));
			remoteViews.setRemoteAdapter(R.id.sticky_comment_StackView, stackViewIntent);

			// The empty view is displayed when the collection has no items. It
			// should be a sibling
			// of the collection view.
			// remoteViews.setEmptyView(R.id.stack_view, R.id.empty_view);

			// ボタンが押された時に発行されるインテントを準備する
			Intent intent = new Intent(context, StickyWidgetConfigure.class);
			intent.setAction(AppWidgetManager.ACTION_APPWIDGET_CONFIGURE);
			intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
			PendingIntent pendingIntent = PendingIntent.getActivity(context,
					appWidgetId, intent, 0);
			remoteViews.setOnClickPendingIntent(R.id.sticky_icon_ImageView, pendingIntent);

			AppWidgetManager appWidgetManager = AppWidgetManager
					.getInstance(context);
			appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetId,
					R.id.sticky_comment_StackView);
			appWidgetManager.updateAppWidget(appWidgetId, remoteViews);
		} catch (Exception e) {
			ExceptionLog.Log(TAG, e);
		}
	}
}
