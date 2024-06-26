package com.jmormar.opentasker.util;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.jmormar.opentasker.models.Agenda;
import com.jmormar.opentasker.models.Categoria;
import com.jmormar.opentasker.models.Evento;
import com.jmormar.opentasker.models.Hora;
import com.jmormar.opentasker.models.Horario;
import com.jmormar.opentasker.models.Nota;
import com.jmormar.opentasker.models.Pomodoro;
import com.jmormar.opentasker.models.Tiempo;
import com.jmormar.opentasker.models.Tipo;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * <h1>DBHelper</h1>
 * <p>Esta clase va a manejar el tema de la base de datos.</p>
 * <p>Utiliza SQLite.</p>
 */
public class DBHelper extends SQLiteOpenHelper {

    private static DBHelper me = null;
    private final SimpleDateFormat dateFormatter=new SimpleDateFormat("yyyy-MM-dd", new Locale("es_ES"));
    public static final int DATABASE_VERSION = 5;
    public static final String DATABASE_NAME = "OpenTaskerBase.db";

    private DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public static synchronized DBHelper getInstance(Context context) {
        if (me == null) {
            me = new DBHelper(context.getApplicationContext());
        }
        return me;
    }


    private static final String SQL_CREATE_AGENDA =
            """
            CREATE TABLE Agenda (
                idAgenda INTEGER PRIMARY KEY,
                fechaInicio TEXT,
                fechaFinal TEXT ,
                beginningDay INTEGER,
                weekLength INTEGER
            );
            """;


    private static final String SQL_DELETE_AGENDA = "DROP TABLE IF EXISTS Agenda";

    public boolean insertarAgenda(Agenda agenda) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("fechaInicio", dateFormatter.format(agenda.getFechaInicio()));
        values.put("fechaFinal", dateFormatter.format(agenda.getFechaFinal()));
        values.put("beginningDay", agenda.getBeginningDay());
        values.put("weekLength", agenda.getWeekLength());

        return db.insert("Agenda", null, values) > 0;
    }

    public Agenda getAgenda() {
        SQLiteDatabase db = this.getReadableDatabase();

        String[] projection = {"idAgenda", "fechaInicio", "fechaFinal", "beginningDay", "weekLength"};
        Cursor c = db.query("Agenda", projection, null, null, null, null, null);

        Agenda ad = null;

        while (c.moveToNext()) {
            ad = new Agenda();
            ad.setIdAgenda(c.getInt(c.getColumnIndexOrThrow("idAgenda")));
            try {
                ad.setFechaInicio(dateFormatter.parse(c.getString(c.getColumnIndexOrThrow("fechaInicio"))));
                ad.setFechaFinal(dateFormatter.parse(c.getString(c.getColumnIndexOrThrow("fechaFinal"))));
            } catch (ParseException p) {
                System.err.println("Error en la lectura de la fecha -> getAgenda()");
            }
            ad.setBeginningDay((byte) c.getInt(c.getColumnIndexOrThrow("beginningDay")));
            ad.setWeekLength((byte) c.getInt(c.getColumnIndexOrThrow("weekLength")));
        }
        c.close();

        return ad;
    }


    public boolean actualizarAgenda(Agenda agenda) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("fechaInicio", dateFormatter.format(agenda.getFechaInicio()));
        values.put("fechaFinal", dateFormatter.format(agenda.getFechaFinal()));
        values.put("beginningDay", agenda.getBeginningDay());
        values.put("weekLength", agenda.getWeekLength());

        String selection = "idAgenda = ?";

        String[] selectionArgs = {String.valueOf(agenda.getIdAgenda())};

        return db.update("Agenda", values, selection, selectionArgs) > 0;
    }


    private static final String SQL_CREATE_HORARIO =
            """
            CREATE TABLE Horario (
                  idHorario INTEGER PRIMARY KEY,
                  idAgenda INTEGER,
                  FOREIGN KEY(idAgenda) REFERENCES Agenda(idAgenda)
                  ON DELETE CASCADE
            )
            """;
    private static final String SQL_DELETE_HORARIO = "DROP TABLE IF EXISTS Horario";

    public boolean insertarHorario(Horario horario) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("idAgenda", horario.getIdAgenda());

        return db.insert("Horario", null, values) > 0;
    }

    public Horario getHorario(){
        SQLiteDatabase db = this.getReadableDatabase();
        String[] projection = {"idHorario", "idAgenda"};
        Cursor c = db.query("Horario", projection, null, null, null, null, null);
        Horario horario = null;
        while (c.moveToNext()) {
            horario = new Horario();
            horario.setIdHorario(c.getInt(c.getColumnIndexOrThrow("idHorario")));
            horario.setIdAgenda(c.getInt(c.getColumnIndexOrThrow("idAgenda")));
        }
        c.close();
        return horario;
    }

    private static final String SQL_CREATE_HORA =
            """
            CREATE TABLE Hora (
                 idHora INTEGER PRIMARY KEY,
                 tiempoInicio TEXT,
                 totalTiempo TEXT,
                 diaSemana INTEGER,
                 idHorario INTEGER,
                 idCategoria Integer,
                 FOREIGN KEY(idHorario) REFERENCES Horario(idHorario),
                 FOREIGN KEY(idCategoria) REFERENCES Categoria(idCategoria)
            );
            """;
    private static final String SQL_DELETE_HORA = "DROP TABLE IF EXISTS Hora";

    public boolean insertarHora(Hora hora) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("tiempoInicio", hora.getTiempoInicio().toString());
        values.put("totalTiempo", hora.getTotalTiempo().toString());
        values.put("diaSemana", hora.getDiaSemana());
        values.put("idHorario", hora.getIdHorario());
        values.put("idCategoria", hora.getIdCategoria());

        return db.insert("Hora", null, values) > 0;
    }

    public List<Hora> getHoras() {
        Hora.resetList();
        SQLiteDatabase db = this.getReadableDatabase();

        String[] projection = {"idHora", "tiempoInicio", "totalTiempo", "diaSemana", "idHorario", "idCategoria"};
        Cursor c = db.query("Hora", projection, null, null, null, null, null);

        List<Hora> list = new ArrayList<>();

        while (c.moveToNext()) {
            Hora hr = new Hora();
            hr.setIdHora(c.getInt(c.getColumnIndexOrThrow("idHora")));
            hr.setTiempoInicio(LocalTime.parse(c.getString(c.getColumnIndexOrThrow("tiempoInicio"))));
            hr.setTotalTiempo(Duration.parse(c.getString(c.getColumnIndexOrThrow("totalTiempo"))));
            hr.setDiaSemana(c.getInt(c.getColumnIndexOrThrow("diaSemana")));
            hr.setIdHorario(c.getInt(c.getColumnIndexOrThrow("idHorario")));
            hr.setIdCategoria(c.getInt(c.getColumnIndexOrThrow("idCategoria")));
            list.add(hr);
        }
        c.close();

        return list;
    }

    public boolean deleteHora(int idHora) {
        SQLiteDatabase db = this.getWritableDatabase();
        String selection = "idHora = ?";
        String[] selectionArgs = {String.valueOf(idHora)};
        return db.delete("Hora", selection, selectionArgs) > 0;
    }

    public boolean actualizarHora(Hora hora) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("tiempoInicio", hora.getTiempoInicio().toString());
        values.put("totalTiempo", hora.getTotalTiempo().toString());
        values.put("diaSemana", hora.getDiaSemana());
        values.put("idHorario", hora.getIdHorario());
        values.put("idCategoria", hora.getIdCategoria());

        String selection = "idHora = ?";

        String[] selectionArgs = {String.valueOf(hora.getIdHora())};

        return db.update("Hora", values, selection, selectionArgs) > 0;
    }

    public Hora getHora(int idHora) {
        SQLiteDatabase db = this.getReadableDatabase();

        String[] projection = {"idHora", "tiempoInicio", "totalTiempo", "diaSemana", "idHorario", "idCategoria"};
        String[] selectionArgs = {String.valueOf(idHora)};
        String selection = "idHora = ?";

        Cursor c = db.query("Hora", projection, selection, selectionArgs, null, null, null);
        List<Hora> lista = new ArrayList<>();

        while (c.moveToNext()) {
            Hora hr = new Hora();
            hr.setIdHora(c.getInt(c.getColumnIndexOrThrow("idHora")));
            hr.setTiempoInicio(LocalTime.parse(c.getString(c.getColumnIndexOrThrow("tiempoInicio"))));
            hr.setTotalTiempo(Duration.parse(c.getString(c.getColumnIndexOrThrow("totalTiempo"))));
            hr.setDiaSemana(c.getInt(c.getColumnIndexOrThrow("diaSemana")));
            hr.setIdHorario(c.getInt(c.getColumnIndexOrThrow("idHorario")));
            hr.setIdCategoria(c.getInt(c.getColumnIndexOrThrow("idCategoria")));
            lista.add(hr);
        }
        c.close();

        return lista.stream().findFirst().orElse(null);

    }

    public List<Hora> getHorasByDayAndHorario(int diaSemana) {
        List<Hora> horas = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        String[] projection = {"idHora", "tiempoInicio", "totalTiempo", "diaSemana", "idHorario", "idCategoria"};
        String selection = "diaSemana = ?";
        String[] selectionArgs = {String.valueOf(diaSemana)};
        Cursor c = db.query("Hora", projection, selection, selectionArgs, null, null, null);

        if (c.moveToFirst()) {
            do {
                Hora hora = new Hora();
                hora.setIdHora(c.getInt(c.getColumnIndexOrThrow("idHora")));
                hora.setTiempoInicio(LocalTime.parse(c.getString(c.getColumnIndexOrThrow("tiempoInicio"))));
                hora.setTotalTiempo(Duration.parse(c.getString(c.getColumnIndexOrThrow("totalTiempo"))));
                hora.setDiaSemana(c.getInt(c.getColumnIndexOrThrow("diaSemana")));
                hora.setIdHorario(c.getInt(c.getColumnIndexOrThrow("idHorario")));
                hora.setIdCategoria(c.getInt(c.getColumnIndexOrThrow("idCategoria")));
                horas.add(hora);
            } while (c.moveToNext());
        }
        c.close();
        return horas;
    }



    private static final String SQL_CREATE_CATEGORIA =
            """
            CREATE TABLE Categoria (
                idCategoria INTEGER PRIMARY KEY,
                nombre TEXT,
                acronimo TEXT,
                color INTEGER,
                idAgenda INTEGER,
                FOREIGN KEY(idAgenda) REFERENCES Agenda(idAgenda)
            );
            """;
    private static final String SQL_DELETE_CATEGORIA = "DROP TABLE IF EXISTS Categoria";

    public boolean insertarCategoria(Categoria catg) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("nombre", catg.getNombre());
        values.put("acronimo", catg.getAcronimo());
        values.put("color", catg.getColor());
        values.put("idAgenda", catg.getIdAgenda());

        return db.insert("Categoria", null, values) > 0;
    }

    public List<Categoria> getCategorias() {
        SQLiteDatabase db = this.getReadableDatabase();

        String[] projection = {"idCategoria", "nombre", "acronimo", "color", "idAgenda"};
        Cursor c = db.query("Categoria", projection, null, null, null, null, null);

        List<Categoria> list = new ArrayList<>();

        while (c.moveToNext()) {
            Categoria cat = new Categoria();
            cat.setIdCategoria(c.getInt(c.getColumnIndexOrThrow("idCategoria")));
            cat.setNombre((c.getString(c.getColumnIndexOrThrow("nombre"))));
            cat.setAcronimo(c.getString(c.getColumnIndexOrThrow("acronimo")));
            cat.setColor(c.getInt(c.getColumnIndexOrThrow("color")));
            cat.setIdAgenda(c.getInt(c.getColumnIndexOrThrow("idAgenda")));
            list.add(cat);
        }
        c.close();

        return list;
    }

    public boolean deleteCategoria(int idCategoria) {
        SQLiteDatabase db = this.getWritableDatabase();
        String selection = "idCategoria = ?";
        String[] selectionArgs = {String.valueOf(idCategoria)};
        return db.delete("Categoria", selection, selectionArgs) > 0;
    }

    public boolean actualizarCategoria(Categoria catg) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("nombre", catg.getNombre());
        values.put("acronimo", catg.getAcronimo());
        values.put("color", catg.getColor());

        String selection = "idCategoria = ?";

        String[] selectionArgs = {String.valueOf(catg.getIdCategoria())};

        return db.update("Categoria", values, selection, selectionArgs) > 0;
    }

    public Categoria getCategoria(int idCategoria) {
        SQLiteDatabase db = this.getReadableDatabase();

        String[] projection = {"idCategoria", "nombre", "acronimo", "color", "idAgenda"};
        String[] selectionArgs = {String.valueOf(idCategoria)};
        String selection = "idCategoria = ?";

        Cursor c = db.query("Categoria", projection, selection, selectionArgs, null, null, null);
        List<Categoria> lista = new ArrayList<>();

        while (c.moveToNext()) {
            Categoria cat = new Categoria();
            cat.setIdCategoria(c.getInt(c.getColumnIndexOrThrow("idCategoria")));
            cat.setNombre((c.getString(c.getColumnIndexOrThrow("nombre"))));
            cat.setAcronimo(c.getString(c.getColumnIndexOrThrow("acronimo")));
            cat.setColor(c.getInt(c.getColumnIndexOrThrow("color")));
            cat.setIdAgenda(c.getInt(c.getColumnIndexOrThrow("idAgenda")));
            lista.add(cat);
        }
        c.close();

        if (!lista.isEmpty()) return lista.get(0);

        return null;
    }


    private static final String SQL_CREATE_EVENTO =
            """
            CREATE TABLE Evento (
                idEvento INTEGER PRIMARY KEY,
                nombre TEXT,
                fecha TEXT,
                hecho INTEGER,
                idTipo INTEGER,
                idCategoria INTEGER,
                FOREIGN KEY(idTipo) REFERENCES Tipo(idTipo),
                FOREIGN KEY(idCategoria) REFERENCES Categoria(idCategoria)
            );
            """;
    private static final String SQL_DELETE_EVENTO = "DROP TABLE IF EXISTS Evento";

    public boolean insertarEvento(Evento evt) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("nombre", evt.getNombre());
        values.put("fecha", dateFormatter.format(evt.getFecha()));
        values.put("hecho", evt.isHecho()?1:0);
        values.put("idTipo", evt.getIdTipo());
        values.put("idCategoria", evt.getIdCategoria());

        return db.insert("Evento", null, values) > 0;
    }

    public List<Evento> getEventos() {
        SQLiteDatabase db = this.getReadableDatabase();

        String[] projection = {"idEvento", "nombre", "fecha", "hecho", "idTipo", "idCategoria"};
        Cursor c = db.query("Evento", projection, null, null, null, null, null);

        List<Evento> list = new ArrayList<>();

        while (c.moveToNext()) {
            Evento evt = new Evento();
            evt.setIdEvento(c.getInt(c.getColumnIndexOrThrow("idEvento")));
            evt.setNombre((c.getString(c.getColumnIndexOrThrow("nombre"))));
            evt.setHecho(c.getInt(c.getColumnIndexOrThrow("hecho"))==1);
            try {
                evt.setFecha(dateFormatter.parse(c.getString(c.getColumnIndexOrThrow("fecha"))));
            } catch (ParseException e) {
                System.err.println("Error en la lectura de la fecha -> getEventos()");
            }
            evt.setIdTipo(c.getInt(c.getColumnIndexOrThrow("idTipo")));
            evt.setIdCategoria(c.getInt(c.getColumnIndexOrThrow("idCategoria")));
            list.add(evt);
        }
        c.close();

        return list;
    }

    public boolean deleteEvento(int idEvento) {
        SQLiteDatabase db = this.getWritableDatabase();
        String selection = "idEvento = ?";
        String[] selectionArgs = {String.valueOf(idEvento)};
        return db.delete("Evento", selection, selectionArgs) > 0;
    }

    public boolean actualizarEvento(Evento evt) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("nombre", evt.getNombre());
        values.put("fecha", dateFormatter.format(evt.getFecha()));
        values.put("hecho", evt.isHecho()?1:0);
        values.put("idTipo", evt.getIdTipo());
        values.put("idCategoria", evt.getIdCategoria());

        String selection = "idEvento = ?";

        String[] selectionArgs = {String.valueOf(evt.getIdEvento())};

        return db.update("Evento", values, selection, selectionArgs) > 0;
    }

    public Evento getEvento(int idEvento) {
        SQLiteDatabase db = this.getReadableDatabase();

        String[] projection = {"idEvento", "nombre", "hecho", "fecha","idTipo", "idCategoria"};
        String[] selectionArgs = {String.valueOf(idEvento)};
        String selection = "idEvento = ?";

        Cursor c = db.query("Evento", projection, selection, selectionArgs, null, null, null);
        List<Evento> lista = new ArrayList<>();

        while (c.moveToNext()) {
            Evento evt = new Evento();
            evt.setIdEvento(c.getInt(c.getColumnIndexOrThrow("idEvento")));
            evt.setNombre((c.getString(c.getColumnIndexOrThrow("nombre"))));
            evt.setHecho(c.getInt(c.getColumnIndexOrThrow("hecho"))==1);
            try {
                evt.setFecha(dateFormatter.parse(c.getString(c.getColumnIndexOrThrow("fecha"))));
            } catch (ParseException e) {
                System.err.println("Error en la lectura de la fecha -> getEvento(int idEvento)");
            }
            evt.setIdTipo(c.getInt(c.getColumnIndexOrThrow("idTipo")));
            evt.setIdCategoria(c.getInt(c.getColumnIndexOrThrow("idCategoria")));
            lista.add(evt);
        }
        c.close();

        if (!lista.isEmpty()) return lista.get(0);

        return null;
    }


    private static final String SQL_CREATE_NOTA =
            """
            CREATE TABLE Nota (
                idNota INTEGER PRIMARY KEY,
                titulo TEXT,
                texto TEXT,
                color INTEGER,
                idCategoria INTEGER,
                FOREIGN KEY(idCategoria) REFERENCES Categoria(idCategoria)
            );
            """;
    private static final String SQL_DELETE_NOTA = "DROP TABLE IF EXISTS Nota";

    public boolean insertarNota(Nota nota) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("titulo", nota.getTitulo());
        values.put("texto", nota.getTexto());
        values.put("color", nota.getColor());
        values.put("idCategoria", nota.getIdCategoria());

        return db.insert("Nota", null, values) > 0;
    }

    public List<Nota> getNotas() {
        SQLiteDatabase db = this.getReadableDatabase();

        String[] projection = {"idNota", "titulo", "texto", "color", "idCategoria"};
        Cursor c = db.query("Nota", projection, null, null, null, null, null);

        List<Nota> list = new ArrayList<>();

        while (c.moveToNext()) {
            Nota nota = new Nota();
            nota.setIdNota(c.getInt(c.getColumnIndexOrThrow("idNota")));
            nota.setTitulo((c.getString(c.getColumnIndexOrThrow("titulo"))));
            nota.setTexto(c.getString(c.getColumnIndexOrThrow("texto")));
            nota.setColor(c.getInt(c.getColumnIndexOrThrow("color")));
            nota.setIdCategoria(c.getInt(c.getColumnIndexOrThrow("idCategoria")));
            list.add(nota);
        }
        c.close();

        return list;
    }

    public boolean deleteNota(int idNota) {
        SQLiteDatabase db = this.getWritableDatabase();
        String selection = "idNota = ?";
        String[] selectionArgs = {String.valueOf(idNota)};
        return db.delete("Nota", selection, selectionArgs) > 0;
    }

    public boolean actualizarNota(Nota nota) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put("titulo", nota.getTitulo());
        values.put("texto", nota.getTexto());
        values.put("color", nota.getColor());
        values.put("idCategoria", nota.getIdCategoria());

        String selection = "idNota = ?";

        String[] selectionArgs = {String.valueOf(nota.getIdNota())};

        return db.update("Nota", values, selection, selectionArgs) > 0;
    }

    public Nota getNota(int idNota) {
        SQLiteDatabase db = this.getReadableDatabase();

        String[] projection = {"idNota", "titulo", "texto", "color", "idCategoria"};
        String[] selectionArgs = {String.valueOf(idNota)};
        String selection = "idNota = ?";

        Cursor c = db.query("Nota", projection, selection, selectionArgs, null, null, null);
        List<Nota> lista = new ArrayList<>();

        while (c.moveToNext()) {
            Nota nota = new Nota();
            nota.setIdNota(c.getInt(c.getColumnIndexOrThrow("idNota")));
            nota.setTitulo((c.getString(c.getColumnIndexOrThrow("titulo"))));
            nota.setTexto(c.getString(c.getColumnIndexOrThrow("texto")));
            nota.setColor(c.getInt(c.getColumnIndexOrThrow("color")));
            nota.setIdCategoria(c.getInt(c.getColumnIndexOrThrow("idCategoria")));
            lista.add(nota);
        }
        c.close();

        if (!lista.isEmpty()) return lista.get(0);

        return null;
    }


    private static final String SQL_CREATE_POMODORO =
            """
            CREATE TABLE Pomodoro (
                idPomodoro INTEGER PRIMARY KEY,
                nombre TEXT
            );
            """;
    private static final String SQL_DELETE_POMODORO = "DROP TABLE IF EXISTS Pomodoro";

    public boolean insertarPomodoro(Pomodoro pmd) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("nombre", pmd.getNombre());

        return db.insert("Pomodoro", null, values) > 0;
    }

    public List<Pomodoro> getPomodoros() {
        SQLiteDatabase db = this.getReadableDatabase();

        String[] projection = {"idPomodoro", "nombre"};
        Cursor c = db.query("Pomodoro", projection, null, null, null, null, null);

        List<Pomodoro> list = new ArrayList<>();

        while (c.moveToNext()) {
            Pomodoro pmd = new Pomodoro();
            pmd.setIdPomodoro(c.getInt(c.getColumnIndexOrThrow("idPomodoro")));
            pmd.setNombre((c.getString(c.getColumnIndexOrThrow("nombre"))));
            list.add(pmd);
        }
        c.close();

        return list;
    }

    public boolean deletePomodoro(int idPomodoro) {
        SQLiteDatabase db = this.getWritableDatabase();
        String selection = "idPomodoro = ?";
        String[] selectionArgs = {String.valueOf(idPomodoro)};
        return db.delete("Pomodoro", selection, selectionArgs) > 0;
    }

    public boolean actualizarPomodoro(Pomodoro pmd) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("nombre", pmd.getNombre());

        String selection = "idPomodoro = ?";

        String[] selectionArgs = {String.valueOf(pmd.getIdPomodoro())};

        return db.update("Pomodoro", values, selection, selectionArgs) > 0;
    }

    public Pomodoro getPomodoro(int idPomodoro) {
        SQLiteDatabase db = this.getReadableDatabase();

        String[] projection = {"idPomodoro", "nombre"};
        String[] selectionArgs = {String.valueOf(idPomodoro)};
        String selection = "idPomodoro = ?";

        Cursor c = db.query("Pomodoro", projection, selection, selectionArgs, null, null, null);
        List<Pomodoro> lista = new ArrayList<>();

        while (c.moveToNext()) {
            Pomodoro pmd = new Pomodoro();
            pmd.setIdPomodoro(c.getInt(c.getColumnIndexOrThrow("idPomodoro")));
            pmd.setNombre((c.getString(c.getColumnIndexOrThrow("nombre"))));
            lista.add(pmd);
        }
        c.close();

        if (!lista.isEmpty()) return lista.get(0);

        return null;
    }


    private static final String SQL_CREATE_TIPO =
            """
            CREATE TABLE Tipo (
                idTipo INTEGER PRIMARY KEY,
                nombre TEXT
            );
            """;
    private static final String SQL_DELETE_TIPO = "DROP TABLE IF EXISTS Tipo";

    public boolean insertarTipo(Tipo tp) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("nombre", tp.getNombre());

        return db.insert("Tipo", null, values) > 0;
    }

    public List<Tipo> getTipos() {
        SQLiteDatabase db = this.getReadableDatabase();

        String[] projection = {"idTipo", "nombre"};
        Cursor c = db.query("Tipo", projection, null, null, null, null, null);

        List<Tipo> list = new ArrayList<>();

        while (c.moveToNext()) {
            Tipo tp = new Tipo();
            tp.setIdTipo(c.getInt(c.getColumnIndexOrThrow("idTipo")));
            tp.setNombre((c.getString(c.getColumnIndexOrThrow("nombre"))));
            list.add(tp);
        }
        c.close();

        return list;
    }

    public boolean deleteTipo(int idTipo) {
        SQLiteDatabase db = this.getWritableDatabase();
        String selection = "idTipo = ?";
        String[] selectionArgs = {String.valueOf(idTipo)};
        return db.delete("Tipo", selection, selectionArgs) > 0;
    }

    public boolean actualizarTipo(Tipo tp) {
        SQLiteDatabase db = this.getWritableDatabase();
        boolean isSuccess = false;

        try {
            ContentValues values = new ContentValues();
            values.put("nombre", tp.getNombre());

            String selection = "idTipo = ?";
            String[] selectionArgs = {String.valueOf(tp.getIdTipo())};

            isSuccess = db.update("Tipo", values, selection, selectionArgs) > 0;
        } catch (Exception e) {
            System.err.println("Ha ocurrido un error -> actualizarTipo(): "+e.getMessage());
        } finally {
            db.close();
        }

        return isSuccess;
    }

    public Tipo getTipo(int idTipo) {
        SQLiteDatabase db = this.getReadableDatabase();

        String[] projection = {"idTipo", "nombre"};
        String[] selectionArgs = {String.valueOf(idTipo)};
        String selection = "idTipo = ?";

        Cursor c = db.query("Tipo", projection, selection, selectionArgs, null, null, null);
        List<Tipo> lista = new ArrayList<>();

        while (c.moveToNext()) {
            Tipo tp = new Tipo();
            tp.setIdTipo(c.getInt(c.getColumnIndexOrThrow("idTipo")));
            tp.setNombre((c.getString(c.getColumnIndexOrThrow("nombre"))));
            lista.add(tp);
        }
        c.close();

        if (!lista.isEmpty()) return lista.get(0);

        return null;
    }


    private static final String SQL_CREATE_TIEMPO =
            """
            CREATE TABLE Tiempo (
                idTiempo INTEGER PRIMARY KEY,
                setSeconds INTEGER,
                updatedSeconds INTEGER,
                rest INTEGER,
                idPomodoro INTEGER,
                FOREIGN KEY(idPomodoro) REFERENCES Pomodoro(idPomodoro) ON DELETE CASCADE
            );
            """;
    private static final String SQL_DELETE_TIEMPO = "DROP TABLE IF EXISTS Tiempo";

    public boolean insertarTiempo(Tiempo tm) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("setSeconds", tm.getSetSeconds());
        values.put("updatedSeconds", tm.getUpdatedSeconds());
        values.put("rest", tm.isRest()?1:0);
        values.put("idPomodoro", tm.getIdPomodoro());

        return db.insert("Tiempo", null, values) > 0;
    }

    public List<Tiempo> getTiempos(int idPomodoro) {
        SQLiteDatabase db = this.getReadableDatabase();

        String[] projection = {"idTiempo", "setSeconds", "updatedSeconds", "rest"};
        String selection = "idPomodoro = ?";
        String[] selectionArgs = {String.valueOf(idPomodoro)};
        Cursor c = db.query("Tiempo", projection, selection, selectionArgs, null, null, null);

        List<Tiempo> list = new ArrayList<>();

        while (c.moveToNext()) {
            Tiempo tm = new Tiempo();
            tm.setIdTiempo(c.getInt(c.getColumnIndexOrThrow("idTiempo")));
            tm.setSetSeconds(c.getInt(c.getColumnIndexOrThrow("setSeconds")));
            tm.setUpdatedSeconds(c.getInt(c.getColumnIndexOrThrow("updatedSeconds")));
            tm.setRest(c.getInt(c.getColumnIndexOrThrow("rest"))==1);
            tm.setIdPomodoro(idPomodoro);
            list.add(tm);
        }
        c.close();

        return list;
    }

    public boolean deleteTiempo(int idTiempo) {
        SQLiteDatabase db = this.getWritableDatabase();
        String selection = "idTiempo = ?";
        String[] selectionArgs = {String.valueOf(idTiempo)};
        return db.delete("Tiempo", selection, selectionArgs) > 0;
    }

    public boolean actualizarTiempo(Tiempo tm) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("setSeconds", tm.getSetSeconds());
        values.put("updatedSeconds", tm.getUpdatedSeconds());
        values.put("rest", tm.isRest()?1:0);
        values.put("idPomodoro", tm.getIdPomodoro());

        String selection = "idTiempo = ?";

        String[] selectionArgs = {String.valueOf(tm.getIdTiempo())};

        return db.update("Tiempo", values, selection, selectionArgs) > 0;
    }


    private static final String SQL_CREATE_POSITION =
            """
            CREATE TABLE Position (
                idPomodoro INTEGER PRIMARY KEY,
                position INTEGER,
                FOREIGN KEY(idPomodoro) REFERENCES Pomodoro(idPomodoro)
            );
            """;

    private static final String SQL_DELETE_POSITION = "DROP TABLE IF EXISTS Position";

    public boolean actualizarOrInsertPosition(int idPomodoro, int position) {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = null;
        boolean result;

        try {
            String[] selectionArgs = {String.valueOf(idPomodoro)};
            cursor = db.query("Position", null, "idPomodoro = ?", selectionArgs, null, null, null);

            ContentValues values = new ContentValues();
            values.put("position", position);

            if (cursor.moveToFirst()) {
                result = db.update("Position", values, "idPomodoro = ?", selectionArgs) > 0;
            } else {
                values.put("idPomodoro", idPomodoro);
                result = db.insert("Position", null, values) > 0;
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }

        return result;
    }

    public int getPosition(int idPomodoro) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = null;
        int position = 0;

        try {
            String[] projection = {"position"};
            String selection = "idPomodoro = ?";
            String[] selectionArgs = {String.valueOf(idPomodoro)};

            c = db.query("Position", projection, selection, selectionArgs, null, null, null);

            if (c != null && c.moveToFirst()) {
                position = c.getInt(c.getColumnIndexOrThrow("position"));
            }
        } finally {
            if (c != null) {
                c.close();
            }
        }

        return position;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_AGENDA);
        db.execSQL(SQL_CREATE_EVENTO);
        db.execSQL(SQL_CREATE_NOTA);
        db.execSQL(SQL_CREATE_TIPO);
        db.execSQL(SQL_CREATE_CATEGORIA);
        db.execSQL(SQL_CREATE_HORARIO);
        db.execSQL(SQL_CREATE_HORA);
        db.execSQL(SQL_CREATE_POMODORO);
        db.execSQL(SQL_CREATE_TIEMPO);
        db.execSQL(SQL_CREATE_POSITION);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        db.execSQL(SQL_DELETE_AGENDA);
        db.execSQL(SQL_DELETE_HORA);
        db.execSQL(SQL_DELETE_HORARIO);
        db.execSQL(SQL_DELETE_POMODORO);
        db.execSQL(SQL_DELETE_CATEGORIA);
        db.execSQL(SQL_DELETE_NOTA);
        db.execSQL(SQL_DELETE_EVENTO);
        db.execSQL(SQL_DELETE_TIPO);
        db.execSQL(SQL_DELETE_TIEMPO);
        db.execSQL(SQL_DELETE_POSITION);
        onCreate(db);
    }
}
