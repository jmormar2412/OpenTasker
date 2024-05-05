package com.jmormar.opentasker.objectmodifiers;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import com.jmormar.opentasker.R;
import com.jmormar.opentasker.models.Categoria;
import com.jmormar.opentasker.models.Evento;
import com.jmormar.opentasker.models.Tipo;
import com.jmormar.opentasker.util.DBHelper;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ModifyEventosActivity extends AppCompatActivity {

    private int idEvento, idAgenda;
    private DBHelper helper;
    private List<Integer> posicionesCategoria, posicionesTipo;
    private DatePickerDialog datePickerFecha;
    private EditText etFecha;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_modify_eventos);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        if(helper==null) helper = DBHelper.getInstance(this);

        String fechaString;
        if(savedInstanceState!=null){
            fechaString = savedInstanceState.getString("sfecha","");
        }
        else{
            fechaString = "";
        }

        @SuppressLint("SimpleDateFormat") SimpleDateFormat dateFormatter = new SimpleDateFormat("dd/MM/yyyy");
        Date fecha;
        if(fechaString.isEmpty()){
            fecha=new Date();
        }
        else {
            try {
                fecha = dateFormatter.parse(fechaString);
            } catch (ParseException e) {
                System.err.println("Error al leer la fecha: NuevoEvento -> onCreate()");
                fecha = new Date();
            }
        }

        prepararFecha(fecha);
        populateSpinners();
        loadData();
    }

    private void prepararFecha(Date fecha) {
        if(etFecha==null) {
            etFecha = findViewById(R.id.et_modifyevento_fecha);
        }
        etFecha.setOnFocusChangeListener((v, hasFocus) -> {
            if(hasFocus) {
                datePickerFecha.show();
            }
            v.clearFocus();
        });
        Calendar newCalendar = Calendar.getInstance();
        newCalendar.setTime(fecha);
        datePickerFecha = new DatePickerDialog(this, (view, year, monthOfYear, dayOfMonth) -> {
            Calendar newDate = Calendar.getInstance();
            newDate.set(year, monthOfYear, dayOfMonth);
            @SuppressLint("SimpleDateFormat") SimpleDateFormat dateFormatter = new SimpleDateFormat("dd/MM/yyyy");
            etFecha.setText(dateFormatter.format(newDate.getTime()));
        },newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH),
                newCalendar.get(Calendar.DAY_OF_MONTH));
    }

    private void populateSpinners() {
        posicionesCategoria = new ArrayList<>();

        Spinner spinnerCategorias = findViewById(R.id.sp_modifyevento_categoria);

        List<Categoria> categorias = helper.getCategorias();
        ArrayList<String> categoriasStrings = new ArrayList<>();

        categorias.forEach(e -> {
            categoriasStrings.add(e.getNombre());
            posicionesCategoria.add(e.getIdCategoria());
        });

        ArrayAdapter<String> adcategorias = new ArrayAdapter<>(this,android.R.layout.simple_spinner_item,
                categoriasStrings);
        adcategorias.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCategorias.setAdapter(adcategorias);

        posicionesTipo = new ArrayList<>();

        Spinner spinnerTipos = findViewById(R.id.sp_modifyevento_tipo);

        List<Tipo> tipos = helper.getTipos();
        ArrayList<String> tiposStrings = new ArrayList<>();

        tipos.forEach(e -> {
            tiposStrings.add(e.getNombre());
            posicionesTipo.add(e.getIdTipo());
        });

        ArrayAdapter<String> adtipos = new ArrayAdapter<>(this,android.R.layout.simple_spinner_item,
                tiposStrings);
        adtipos.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerTipos.setAdapter(adtipos);
    }

    private void loadData() {
        Intent intent = getIntent();
        int id = intent.getIntExtra("id", -1);

        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy", new Locale("es_ES"));

        if(id == -1){
            System.err.println("No se ha recuperado el id -> loadData() en ModifyEventos");
            return;
        }

        if(helper == null) helper = DBHelper.getInstance(this);
        Evento evento = helper.getEvento(id);

        this.idEvento = evento.getIdEvento();
        this.idAgenda = evento.getIdAgenda();

        EditText nombre = findViewById(R.id.et_modifyevento_nombre);
        Spinner spinnerCategoria = findViewById(R.id.sp_modifyevento_categoria);
        Spinner spinnerTipo = findViewById(R.id.sp_modifyevento_tipo);
        EditText fecha = findViewById(R.id.et_modifyevento_fecha);

        int positionTipo = posicionesTipo.indexOf(evento.getIdTipo());
        int positionCategoria = posicionesTipo.indexOf(evento.getIdCategoria());

        nombre.setText(evento.getNombre());
        spinnerCategoria.setSelection(positionCategoria);
        spinnerTipo.setSelection(positionTipo);
        fecha.setText(formatter.format(evento.getFecha()));
    }

    public void aceptar(View view) {
        EditText nombre = findViewById(R.id.et_modifyevento_nombre);
        Spinner spinnerCategoria = findViewById(R.id.sp_modifyevento_categoria);
        Spinner spinnerTipo = findViewById(R.id.sp_modifyevento_tipo);
        EditText fecha = findViewById(R.id.et_modifyevento_fecha);
        Button aceptar = findViewById(R.id.bt_modifyevento_aceptar);

        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy", new Locale("es_ES"));

        if(nombre.getText().toString().isEmpty()){
            nombre.setError("No has rellenado este campo");
            return;
        }

        if(fecha.getText().toString().isEmpty()){
            fecha.setError("No has rellenado este campo");
            return;
        }

        Evento evento = new Evento();
        evento.setIdEvento(idEvento);
        evento.setNombre(nombre.getText().toString());

        evento.setIdCategoria(posicionesCategoria.get(spinnerCategoria.getSelectedItemPosition()));

        Log.d("TIPOSELECCIONADO: ", String.valueOf(spinnerTipo.getSelectedItemPosition()));
        Log.d("POSICIONESTIPO: ", String.valueOf(posicionesTipo));
        evento.setIdTipo(posicionesTipo.get(spinnerTipo.getSelectedItemPosition()));
        evento.setIdAgenda(idAgenda);
        try{
            evento.setFecha(formatter.parse(fecha.getText().toString()));
        } catch (ParseException e) {
            System.err.println("No se ha reconocido la fecha -> aceptar() en ModifyEventos");
            return;
        }

        boolean insertado;
        insertado = helper.actualizarEvento(evento);
        if(insertado){
            finish();
        } else{
            aceptar.setEnabled(true);
            aceptar.setClickable(true);
            showError("error.IOException");
        }
    }

    private void showError(String error) {
        String message;
        Resources res = getResources();
        int duration;
        if(error.equals("error.IOException")){
            duration = Toast.LENGTH_LONG;
            message=res.getString(R.string.error_bd);
        }
        else {
            duration = Toast.LENGTH_SHORT;
            message = res.getString(R.string.error_unknown);
        }
        Context context = this.getApplicationContext();
        Toast toast = Toast.makeText(context, message, duration);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();
    }
}