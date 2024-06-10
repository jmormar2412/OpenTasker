package com.jmormar.opentasker.logging;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;

import androidx.annotation.NonNull;

import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import timber.log.Timber;

public class ReleaseTree extends Timber.Tree {
    private final Context context;

    public ReleaseTree(Context context) {
        this.context = context;
    }

    @Override
    protected void log(int priority, String tag, @NonNull String message, Throwable t) {
        if (priority == Log.VERBOSE || priority == Log.DEBUG) {
            return;
        }

        String timeStamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date());
        String logMessage = String.format("%s %s/%s: %s\n", timeStamp, getPriorityString(priority), tag, message);

        if (t != null) {
            logMessage += Log.getStackTraceString(t) + '\n';
        }

        writeLogToFile(logMessage);
    }

    private String getPriorityString(int priority) {
        return switch (priority) {
            case Log.ERROR -> "E";
            case Log.WARN -> "W";
            case Log.INFO -> "I";
            case Log.DEBUG -> "D";
            case Log.VERBOSE -> "V";
            case Log.ASSERT -> "A";
            default -> String.valueOf(priority);
        };
    }

    private void writeLogToFile(String logMessage) {
        ContentValues values = new ContentValues();
        values.put(MediaStore.MediaColumns.DISPLAY_NAME, "log.txt");
        values.put(MediaStore.MediaColumns.MIME_TYPE, "text/plain");
        values.put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOCUMENTS + "/logs");

        Uri uri;
        OutputStream outputStream = null;

        try {
            Uri existingUri = getExistingLogUri();
            if (existingUri != null) {
                context.getContentResolver().delete(existingUri, null, null);
            }

            uri = context.getContentResolver().insert(MediaStore.Files.getContentUri("external"), values);
            if (uri != null) {
                outputStream = context.getContentResolver().openOutputStream(uri, "wa");
                if (outputStream != null) {
                    outputStream.write(logMessage.getBytes());
                }
            }
        } catch (IOException e) {
            Timber.tag("ReleaseTree").e(e, "Error writing log to file");
        } finally {
            if (outputStream != null) {
                try {
                    outputStream.close();
                } catch (IOException e) {
                    Timber.tag("ReleaseTree").e(e, "Error closing output stream");
                }
            }
        }
    }

    private Uri getExistingLogUri() {
        Uri queryUri = MediaStore.Files.getContentUri("external");
        String[] projection = {MediaStore.MediaColumns._ID};
        String selection = MediaStore.MediaColumns.DISPLAY_NAME + " = ?";
        String[] selectionArgs = {"log.txt"};

        try (Cursor cursor = context.getContentResolver().query(queryUri, projection, selection, selectionArgs, null)) {
            if (cursor != null && cursor.moveToFirst()) {
                int idColumnIndex = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns._ID);
                long id = cursor.getLong(idColumnIndex);
                return Uri.withAppendedPath(queryUri, String.valueOf(id));
            }
        }

        return null;
    }
}