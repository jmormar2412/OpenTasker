package com.jmormar.opentasker.onboarding;


import static android.Manifest.permission.POST_NOTIFICATIONS;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.jmormar.opentasker.R;

public class LogoScreenActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_logo_screen);

        ImageView logo = findViewById(R.id.splash_logo);
        TextView appName = findViewById(R.id.tv_app_name);
        Button nextButton = findViewById(R.id.bt_next);

        logo.setVisibility(View.GONE);
        appName.setVisibility(View.GONE);
        nextButton.setVisibility(View.GONE);

        new Handler().postDelayed(() -> {
            logo.setVisibility(View.VISIBLE);
            Animation fadeIn = new AlphaAnimation(0, 1);
            fadeIn.setDuration(1000);
            logo.startAnimation(fadeIn);
        }, 500);

        new Handler().postDelayed(() -> {
            appName.setVisibility(View.VISIBLE);
            Animation fadeIn = new AlphaAnimation(0, 1);
            fadeIn.setDuration(1000);
            appName.startAnimation(fadeIn);
        }, 1500);

        new Handler().postDelayed(() -> {
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU){
                if(ContextCompat.checkSelfPermission(this, POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED){
                    ActivityCompat.requestPermissions(this, new String[]{POST_NOTIFICATIONS}, 100);
                } else{
                    createNotificationChannel();
                }
            } else{
                createNotificationChannel();
            }

            nextButton.setVisibility(View.VISIBLE);
            Animation fadeIn = new AlphaAnimation(0, 1);
            fadeIn.setDuration(1000);
            nextButton.startAnimation(fadeIn);
        }, 2500);

        nextButton.setOnClickListener(v -> {
            Intent intent = new Intent(LogoScreenActivity.this, DatosAgendaActivity.class);
            startActivity(intent);
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            finish();
        });
    }

    private void createNotificationChannel() {
        CharSequence name = "Canal Recordatorio de Eventos";
        String description = "Canal para los recordatorios de los eventos";
        int importance = NotificationManager.IMPORTANCE_HIGH;
        NotificationChannel channel = new NotificationChannel("opentasker_evento_channel", name, importance);
        channel.setDescription(description);
        NotificationManager notificationManager = getSystemService(NotificationManager.class);
        notificationManager.createNotificationChannel(channel);
    }
}