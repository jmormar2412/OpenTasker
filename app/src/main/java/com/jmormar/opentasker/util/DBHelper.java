package com.jmormar.opentasker.util;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.jmormar.opentasker.entities.Agenda;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

/**
 * <h1>DBHelper</h1>
 * <p>Esta clase va a manejar el tema de la base de datos.</p>
 * <p>Utiliza SQLite.</p>
 */
public class DBHelper extends SQLiteOpenHelper {

    private static volatile DBHelper me = null;
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "OpenTaskerBase.db";

    // Aquí van todas las cosas (prepárate para las 2 millones de líneas)

    // AGENDA
    private static final String SQL_CREATE_AGENDA =
            "CREATE TABLE Agenda (" +
                    "idAgenda INTEGER PRIMARY KEY," +
                    "nombre TEXT NOT NULL," +
                    "fechaInicio TEXT," +
                    "fechaFinal TEXT ," +
                    "beginningDay INTEGER," +
                    "weekLength INTEGER" +
                    ")";
    private static final String SQL_DELETE_AGENDA = "DROP TABLE IF EXISTS Agenda";

    public boolean insertarAgenda(Agenda agenda){
        @SuppressLint("SimpleDateFormat") SimpleDateFormat encoder = new SimpleDateFormat("yyyy-MM-dd");
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("nombre", agenda.getNombre());
        values.put("fechaInicio", encoder.format(agenda.getFechaInicio()));
        values.put("fechaFinal", encoder.format(agenda.getFechaFinal()));
        values.put("beginningDay", agenda.getBeginningDay());
        values.put("weekLength", agenda.getWeekLength());

        //Como esto de error me va a entrar puto sida
        return db.insert("Agenda", null, values) > 0;
    }

    public Agenda getAgenda(){
        SQLiteDatabase db=this.getReadableDatabase();
        //No me toques los huevos ya no puedo hacer nada tio
        @SuppressLint("SimpleDateFormat") SimpleDateFormat decoder = new SimpleDateFormat("yyyy-MM-dd");

        String[] projection = {"idAgenda", "nombre", "fechaInicio", "fechaFinal", "beginningDay", "weekLength"};
        Cursor c=db.query("Agenda",projection,null, null, null, null, null);

        Agenda ad = null;

        //Como nada más se va a ejecutar una sóla vez, no hará falta almacenar las agendas en una lista.
        while(c.moveToNext()){
            ad = new Agenda();
            ad.setIdAgenda(c.getInt(c.getColumnIndexOrThrow("idAgenda")));
            ad.setNombre(c.getString(c.getColumnIndexOrThrow("nombre")));
            try{
                ad.setFechaInicio(decoder.parse(c.getString(c.getColumnIndexOrThrow("fechaInicio"))));
                ad.setFechaFinal(decoder.parse(c.getString(c.getColumnIndexOrThrow("fechaFinal"))));
            } catch (ParseException p){
                System.err.println("La fecha no ha podido ser leída de la base de datos.");
            }
            //Horror
            ad.setBeginningDay((byte) c.getInt(c.getColumnIndexOrThrow("beginningDay")));
            ad.setWeekLength((byte) c.getInt(c.getColumnIndexOrThrow("weekLength")));
        }
        c.close();

        return ad;
    }

    //No hará falta eliminar agenda porque si la eliminas se van todos al carajo (añadir un reset por alguna parte con su correspondiende activity)

    public boolean actualizarAgenda(Agenda agenda){
        @SuppressLint("SimpleDateFormat") SimpleDateFormat encoder = new SimpleDateFormat("yyyy-MM-dd");
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("nombre", agenda.getNombre());
        values.put("fechaInicio", encoder.format(agenda.getFechaInicio()));
        values.put("fechaFinal", encoder.format(agenda.getFechaFinal()));
        values.put("beginningDay", agenda.getBeginningDay());
        values.put("weekLength", agenda.getWeekLength());

        String selection = "idAgenda = ?";

        String[] selectionArgs = { String.valueOf(agenda.getIdAgenda()) };

        //Ea, con dos huevos. No me des error por favor.
        return db.update("Agenda", values, selection, selectionArgs) > 0;
    }

    //HORACIO Y HORAS (son dependientes)

    private static final String SQL_CREATE_HORARIO =
            "CREATE TABLE Horario (" +
                    "idHorario INTEGER PRIMARY KEY," +
                    ")";
    private static final String SQL_DELETE_HORARIO = "DROP TABLE IF EXISTS Horario";

    private static final String SQL_CREATE_HORA =
            "CREATE TABLE Hora (" +
                    "idHorario INTEGER PRIMARY KEY," +
                    ")";
    private static final String SQL_DELETE_HORA = "DROP TABLE IF EXISTS Horario";




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
        //Ejecutar todos los create
        sqLiteDatabase.execSQL(SQL_CREATE_AGENDA);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL(SQL_DELETE_AGENDA);
        onCreate(sqLiteDatabase);
    }
}
