package com.jmormar.opentasker.util;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Build;

import com.jmormar.opentasker.entities.Agenda;
import com.jmormar.opentasker.entities.Hora;
import com.jmormar.opentasker.entities.Horario;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.Period;
import java.util.ArrayList;
import java.util.List;

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
            //Horror (conversión de int a byte)
            ad.setBeginningDay((byte) c.getInt(c.getColumnIndexOrThrow("beginningDay")));
            ad.setWeekLength((byte) c.getInt(c.getColumnIndexOrThrow("weekLength")));
        }
        c.close();

        return ad;
    }

    //No hará falta eliminar agenda porque si la eliminas se van todos al carajo

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
                    "idAgenda INTEGER, "+
                    "FOREIGN KEY(idAgenda) REFERENCES Agenda(idAgenda)"+
                    ")";
    private static final String SQL_DELETE_HORARIO = "DROP TABLE IF EXISTS Horario";

    //Sólo se ejecutará una sola vez porque no vas a crear 19 horarios, TODO: implementar el reset con su activity
    //Por tanto no hará falta un borrar 1 horario, porque el reset borrará el único que hay TODO:buscar on delete cascade
    public boolean insertarHorario(Horario horario){
        SQLiteDatabase db = this.getWritableDatabase();

        for (Hora h : horario.getHoras()) {
            if(!insertarHora(h)) System.err.println("No se ha introducido la hora.");
        }

        ContentValues values = new ContentValues();
        values.put("idAgenda", horario.getIdAgenda());

        //Como esto de error me va a entrar puto sida
        return db.insert("Horario", null, values) > 0;
    }

    //No hace falta getHorario porque como las horas no van a tener nada más que 1 horario se hace desde el getHoras()
    private static final String SQL_CREATE_HORA =
            "CREATE TABLE Hora (" +
                    "idHora INTEGER PRIMARY KEY," +
                    "fechayTiempoInicio TEXT, " +
                    "totalTiempo TEXT, " +
                    "idHorario INTEGER, "+
                    "FOREIGN KEY(idHorario) REFERENCES Horario(idHorario)" +
                    ")";
    private static final String SQL_DELETE_HORA = "DROP TABLE IF EXISTS Hora";
    public boolean insertarHora(Hora hora){
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("fechayTiempoInicio", hora.getFechayTiempoInicio().toString());
        values.put("totalTiempo", hora.getTotalTiempo().toString());
        values.put("idHorario", hora.getIdHorario());

        return db.insert("Hora", null, values) > 0;
    }

    public List<Hora> getHoras(){
        SQLiteDatabase db=this.getReadableDatabase();
        @SuppressLint("SimpleDateFormat") SimpleDateFormat decoder = new SimpleDateFormat("yyyy-MM-dd");

        String[] projection = {"idHora", "fechayTiempoInicio", "totalTiempo", "idHorario"};
        Cursor c=db.query("Hora",projection,null, null, null, null, null);

        List<Hora> list = new ArrayList<>();

        while(c.moveToNext()){
            Hora hr = new Hora();
            hr.setIdHora(c.getInt(c.getColumnIndexOrThrow("idHora")));
            hr.setFechayTiempoInicio(LocalDateTime.parse(c.getString(c.getColumnIndexOrThrow("fechayTiempoInicio"))));
            hr.setTotalTiempo(Period.parse(c.getString(c.getColumnIndexOrThrow("totalTiempo"))));
            hr.setIdHorario(c.getInt(c.getColumnIndexOrThrow("idHorario")));
            list.add(hr);
        }
        c.close();

        return list;
    }

    public boolean deleteHora(int idHora){
        SQLiteDatabase db = this.getWritableDatabase();
        String selection = "idHora = ?";
        String[] selectionArgs = { String.valueOf(idHora) };
        return db.delete("Hora", selection, selectionArgs)>0;
    }

    public boolean actualizarHora(Hora hora){
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("fechayTiempoInicio", hora.getFechayTiempoInicio().toString());
        values.put("totalTiempo", hora.getTotalTiempo().toString());
        values.put("idHorario", hora.getIdHorario());

        String selection = "idHora = ?";

        String[] selectionArgs = { String.valueOf(hora.getIdHora()) };

        return db.update("Hora", values, selection, selectionArgs) > 0;
    }






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
        sqLiteDatabase.execSQL(SQL_CREATE_HORA);
        sqLiteDatabase.execSQL(SQL_CREATE_HORARIO);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL(SQL_DELETE_AGENDA);
        sqLiteDatabase.execSQL(SQL_DELETE_HORA);
        sqLiteDatabase.execSQL(SQL_DELETE_HORARIO);
        onCreate(sqLiteDatabase);
    }
}
