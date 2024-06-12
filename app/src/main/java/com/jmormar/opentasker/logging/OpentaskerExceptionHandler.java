package com.jmormar.opentasker.logging;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import androidx.annotation.NonNull;

import com.jmormar.opentasker.R;

import timber.log.Timber;

public class OpentaskerExceptionHandler implements Thread.UncaughtExceptionHandler {
    private final Context context;

    public OpentaskerExceptionHandler(Context context) {
        this.context = context;
    }

    @Override
    public void uncaughtException(@NonNull Thread thread, @NonNull Throwable throwable) {
        Timber.e(throwable, context.getString(R.string.excepcion_no_capturada));

        showExceptionDialog(throwable);
    }

    private void showExceptionDialog(Throwable throwable) {
        new Handler(Looper.getMainLooper()).post(() -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setTitle(context.getString(R.string.error_ocurrio));
            builder.setMessage(context.getString(R.string.quieres_enviar_informe));
            builder.setPositiveButton(context.getString(R.string.enviar), (dialog, which) -> sendEmail(throwable));
            builder.setNegativeButton(context.getString(R.string.cancelar), (dialog, which) -> System.exit(1));
            builder.show();
        });
    }

    private void sendEmail(Throwable throwable) {
        Intent emailIntent = new Intent(Intent.ACTION_SENDTO);
        emailIntent.setData(Uri.parse("mailto:jmormar2412@g.educaand.es"));
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Informe Error Opentasker");
        emailIntent.putExtra(Intent.EXTRA_TEXT, Log.getStackTraceString(throwable));

        if (emailIntent.resolveActivity(context.getPackageManager()) != null) {
            context.startActivity(emailIntent);
        }
    }
}
