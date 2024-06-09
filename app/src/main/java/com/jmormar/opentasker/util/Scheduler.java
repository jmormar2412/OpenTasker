package com.jmormar.opentasker.util;

import static com.jmormar.opentasker.util.Constants.NOMBRE_PREFERENCIAS;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;

import com.jmormar.opentasker.models.Evento;

import java.util.Calendar;
import java.util.HashSet;
import java.util.Set;

public class Scheduler {
    private final Context context;
    private final SharedPreferences sharedPreferences;

    public Scheduler(Context context) {
        this.context = context;
        sharedPreferences = context.getSharedPreferences(NOMBRE_PREFERENCIAS, Context.MODE_PRIVATE);
    }
    public void scheduleEventNotifications(Evento evento) {
        Set<String> notificationFrequencies = sharedPreferences.getStringSet("notificationfrequency", new HashSet<>());

        for (String frequency : notificationFrequencies) {
            int daysBefore = Integer.parseInt(frequency);
            scheduleNotification(evento, daysBefore);
        }
    }

    public void clearAllNotifications(Evento evento) {
        AlarmManager alarmManager = (AlarmManager) this.context.getSystemService(Context.ALARM_SERVICE);
        if (alarmManager == null) return;

        Set<String> notificationFrequencies = sharedPreferences.getStringSet("notificationfrequency", new HashSet<>());

        for (String frequency : notificationFrequencies) {
            int daysBefore = Integer.parseInt(frequency);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(
                    this.context,
                    evento.getIdEvento() * 100 + daysBefore,
                    new Intent(this.context, NotificationReceiver.class),
                    PendingIntent.FLAG_IMMUTABLE
            );
            if (pendingIntent != null) {
                alarmManager.cancel(pendingIntent);
                pendingIntent.cancel();
            }
        }
    }

    private void scheduleNotification(Evento evento, int daysBefore) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(evento.getFecha());
        calendar.add(Calendar.DAY_OF_YEAR, -daysBefore);

        long triggerAtMillis = calendar.getTimeInMillis();
        if (triggerAtMillis < System.currentTimeMillis()) return;

        Intent intent = new Intent(this.context, NotificationReceiver.class);
        intent.putExtra("idEvento", evento.getIdEvento());
        intent.putExtra("nombre", evento.getNombre());
        intent.putExtra("diasPara", daysBefore);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                this.context,
                evento.getIdEvento() * 100 + daysBefore,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        AlarmManager alarmManager = (AlarmManager) this.context.getSystemService(Context.ALARM_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if(alarmManager.canScheduleExactAlarms()) alarmManager.setExact(AlarmManager.RTC_WAKEUP, triggerAtMillis, pendingIntent);
        } else{
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, triggerAtMillis, pendingIntent);
        }
    }
}
