package com.jmormar.opentasker.onboarding;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.jmormar.opentasker.R;
import com.jmormar.opentasker.models.Agenda;
import com.jmormar.opentasker.models.Horario;
import com.jmormar.opentasker.util.DBHelper;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class DatosAgendaActivity extends AppCompatActivity {

    private EditText fechaInicial, fechaFinal, currentEditText;
    private Spinner startingDay;
    private SeekBar seekBar;
    private Button next;
    private DatePickerDialog datePickerFecha;
    private final SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy", new Locale("es"));
    private TextView tvValorSb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_datos_agenda);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        setElements();
        prepararFecha();
        populateSpinner();
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                tvValorSb.setText(String.valueOf(progress));
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });
        next.setOnClickListener(v -> continuar());
    }

    private void continuar() {
        if(fechaInicial.getText().toString().isEmpty() || fechaFinal.getText().toString().isEmpty()){
            Toast.makeText(this, getString(R.string.fecha_es_obligatoria), Toast.LENGTH_SHORT).show();
            return;
        }

        Date fechaInicio, fechaFin;

        try{
            fechaInicio = formatter.parse(this.fechaInicial.getText().toString());
            fechaFin = formatter.parse(this.fechaFinal.getText().toString());
        } catch (Exception e){
            System.err.println("Error interno, no se puede leer la fecha: DatosAgenda -> continuar()");
            return;
        }

        assert fechaInicio != null : "Error interno, fechaInicio es null";
        assert fechaFin != null : "Error interno, fechaFin es null";

        if(fechaInicio.after(fechaFin)){
            Toast.makeText(this, getString(R.string.inicio_nomayorque_fin), Toast.LENGTH_SHORT).show();
            return;
        }

        Agenda agenda = new Agenda();
        agenda.setFechaInicio(fechaInicio);
        agenda.setFechaFinal(fechaFin);
        agenda.setBeginningDay((byte) this.startingDay.getSelectedItemPosition());
        agenda.setWeekLength((byte) this.seekBar.getProgress());

        DBHelper helper = DBHelper.getInstance(this);
        assert helper.insertarAgenda(agenda) : "Error interno, no se pudo insertar la agenda";

        Horario horario = new Horario();
        horario.setIdAgenda(helper.getAgenda().getIdAgenda());
        assert helper.insertarHorario(horario) : "Error interno, no se pudo insertar el horario";

        Intent intent = new Intent(this, DatosCategoriaActivity.class);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        finish();
    }

    private void populateSpinner() {
        List<String> dias = Arrays.asList(getResources().getStringArray(R.array.dias_semana));
        ArrayAdapter<String> adapterDias = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, dias);
        adapterDias.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        this.startingDay.setAdapter(adapterDias);
    }

    private void prepararFecha(){
        Calendar newCalendar = Calendar.getInstance();
        newCalendar.setTime(new Date());

        fechaInicial.setOnFocusChangeListener((v, hasFocus) -> {
            if(hasFocus) {
                datePickerFecha.show();
                currentEditText = fechaInicial;
            }
            v.clearFocus();
        });

        fechaFinal.setOnFocusChangeListener((v, hasFocus) -> {
            if(hasFocus) {
                datePickerFecha.show();
                currentEditText = fechaFinal;
            }
            v.clearFocus();
        });

        datePickerFecha = new DatePickerDialog(this, (view, year, monthOfYear, dayOfMonth) -> {
            Calendar newDate = Calendar.getInstance();
            newDate.set(year, monthOfYear, dayOfMonth);
            currentEditText.setText(formatter.format(newDate.getTime()));
        },newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH),
                newCalendar.get(Calendar.DAY_OF_MONTH));
    }

    private void setElements() {
        this.fechaInicial = findViewById(R.id.et_fecha_inicial);
        this.fechaFinal = findViewById(R.id.et_fecha_final);
        this.startingDay = findViewById(R.id.sp_dia_inicial);
        this.seekBar = findViewById(R.id.sb_duracion_semana);
        this.next = findViewById(R.id.bt_next);
        this.tvValorSb = findViewById(R.id.tv_valor_sb);
    }
}