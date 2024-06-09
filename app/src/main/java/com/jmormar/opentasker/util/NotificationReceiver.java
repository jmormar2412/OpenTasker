package com.jmormar.opentasker.util;

import android.Manifest;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;

import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.jmormar.opentasker.MainActivity;
import com.jmormar.opentasker.R;

public class NotificationReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        int idEvento = intent.getIntExtra("idEvento", -1);
        String nombre = intent.getStringExtra("nombre");
        int diasPara = intent.getIntExtra("diasPara", -1);

        Intent blahdy = new Intent(context, MainActivity.class);
        blahdy.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, idEvento, blahdy, PendingIntent.FLAG_UPDATE_CURRENT);


        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "evento_channel")
                .setSmallIcon(R.drawable.baseline_assignment_24)
                .setContentTitle(context.getString(R.string.recordatorio_evento))
                .setContentText(diasPara + " " + context.getString(R.string.dias_para) + " " + nombre)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU){
            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED) {
                notificationManager.notify(idEvento, builder.build());
            }
        } else{
            notificationManager.notify(idEvento, builder.build());
        }

    }
}

