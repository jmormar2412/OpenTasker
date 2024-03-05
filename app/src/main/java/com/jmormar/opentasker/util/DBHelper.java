package com.jmormar.opentasker.util;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper {

    private static volatile DBHelper me = null;
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "OpenTaskerBase.db";

    // Strings de creación y borrado de las tablas
    private static final String SQL_CREATE_AGENDA =
            "CREATE TABLE Agenda (" +
                    "idAgenda INTEGER PRIMARY KEY," +
                    "nombre TEXT NOT NULL," +
                    "apellidos TEXT," +
                    "dni TEXT NOT NULL UNIQUE," +
                    "fechaNacimiento TEXT," +
                    "genero INTEGER," +
                    "consentimiento INTEGER," +
                    "nivelEstudios INTEGER" +
                    ")";
    private static final String SQL_DELETE_AGENDA =
            "DROP TABLE IF EXISTS Agenda";

    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
    public static DBHelper getInstance(Context context){
        if (me == null) {
            synchronized(DBHelper.class){
                if(me==null){
                    me = new DBHelper(context);
                }
            }
        }
        return me;
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(SQL_CREATE_AGENDA);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL(SQL_DELETE_AGENDA);
        onCreate(sqLiteDatabase);
    }
}
