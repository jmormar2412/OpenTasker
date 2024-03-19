package com.jmormar.opentasker;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.jmormar.opentasker.entities.Categoria;
import com.jmormar.opentasker.entities.Evento;
import com.jmormar.opentasker.entities.Tipo;
import com.jmormar.opentasker.util.DBHelper;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class NuevoEvento extends AppCompatActivity {

    private DatePickerDialog dpdFecha;
    private EditText etFecha;
    private String sfecha;
    private DBHelper helper;
    private ArrayList<Integer> posicionesCategoria;
    private ArrayList<Integer> posicionesTipos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_nuevo_evento);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        if(helper==null) helper = DBHelper.getInstance(this);
        if(savedInstanceState!=null){
            sfecha = savedInstanceState.getString("sfecha","");
        }
        else{
            sfecha="";
        }
        @SuppressLint("SimpleDateFormat") SimpleDateFormat dateFormatter = new SimpleDateFormat("dd/MM/yyyy");
        Date fecha;
        if(sfecha.isEmpty()){
            fecha=new Date();
        }
        else {
            try {
                fecha = dateFormatter.parse(sfecha);
            } catch (ParseException e) {
                System.err.println("Error al leer la fecha: NuevoEvento -> onCreate()");
                fecha = new Date();
            }
        }
        prepararFecha(fecha);
        populateSpinners();
    }

    private void prepararFecha(Date inicio){
        if(etFecha==null) {
            etFecha = findViewById(R.id.et_nuevoevento_fecha);
        }
        etFecha.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus) {
                    dpdFecha.show();
                }
                v.clearFocus();
            }
        });
        Calendar newCalendar = Calendar.getInstance();
        newCalendar.setTime(inicio);
        dpdFecha = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                Calendar newDate = Calendar.getInstance();
                newDate.set(year, monthOfYear, dayOfMonth);
                @SuppressLint("SimpleDateFormat") SimpleDateFormat dateFormatter = new SimpleDateFormat("dd/MM/yyyy");
                etFecha.setText(dateFormatter.format(newDate.getTime()));
            }
        },newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH),
                newCalendar.get(Calendar.DAY_OF_MONTH));
    }

    private void populateSpinners(){
        posicionesCategoria = new ArrayList<>();
        posicionesTipos = new ArrayList<>();

        Spinner stipos = (Spinner) findViewById(R.id.sp_nuevoevento_tipo);
        Spinner scategorias = (Spinner) findViewById(R.id.sp_nuevoevento_categoria);

        //Para el de tipos
        ArrayList<Tipo> tips = new ArrayList<>(helper.getTipos());
        ArrayList<String> tipos = new ArrayList<>();
        for (Tipo tip : tips) {
            tipos.add(tip.getNombre());
            posicionesTipos.add(tip.getIdTipo());
        }
        ArrayAdapter<String> adtipos = new ArrayAdapter<>(this,android.R.layout.simple_spinner_item,
                tipos);
        adtipos.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        stipos.setAdapter(adtipos);

        //Para el de categorias
        ArrayList<Categoria> cats = new ArrayList<>(helper.getCategorias());
        ArrayList<String> categorias = new ArrayList<>();
        for (Categoria cat : cats) {
            categorias.add(cat.getNombre());
            posicionesCategoria.add(cat.getIdCategoria());
        }
        ArrayAdapter<String> adcategorias = new ArrayAdapter<>(this,android.R.layout.simple_spinner_item,
                categorias);
        adcategorias.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        scategorias.setAdapter(adcategorias);
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        if(etFecha==null){
            etFecha=findViewById(R.id.et_nuevoevento_fecha);
        }
        sfecha=etFecha.getText().toString();
        outState.putString("sfecha", sfecha);
    }

    public void aceptar(View view) {
        if(helper == null) helper=DBHelper.getInstance(this);

        EditText etNombre = findViewById(R.id.et_nuevoevento_nombre);
        if(etFecha==null) etFecha= findViewById(R.id.et_nuevoevento_fecha);
        Spinner sTipo = findViewById(R.id.sp_nuevoevento_tipo);
        Spinner sCategoria = findViewById(R.id.sp_nuevoevento_categoria);

        sfecha=etFecha.getText().toString();
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        Date fecha;
        try {
            fecha = dateFormat.parse(sfecha);
        } catch (ParseException e) {
            System.err.println("La fecha no ha sido introducida correctamente: NuevoEvento -> aceptar()");
            fecha = null;
        }
        String nombre = etNombre.getText().toString();
        String tipo = sTipo.getSelectedItem().toString();

        Button btAceptar= findViewById(R.id.bt_nuevoevento_aceptar);
        btAceptar.setEnabled(false);
        btAceptar.setClickable(false);

        if(helper!=null){
            Evento evento = new Evento();
            evento.setNombre(nombre);
            evento.setFecha(fecha);
            //Gracias profe eres el salvador de las vidas en Vietnam :)
            evento.setIdTipo(posicionesTipos.get(sTipo.getSelectedItemPosition()));
            evento.setIdCategoria(posicionesCategoria.get(sCategoria.getSelectedItemPosition()));

            boolean insertado;
            insertado = helper.insertarEvento(evento);
            if(insertado){
                setResult(RESULT_OK);
                finish();
            } else{
                btAceptar.setEnabled(true);
                btAceptar.setClickable(true);
                showError("error.IOException");
            }
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