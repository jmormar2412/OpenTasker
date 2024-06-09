package com.jmormar.opentasker.util;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.jmormar.opentasker.models.Evento;

import java.util.List;

public class BootReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        DBHelper helper = DBHelper.getInstance(context);

        if (Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction())) {
            Scheduler scheduler = new Scheduler(context);
            List<Evento> eventos = helper.getEventos();
            for (Evento evento : eventos) {
                scheduler.clearAllNotifications(evento);
                scheduler.scheduleEventNotifications(evento);
            }
        }
    }
}
