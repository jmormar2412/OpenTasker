package com.jmormar.opentasker.util;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Build;

import com.jmormar.opentasker.entities.Agenda;
import com.jmormar.opentasker.entities.Categoria;
import com.jmormar.opentasker.entities.Evento;
import com.jmormar.opentasker.entities.Hora;
import com.jmormar.opentasker.entities.Horario;
import com.jmormar.opentasker.entities.Nota;
import com.jmormar.opentasker.entities.Pomodoro;
import com.jmormar.opentasker.entities.Tipo;

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
    @SuppressLint("SimpleDateFormat")
    private final SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd");
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

        String[] projection = {"idAgenda", "nombre", "fechaInicio", "fechaFinal", "beginningDay", "weekLength"};
        Cursor c=db.query("Agenda",projection,null, null, null, null, null);

        Agenda ad = null;

        //Como nada más se va a ejecutar una sóla vez, no hará falta almacenar las agendas en una lista.
        while(c.moveToNext()){
            ad = new Agenda();
            ad.setIdAgenda(c.getInt(c.getColumnIndexOrThrow("idAgenda")));
            ad.setNombre(c.getString(c.getColumnIndexOrThrow("nombre")));
            try{
                ad.setFechaInicio(dateFormatter.parse(c.getString(c.getColumnIndexOrThrow("fechaInicio"))));
                ad.setFechaFinal(dateFormatter.parse(c.getString(c.getColumnIndexOrThrow("fechaFinal"))));
            } catch (ParseException p){
                System.err.println("Error en la lectura de la fecha -> getAgenda()");
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
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("nombre", agenda.getNombre());
        values.put("fechaInicio", dateFormatter.format(agenda.getFechaInicio()));
        values.put("fechaFinal", dateFormatter.format(agenda.getFechaFinal()));
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
                    "ON DELETE CASCADE"+
                    ")";
    private static final String SQL_DELETE_HORARIO = "DROP TABLE IF EXISTS Horario";

    //Sólo se ejecutará una sola vez porque no vas a crear 19 horarios, TODO: implementar el reset con su activity
    public boolean insertarHorario(Horario horario){
        SQLiteDatabase db = this.getWritableDatabase();

        for (Hora h : horario.getHoras()) {
            if(!insertarHora(h)) System.err.println("No se ha introducido la hora. -> insertarHorario()");
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

    public Hora getHora(int idHora){
        SQLiteDatabase db = this.getReadableDatabase();

        String[] projection = {"idHora","fechayTiempoInicio","totalTiempo","idHorario"};
        String[] selectionArgs = { String.valueOf(idHora) };
        String selection="idHora = ?";

        Cursor c = db.query("Hora",projection,selection,selectionArgs,null,null,null);
        List<Hora> lista=new ArrayList<>();

        while (c.moveToNext()) {
            Hora hr=new Hora();
            hr.setIdHora(c.getInt(c.getColumnIndexOrThrow("idHora")));
            hr.setFechayTiempoInicio(LocalDateTime.parse(c.getString(c.getColumnIndexOrThrow("fechayTiempoInicio"))));
            hr.setTotalTiempo(Period.parse(c.getString(c.getColumnIndexOrThrow("totalTiempo"))));
            hr.setIdHorario(c.getInt(c.getColumnIndexOrThrow("idHorario")));
            lista.add(hr);
        }
        c.close();

        if(!lista.isEmpty()) return lista.get(0);

        return null;
    }

    //CATEGORÍA

    private static final String SQL_CREATE_CATEGORIA =
            "CREATE TABLE Categoria (" +
                    "idCategoria INTEGER PRIMARY KEY," +
                    "nombre TEXT, " +
                    "color TEXT, " +
                    "idAgenda INTEGER," +
                    "FOREIGN KEY(idAgenda) REFERENCES Agenda(idAgenda)" +
                    ")";
    private static final String SQL_DELETE_CATEGORIA = "DROP TABLE IF EXISTS Categoria";
    public boolean insertarCategoria(Categoria catg){
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("nombre", catg.getNombre());
        values.put("color", catg.getColor());
        values.put("idAgenda", catg.getIdAgenda());

        return db.insert("Categoria", null, values) > 0;
    }

    public List<Categoria> getCategorias(){
        SQLiteDatabase db=this.getReadableDatabase();

        String[] projection = {"idCategoria", "nombre", "color", "idAgenda"};
        Cursor c=db.query("Categoria",projection,null, null, null, null, null);

        List<Categoria> list = new ArrayList<>();

        while(c.moveToNext()){
            Categoria cat = new Categoria();
            cat.setIdCategoria(c.getInt(c.getColumnIndexOrThrow("idCategoria")));
            cat.setNombre((c.getString(c.getColumnIndexOrThrow("nombre"))));
            cat.setColor(c.getString(c.getColumnIndexOrThrow("color")));
            cat.setIdAgenda(c.getInt(c.getColumnIndexOrThrow("idAgenda")));
            list.add(cat);
        }
        c.close();

        return list;
    }

    public boolean deleteCategoria(int idCategoria){
        SQLiteDatabase db = this.getWritableDatabase();
        String selection = "idCategoria = ?";
        String[] selectionArgs = { String.valueOf(idCategoria) };
        return db.delete("Categoria", selection, selectionArgs)>0;
    }

    public boolean actualizarCategoria(Categoria catg){
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("nombre", catg.getNombre());
        values.put("color", catg.getColor());
        values.put("idAgenda", catg.getIdAgenda());

        String selection = "idCategoria = ?";

        String[] selectionArgs = { String.valueOf(catg.getIdCategoria()) };

        return db.update("Categoria", values, selection, selectionArgs) > 0;
    }

    public Categoria getCategoria(int idCategoria){
        SQLiteDatabase db = this.getReadableDatabase();

        String[] projection = {"idCategoria","nombre","color","idAgenda"};
        String[] selectionArgs = { String.valueOf(idCategoria) };
        String selection="idCategoria = ?";

        Cursor c = db.query("Categoria",projection,selection,selectionArgs,null,null,null);
        List<Categoria> lista=new ArrayList<>();

        while(c.moveToNext()){
            Categoria cat = new Categoria();
            cat.setIdCategoria(c.getInt(c.getColumnIndexOrThrow("idCategoria")));
            cat.setNombre((c.getString(c.getColumnIndexOrThrow("nombre"))));
            cat.setColor(c.getString(c.getColumnIndexOrThrow("color")));
            cat.setIdAgenda(c.getInt(c.getColumnIndexOrThrow("idAgenda")));
            lista.add(cat);
        }
        c.close();

        if(!lista.isEmpty()) return lista.get(0);

        return null;
    }

    //EVENTO

    private static final String SQL_CREATE_EVENTO =
            "CREATE TABLE Evento (" +
                    "idEvento INTEGER PRIMARY KEY," +
                    "nombre TEXT, " +
                    "fecha TEXT, " +
                    "idTipo INTEGER, "+
                    "idCategoria INTEGER," +
                    "idAgenda INTEGER," +
                    "FOREIGN KEY(idTipo) REFERENCES Tipo(idTipo)," +
                    "FOREIGN KEY(idCategoria) REFERENCES Categoria(idCategoria)," +
                    "FOREIGN KEY(idAgenda) REFERENCES Agenda(idAgenda)" +
                    ")";
    private static final String SQL_DELETE_EVENTO = "DROP TABLE IF EXISTS Evento";
    public boolean insertarEvento(Evento evt){
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("nombre", evt.getNombre());
        values.put("fecha", dateFormatter.format(evt.getFecha()));
        values.put("idTipo", evt.getIdTipo());
        values.put("idCategoria", evt.getIdCategoria());
        values.put("idAgenda", evt.getIdAgenda());

        return db.insert("Evento", null, values) > 0;
    }

    public List<Evento> getEventos(){
        SQLiteDatabase db=this.getReadableDatabase();

        String[] projection = {"idEvento", "nombre", "color", "idTipo", "idCategoria", "idAgenda"};
        Cursor c=db.query("Evento",projection,null, null, null, null, null);

        List<Evento> list = new ArrayList<>();

        while(c.moveToNext()){
            Evento evt = new Evento();
            evt.setIdEvento(c.getInt(c.getColumnIndexOrThrow("idEvento")));
            evt.setNombre((c.getString(c.getColumnIndexOrThrow("nombre"))));
            try {
                evt.setFecha(dateFormatter.parse(c.getString(c.getColumnIndexOrThrow("fecha"))));
            } catch (ParseException e) {
                System.err.println("Error en la lectura de la fecha -> getEventos()");
            }
            evt.setIdTipo(c.getInt(c.getColumnIndexOrThrow("idTipo")));
            evt.setIdCategoria(c.getInt(c.getColumnIndexOrThrow("idCategoria")));
            evt.setIdAgenda(c.getInt(c.getColumnIndexOrThrow("idAgenda")));
            list.add(evt);
        }
        c.close();

        return list;
    }

    public boolean deleteEvento(int idEvento){
        SQLiteDatabase db = this.getWritableDatabase();
        String selection = "idEvento = ?";
        String[] selectionArgs = { String.valueOf(idEvento) };
        return db.delete("Evento", selection, selectionArgs)>0;
    }

    public boolean actualizarEvento(Evento evt){
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("nombre", evt.getNombre());
        values.put("fecha", dateFormatter.format(evt.getFecha()));
        values.put("idTipo", evt.getIdTipo());
        values.put("idCategoria", evt.getIdCategoria());
        values.put("idAgenda", evt.getIdAgenda());

        String selection = "idEvento = ?";

        String[] selectionArgs = { String.valueOf(evt.getIdEvento()) };

        return db.update("Evento", values, selection, selectionArgs) > 0;
    }

    public Evento getEvento(int idEvento){
        SQLiteDatabase db = this.getReadableDatabase();

        String[] projection = {"idEvento","nombre","fecha","idTipo", "idCategoria", "idAgenda"};
        String[] selectionArgs = { String.valueOf(idEvento) };
        String selection="idEvento = ?";

        Cursor c = db.query("Evento",projection,selection,selectionArgs,null,null,null);
        List<Evento> lista=new ArrayList<>();

        while(c.moveToNext()){
            Evento evt = new Evento();
            evt.setIdEvento(c.getInt(c.getColumnIndexOrThrow("idEvento")));
            evt.setNombre((c.getString(c.getColumnIndexOrThrow("nombre"))));
            try {
                evt.setFecha(dateFormatter.parse(c.getString(c.getColumnIndexOrThrow("fecha"))));
            } catch (ParseException e) {
                System.err.println("Error en la lectura de la fecha -> getEvento(int idEvento)");
            }
            evt.setIdTipo(c.getInt(c.getColumnIndexOrThrow("idTipo")));
            evt.setIdCategoria(c.getInt(c.getColumnIndexOrThrow("idCategoria")));
            evt.setIdAgenda(c.getInt(c.getColumnIndexOrThrow("idAgenda")));
            lista.add(evt);
        }
        c.close();

        if(!lista.isEmpty()) return lista.get(0);

        return null;
    }

    //NOTA

    private static final String SQL_CREATE_NOTA =
            "CREATE TABLE Nota (" +
                    "idNota INTEGER PRIMARY KEY," +
                    "nombre TEXT, " +
                    "texto TEXT," +
                    "color TEXT," +
                    "idCategoria INTEGER," +
                    "idAgenda INTEGER," +
                    "FOREIGN KEY(idTipo) REFERENCES Tipo(idTipo)," +
                    "FOREIGN KEY(idCategoria) REFERENCES Categoria(idCategoria)," +
                    "FOREIGN KEY(idAgenda) REFERENCES Agenda(idAgenda)" +
                    ")";
    private static final String SQL_DELETE_NOTA = "DROP TABLE IF EXISTS Nota";

    public boolean insertarNota(Nota evt){
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("nombre", evt.getNombre());
        values.put("texto", evt.getTexto());
        values.put("color", evt.getColor());
        values.put("idCategoria", evt.getIdCategoria());

        return db.insert("Nota", null, values) > 0;
    }

    public List<Nota> getNotas(){
        SQLiteDatabase db=this.getReadableDatabase();

        String[] projection = {"idNota", "nombre", "texto", "color", "idCategoria"};
        Cursor c=db.query("Nota",projection,null, null, null, null, null);

        List<Nota> list = new ArrayList<>();

        while(c.moveToNext()){
            Nota nota = new Nota();
            nota.setIdNota(c.getInt(c.getColumnIndexOrThrow("idNota")));
            nota.setNombre((c.getString(c.getColumnIndexOrThrow("nombre"))));
            nota.setTexto(c.getString(c.getColumnIndexOrThrow("texto")));
            nota.setColor(c.getString(c.getColumnIndexOrThrow("color")));
            nota.setIdCategoria(c.getInt(c.getColumnIndexOrThrow("idCategoria")));
            list.add(nota);
        }
        c.close();

        return list;
    }

    public boolean deleteNota(int idNota){
        SQLiteDatabase db = this.getWritableDatabase();
        String selection = "idNota = ?";
        String[] selectionArgs = { String.valueOf(idNota) };
        return db.delete("Nota", selection, selectionArgs)>0;
    }

    public boolean actualizarNota(Nota evt){
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("nombre", evt.getNombre());
        values.put("texto", evt.getTexto());
        values.put("color", evt.getColor());
        values.put("idCategoria", evt.getIdCategoria());

        String selection = "idNota = ?";

        String[] selectionArgs = { String.valueOf(evt.getIdNota()) };

        return db.update("Nota", values, selection, selectionArgs) > 0;
    }

    public Evento getNota(int idNota){
        SQLiteDatabase db = this.getReadableDatabase();

        String[] projection = {"idNota","nombre","texto","color", "idCategoria"};
        String[] selectionArgs = { String.valueOf(idNota) };
        String selection="idNota = ?";

        Cursor c = db.query("Nota",projection,selection,selectionArgs,null,null,null);
        List<Evento> lista=new ArrayList<>();

        while(c.moveToNext()){
            Evento evt = new Evento();
            evt.setIdEvento(c.getInt(c.getColumnIndexOrThrow("idNota")));
            evt.setNombre((c.getString(c.getColumnIndexOrThrow("nombre"))));
            evt.setIdTipo(c.getInt(c.getColumnIndexOrThrow("texto")));
            evt.setIdAgenda(c.getInt(c.getColumnIndexOrThrow("color")));
            evt.setIdCategoria(c.getInt(c.getColumnIndexOrThrow("idCategoria")));
            lista.add(evt);
        }
        c.close();

        if(!lista.isEmpty()) return lista.get(0);

        return null;
    }

    // POMODORO

    private static final String SQL_CREATE_POMODORO =
            "CREATE TABLE Pomodoro (" +
                    "idPomodoro INTEGER PRIMARY KEY," +
                    "nombre TEXT " +
                    ")";
    private static final String SQL_DELETE_POMODORO = "DROP TABLE IF EXISTS Pomodoro";

    public boolean insertarPomodoro(Pomodoro pmd){
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("nombre", pmd.getNombre());

        return db.insert("Pomodoro", null, values) > 0;
    }

    public List<Pomodoro> getPomodoros(){
        SQLiteDatabase db=this.getReadableDatabase();

        String[] projection = {"idPomodoro", "nombre"};
        Cursor c=db.query("Pomodoro",projection,null, null, null, null, null);

        List<Pomodoro> list = new ArrayList<>();

        while(c.moveToNext()){
            Pomodoro pmd = new Pomodoro();
            pmd.setIdPomodoro(c.getInt(c.getColumnIndexOrThrow("idPomodoro")));
            pmd.setNombre((c.getString(c.getColumnIndexOrThrow("nombre"))));
            list.add(pmd);
        }
        c.close();

        return list;
    }

    public boolean deletePomodoro(int idPomodoro){
        SQLiteDatabase db = this.getWritableDatabase();
        String selection = "idPomodoro = ?";
        String[] selectionArgs = { String.valueOf(idPomodoro) };
        return db.delete("Pomodoro", selection, selectionArgs)>0;
    }

    public boolean actualizarPomodoro(Pomodoro pmd){
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("nombre", pmd.getNombre());

        String selection = "idPomodoro = ?";

        String[] selectionArgs = { String.valueOf(pmd.getIdPomodoro()) };

        return db.update("Pomodoro", values, selection, selectionArgs) > 0;
    }

    public Pomodoro getPomodoro(int idPomodoro){
        SQLiteDatabase db = this.getReadableDatabase();

        String[] projection = {"idPomodoro","nombre"};
        String[] selectionArgs = { String.valueOf(idPomodoro) };
        String selection="idPomodoro = ?";

        Cursor c = db.query("Pomodoro",projection,selection,selectionArgs,null,null,null);
        List<Pomodoro> lista=new ArrayList<>();

        while(c.moveToNext()){
            Pomodoro pmd = new Pomodoro();
            pmd.setIdPomodoro(c.getInt(c.getColumnIndexOrThrow("idPomodoro")));
            pmd.setNombre((c.getString(c.getColumnIndexOrThrow("nombre"))));
            lista.add(pmd);
        }
        c.close();

        if(!lista.isEmpty()) return lista.get(0);

        return null;
    }

    // TIPO

    private static final String SQL_CREATE_TIPO =
            "CREATE TABLE Tipo (" +
                    "idTipo INTEGER PRIMARY KEY," +
                    "nombre TEXT " +
                    ")";
    private static final String SQL_DELETE_TIPO = "DROP TABLE IF EXISTS Tipo";

    public boolean insertarTipo(Tipo pmd){
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("nombre", pmd.getNombre());

        return db.insert("Tipo", null, values) > 0;
    }

    public List<Tipo> getTipos(){
        SQLiteDatabase db=this.getReadableDatabase();

        String[] projection = {"idTipo", "nombre"};
        Cursor c=db.query("Tipo",projection,null, null, null, null, null);

        List<Tipo> list = new ArrayList<>();

        while(c.moveToNext()){
            Tipo pmd = new Tipo();
            pmd.setIdTipo(c.getInt(c.getColumnIndexOrThrow("idTipo")));
            pmd.setNombre((c.getString(c.getColumnIndexOrThrow("nombre"))));
            list.add(pmd);
        }
        c.close();

        return list;
    }

    public boolean deleteTipo(int idTipo){
        SQLiteDatabase db = this.getWritableDatabase();
        String selection = "idTipo = ?";
        String[] selectionArgs = { String.valueOf(idTipo) };
        return db.delete("Tipo", selection, selectionArgs)>0;
    }

    public boolean actualizarTipo(Tipo pmd){
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("nombre", pmd.getNombre());

        String selection = "idTipo = ?";

        String[] selectionArgs = { String.valueOf(pmd.getIdTipo()) };

        return db.update("Tipo", values, selection, selectionArgs) > 0;
    }

    public Tipo getTipo(int idTipo){
        SQLiteDatabase db = this.getReadableDatabase();

        String[] projection = {"idTipo","nombre"};
        String[] selectionArgs = { String.valueOf(idTipo) };
        String selection="idTipo = ?";

        Cursor c = db.query("Tipo",projection,selection,selectionArgs,null,null,null);
        List<Tipo> lista=new ArrayList<>();

        while(c.moveToNext()){
            Tipo pmd = new Tipo();
            pmd.setIdTipo(c.getInt(c.getColumnIndexOrThrow("idTipo")));
            pmd.setNombre((c.getString(c.getColumnIndexOrThrow("nombre"))));
            lista.add(pmd);
        }
        c.close();

        if(!lista.isEmpty()) return lista.get(0);

        return null;
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
