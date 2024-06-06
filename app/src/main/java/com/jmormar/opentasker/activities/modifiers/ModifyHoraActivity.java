package com.jmormar.opentasker.activities.modifiers;

import android.app.TimePickerDialog;
import android.os.Bundle;
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
import com.jmormar.opentasker.models.Hora;
import com.jmormar.opentasker.util.DBHelper;

import java.time.Duration;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class ModifyHoraActivity extends AppCompatActivity {

    private int idHora;
    private Spinner spDias, spCategorias;
    private DBHelper helper;
    private Button btGuardar, btBorrar;
    private EditText etHoraInicio, etHoraFin, currentEditText;
    private TimePickerDialog timePickerDialog;
    private List<Integer> posicionesCategorias;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_modify_hora);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        this.idHora = getIntent().getIntExtra("idHora", -1);
        assert idHora != -1 : "idHora no encontrado";

        setElements();
        populateSpinners();
        prepareTimePicker();
        loadData();
        btGuardar.setOnClickListener(v -> guardar());
        btBorrar.setOnClickListener(v -> borrar());
    }

    private void borrar() {
        assert helper.deleteHora(idHora) : "Error al borrar hora";
        finish();
    }

    private void loadData() {
        Hora hora = helper.getHora(idHora);
        assert hora != null : "Hora no encontrada";

        spDias.setSelection(hora.getDiaSemana());
        etHoraInicio.setText(hora.getTiempoInicio().format(DateTimeFormatter.ofPattern("HH:mm")));
        etHoraFin.setText(hora.getTiempoInicio().plus(hora.getTotalTiempo()).format(DateTimeFormatter.ofPattern("HH:mm")));
        spCategorias.setSelection(posicionesCategorias.indexOf(posicionesCategorias.indexOf(hora.getIdCategoria())));
    }

    private void setElements() {
        this.spDias = findViewById(R.id.sp_diasemana);
        this.helper = DBHelper.getInstance(this);
        this.btGuardar = findViewById(R.id.bt_guardar);

        this.etHoraInicio = findViewById(R.id.et_horainicial);
        this.etHoraFin = findViewById(R.id.et_horafinal);

        this.spCategorias = findViewById(R.id.sp_categorias);
        this.btBorrar = findViewById(R.id.bt_borrar);
    }

    private void prepareTimePicker() {
        Calendar calendar = Calendar.getInstance();

        timePickerDialog = new TimePickerDialog(this, (view, hourOfDay, minute) -> {
            String tiempoSeleccionado = String.format(Locale.getDefault(), "%02d:%02d", hourOfDay, minute);
            currentEditText.setText(tiempoSeleccionado);
        }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), true);

        etHoraInicio.setOnFocusChangeListener((v, hasFocus) -> {
            if(hasFocus){
                String time = etHoraInicio.getText().toString();
                LocalTime selectedTime = time.isEmpty() ? LocalTime.now() : LocalTime.parse(time);
                timePickerDialog.updateTime(selectedTime.getHour(), selectedTime.getMinute());
                timePickerDialog.show();
                currentEditText = etHoraInicio;
            }
            v.clearFocus();
        });

        etHoraFin.setOnFocusChangeListener((v, hasFocus) -> {
            if(hasFocus){
                String time = etHoraFin.getText().toString();
                LocalTime selectedTime = time.isEmpty() ? LocalTime.now() : LocalTime.parse(time);
                timePickerDialog.updateTime(selectedTime.getHour(), selectedTime.getMinute());
                timePickerDialog.show();
                currentEditText = etHoraFin;
            }
            v.clearFocus();
        });
    }

    private void populateSpinners() {
        List<String> dias = Arrays.asList(getResources().getStringArray(R.array.dias_semana));
        ArrayAdapter<String> adapterDias = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, dias);
        adapterDias.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        this.spDias.setAdapter(adapterDias);

        List<Categoria> categorias = helper.getCategorias();
        List<String> categoriasStrings = new ArrayList<>();
        this.posicionesCategorias = new ArrayList<>();

        categorias.forEach(c -> {
            categoriasStrings.add(c.getNombre());
            posicionesCategorias.add(c.getIdCategoria());
        });

        ArrayAdapter<String> adapterCategorias = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, categoriasStrings);
        adapterCategorias.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        this.spCategorias.setAdapter(adapterCategorias);
    }

    private void guardar(){
        LocalTime horaInicio, horaFinal;

        if(etHoraInicio.getText().toString().isEmpty()){
            Toast.makeText(this, "Debe ingresar una hora de inicio", Toast.LENGTH_SHORT).show();
            etHoraInicio.setError("");
            return;
        }
        if(etHoraFin.getText().toString().isEmpty()){
            Toast.makeText(this, "Debe ingresar una hora de fin", Toast.LENGTH_SHORT).show();
            etHoraFin.setError("");
            return;
        }

        horaInicio = LocalTime.parse(etHoraInicio.getText().toString());
        horaFinal = LocalTime.parse(etHoraFin.getText().toString());

        if(horaInicio.isAfter(LocalTime.of(22, 59))){
            Toast.makeText(this, "La hora de inicio debe ser menor a las 11:00", Toast.LENGTH_SHORT).show();
            etHoraInicio.setError("");
            return;
        }
        if(horaFinal.isAfter(LocalTime.of(22, 59))){
            Toast.makeText(this, "La hora de fin debe ser menor a las 11:00", Toast.LENGTH_SHORT).show();
            etHoraFin.setError("");
            return;
        }

        if(horaInicio.isAfter(horaFinal)){
            Toast.makeText(this, "La hora de inicio debe ser menor a la hora de fin", Toast.LENGTH_SHORT).show();
            etHoraFin.setError("");
            return;
        }

        int idCategoria = posicionesCategorias.get(spCategorias.getSelectedItemPosition());
        int idHorario = helper.getHorario().getIdHorario();
        Duration duration = Duration.of(ChronoUnit.MINUTES.between(horaInicio, horaFinal), ChronoUnit.MINUTES);
        int diaSeleccionado = spDias.getSelectedItemPosition();

        List<Hora> horasEnElDia = helper.getHorasByDayAndHorario(diaSeleccionado);

        // Check for overlap with existing Horas
        for (Hora hora : horasEnElDia) {
            if (horaInicio.isAfter(hora.getTiempoInicio()) && horaInicio.isBefore(hora.getTiempoInicio().plus(hora.getTotalTiempo()))) {
                Toast.makeText(this, "La hora de inicio se superpone con otra Hora existente", Toast.LENGTH_SHORT).show();
                etHoraInicio.setError("");
                return;
            }
            if (horaFinal.isAfter(hora.getTiempoInicio()) && horaFinal.isBefore(hora.getTiempoInicio().plus(hora.getTotalTiempo()))) {
                Toast.makeText(this, "La hora de fin se superpone con otra Hora existente", Toast.LENGTH_SHORT).show();
                etHoraFin.setError("");
                return;
            }
        }

        Hora hora = new Hora();
        hora.setIdHora(this.idHora);
        hora.setIdCategoria(idCategoria);
        hora.setTiempoInicio(horaInicio);
        hora.setTotalTiempo(duration);
        hora.setDiaSemana(diaSeleccionado);
        hora.setIdHorario(idHorario);

        assert helper.actualizarHora(hora) : "Error al insertar hora";
        finish();
    }

}