package com.jmormar.opentasker.widgets;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.util.Log;
import android.widget.RemoteViews;

import com.jmormar.opentasker.R;
import com.jmormar.opentasker.activities.builders.NewEventoActivity;
import com.jmormar.opentasker.activities.modifiers.ModifyEventosActivity;
import com.jmormar.opentasker.fragments.EventosFragment;

public class WidgetEventos extends AppWidgetProvider {

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        for (int appWidgetId : appWidgetIds) {
            RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget_eventos);

            Intent widgetIntent = new Intent(context, EventosFragment.class);
            PendingIntent widgetPendingIntent = PendingIntent.getActivity(context, 0, widgetIntent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
            views.setOnClickPendingIntent(R.id.tv_titulo, widgetPendingIntent);
            views.setTextColor(R.id.tv_titulo, Color.WHITE);

            Intent addIntent = new Intent(context, NewEventoActivity.class);
            PendingIntent addPendingIntent = PendingIntent.getActivity(context, 0, addIntent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
            views.setOnClickPendingIntent(R.id.ib_add, addPendingIntent);

            Intent clickIntent = new Intent(context, ModifyEventosActivity.class);
            PendingIntent clickPendingIntent = PendingIntent.getActivity(context, 0, clickIntent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_MUTABLE);
            views.setPendingIntentTemplate(R.id.lv_eventos, clickPendingIntent);

            Intent serviceIntent = new Intent(context, EventosRemoteViewsService.class);
            serviceIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
            views.setRemoteAdapter(R.id.lv_eventos, serviceIntent);

            appWidgetManager.updateAppWidget(appWidgetId, views);
        }
    }
}