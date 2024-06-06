package com.jmormar.opentasker.activities.modifiers;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
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
    private EditText etFecha, nombre;
    private Spinner spinnerCategoria, spinnerTipo;
    private Button aceptar;

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

        setElements();
        prepararFecha(fecha);
        populateCategorias();
        populateTipos();
        loadData();
    }

    private void setElements() {
        nombre = findViewById(R.id.et_modifyevento_nombre);
        spinnerCategoria = findViewById(R.id.sp_modifyevento_categoria);
        spinnerTipo = findViewById(R.id.sp_modifyevento_tipo);
        etFecha = findViewById(R.id.et_modifyevento_fecha);
        aceptar = findViewById(R.id.bt_modifyevento_aceptar);

    }

    private void prepararFecha(Date fecha) {
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

    private void populateTipos() {
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

    private void populateCategorias() {
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

        int positionTipo = posicionesTipo.indexOf(evento.getIdTipo());
        int positionCategoria = posicionesTipo.indexOf(evento.getIdCategoria());

        nombre.setText(evento.getNombre());
        spinnerCategoria.setSelection(positionCategoria);
        spinnerTipo.setSelection(positionTipo);
        etFecha.setText(formatter.format(evento.getFecha()));
    }

    public void aceptar(View view) {

        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy", new Locale("es_ES"));

        if(nombre.getText().toString().isEmpty()){
            nombre.setError("No has rellenado este campo");
            return;
        }

        if(etFecha.getText().toString().isEmpty()){
            etFecha.setError("No has rellenado este campo");
            return;
        }

        Evento evento = new Evento();
        evento.setIdEvento(idEvento);
        evento.setNombre(nombre.getText().toString());

        evento.setIdCategoria(posicionesCategoria.get(spinnerCategoria.getSelectedItemPosition()));

        evento.setIdTipo(posicionesTipo.get(spinnerTipo.getSelectedItemPosition()));
        evento.setIdAgenda(idAgenda);
        try{
            evento.setFecha(formatter.parse(etFecha.getText().toString()));
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
            Toast.makeText(this, "No se ha podido modificar el evento", Toast.LENGTH_SHORT).show();
        }
    }
}