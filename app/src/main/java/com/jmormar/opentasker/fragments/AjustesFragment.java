package com.jmormar.opentasker.fragments;

import static com.jmormar.opentasker.util.Constants.NOMBRE_PREFERENCIAS;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.preference.EditTextPreference;
import androidx.preference.ListPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.SeekBarPreference;

import com.jmormar.opentasker.R;
import com.jmormar.opentasker.activities.CategoriaActivity;
import com.jmormar.opentasker.activities.TipoActivity;
import com.jmormar.opentasker.models.Agenda;
import com.jmormar.opentasker.util.DBHelper;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * A simple {@link Fragment} subclass.
 * Use {@link AjustesFragment#AjustesFragment()} to create
 * an instance of this fragment.
 */
public class AjustesFragment extends PreferenceFragmentCompat {
    private Context context;
    private AlertDialog.Builder builder;
    private DBHelper helper;
    private Agenda agenda;
    private DatePickerDialog datePickerFecha;
    private SimpleDateFormat formatter;
    private int weekLength, beginningDay;

    public AjustesFragment() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onCreatePreferences(@Nullable Bundle savedInstanceState, @Nullable String rootKey) {
        this.context = requireContext();
        this.helper = DBHelper.getInstance(this.context);
        this.agenda = helper.getAgenda();
        this.formatter = new SimpleDateFormat("dd/MM/yyyy", new Locale("es_ES"));

        setPreferencesFromResource(R.xml.pantalla_ajustes, rootKey);

        Preference categoriaPreference = findPreference("managecategories");
        assert categoriaPreference != null : "No se ha encontrado el Preference categoriaPreference";
        categoriaPreference.setOnPreferenceClickListener(preference -> {
            Intent myIntent = new Intent(this.context, CategoriaActivity.class);
            startActivity(myIntent);
            return true;
        });

        Preference tipoPreference = findPreference("managetypes");
        assert tipoPreference != null : "No se ha encontrado el Preference tipoPreference";
        tipoPreference.setOnPreferenceClickListener(preference -> {
            Intent myIntent = new Intent(this.context, TipoActivity.class);
            startActivity(myIntent);
            return true;
        });

        SeekBarPreference numDias = findPreference("weeklength");
        assert numDias != null : "No se ha encontrado el SeekBarPreference numDias";
        numDias.setValue(agenda.getWeekLength());
        numDias.setOnPreferenceChangeListener((preference, newValue) -> {
            this.weekLength = (int) newValue;
            return true;
        });

        Preference startDate = findPreference("startdate");
        assert startDate != null : "No se ha encontrado el EditTextPreference startDate";
        startDate.setOnPreferenceClickListener(preference -> {
            launchBuilder(1);
            return true;
        });

        Preference endingDate = findPreference("endingdate");
        assert endingDate != null : "No se ha encontrado el EditTextPreference endingDate";
        endingDate.setOnPreferenceClickListener(preference -> {
            launchBuilder(2);
            return true;
        });

        ListPreference beginningday = findPreference("beginningday");
        assert beginningday != null : "No se ha encontrado el ListPreference beginningday";
        beginningday.setEntries(R.array.dias_semana);
        beginningday.setEntryValues(R.array.dias_semana_values);
        beginningday.setSummary(getResources().getStringArray(R.array.dias_semana)[agenda.getBeginningDay()]);

        beginningday.setOnPreferenceChangeListener((preference, newValue) -> {
            String selectedValue = newValue.toString();
            int index = beginningday.findIndexOfValue(selectedValue);
            CharSequence[] entries = beginningday.getEntries();
            if (index >= 0) {
                preference.setSummary(entries[index]);
            }
            return true;
        });

        String currentValue = beginningday.getValue();
        if (currentValue != null) {
            int index = beginningday.findIndexOfValue(currentValue);
            if (index >= 0) {
                beginningday.setSummary(beginningday.getEntries()[index]);
            }
        }
    }

    private void prepararFecha(EditText etFecha, Date fecha){
        etFecha.setText(formatter.format(fecha));

        etFecha.setOnFocusChangeListener((v, hasFocus) -> {
            if(hasFocus) {
                datePickerFecha.show();
            }
            v.clearFocus();
        });
        Calendar newCalendar = Calendar.getInstance();
        newCalendar.setTime(fecha);
        datePickerFecha = new DatePickerDialog(this.context, (view, year, monthOfYear, dayOfMonth) -> {
            Calendar newDate = Calendar.getInstance();
            newDate.set(year, monthOfYear, dayOfMonth);
            @SuppressLint("SimpleDateFormat") SimpleDateFormat dateFormatter = new SimpleDateFormat("dd/MM/yyyy");
            etFecha.setText(dateFormatter.format(newDate.getTime()));
        },newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH),
                newCalendar.get(Calendar.DAY_OF_MONTH));
    }

    private void launchBuilder(int preferenceNumber) {
        this.builder = new AlertDialog.Builder(this.context);
        builder.setTitle("Fecha");

        final EditText input = new EditText(this.context);
        input.setInputType(InputType.TYPE_CLASS_TEXT);

        switch (preferenceNumber){
            case 1 -> prepararFecha(input, agenda.getFechaInicio());
            case 2 -> prepararFecha(input, agenda.getFechaFinal());
        }

        input.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {
                datePickerFecha.show();
            }
        });

        builder.setView(input);

        builder.setPositiveButton("OK", (dialog, which) ->{
            if(input.getText().toString().isEmpty()){
                input.setError("Campo requerido");
                return;
            }
            try {
                switch (preferenceNumber){
                    case 1 -> agenda.setFechaInicio(formatter.parse(input.getText().toString()));
                    case 2 -> agenda.setFechaFinal(formatter.parse(input.getText().toString()));
                }
            } catch (ParseException e) {
                System.err.println("Error al leer la fecha -> launchBuilder(): "+e.getMessage());
            }
        });
        builder.setNegativeButton("Cancelar", (dialog, which) ->{
            dialog.cancel();
            builder = null;
        } );

        builder.show();
    }

    @NonNull
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onStop() {
        super.onStop();
        SharedPreferences prefs = this.context.getSharedPreferences(NOMBRE_PREFERENCIAS, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        agenda.setWeekLength((byte) this.weekLength);
        assert helper.actualizarAgenda(agenda) : "No se ha podido actualizar la agenda";
        editor.apply();
    }
}