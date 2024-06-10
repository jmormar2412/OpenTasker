package com.jmormar.opentasker.fragments;

import static com.jmormar.opentasker.util.Constants.NOMBRE_PREFERENCIAS;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.FragmentActivity;
import androidx.preference.ListPreference;
import androidx.preference.MultiSelectListPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.SeekBarPreference;

import com.jmormar.opentasker.R;
import com.jmormar.opentasker.activities.CategoriaActivity;
import com.jmormar.opentasker.activities.TipoActivity;
import com.jmormar.opentasker.models.Agenda;
import com.jmormar.opentasker.models.Evento;
import com.jmormar.opentasker.util.DBHelper;
import com.jmormar.opentasker.util.Scheduler;
import com.yariksoffice.lingver.Lingver;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import lombok.Setter;
import timber.log.Timber;


public class AjustesFragment extends PreferenceFragmentCompat {
    private SharedPreferences sharedPreferences;
    private Context context;
    private AlertDialog.Builder builder;
    private DBHelper helper;
    private Agenda agenda;
    private DatePickerDialog datePickerFecha;
    private SimpleDateFormat formatter;
    private int weekLength, beginningDay;
    private Set<String> notificationFrequencies;
    private Scheduler scheduler;
    private boolean notificationFrequencyChanged;
    @Setter
    private RemoteRecreate remoteRecreate;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    @SuppressWarnings("unchecked")
    public void onCreatePreferences(@Nullable Bundle savedInstanceState, @Nullable String rootKey) {
        this.context = requireContext();
        this.helper = DBHelper.getInstance(this.context);
        this.agenda = helper.getAgenda();
        this.formatter = new SimpleDateFormat("dd/MM/yyyy", new Locale("es"));
        this.sharedPreferences = context.getSharedPreferences(NOMBRE_PREFERENCIAS, Context.MODE_PRIVATE);
        this.scheduler = new Scheduler(context);

        setPreferencesFromResource(R.xml.pantalla_ajustes, rootKey);

        Preference categoriaPreference = findPreference("managecategories");
        assert categoriaPreference != null : getString(R.string.preference_no_encontrado) + " categoriaPreference";
        categoriaPreference.setOnPreferenceClickListener(preference -> {
            Intent myIntent = new Intent(this.context, CategoriaActivity.class);
            startActivity(myIntent);
            return true;
        });

        Preference tipoPreference = findPreference("managetypes");
        assert tipoPreference != null : getString(R.string.preference_no_encontrado) + " tipoPreference";
        tipoPreference.setOnPreferenceClickListener(preference -> {
            Intent myIntent = new Intent(this.context, TipoActivity.class);
            startActivity(myIntent);
            return true;
        });

        SeekBarPreference numDias = findPreference("weeklength");
        assert numDias != null : getString(R.string.preference_no_encontrado) + " numDias";
        this.weekLength = agenda.getWeekLength();
        numDias.setValue(this.weekLength);
        numDias.setOnPreferenceChangeListener((preference, newValue) -> {
            this.weekLength = (int) newValue;
            return true;
        });

        Preference startDate = findPreference("startdate");
        assert startDate != null : getString(R.string.preference_no_encontrado) + " startDate";
        startDate.setSummary(formatter.format(agenda.getFechaInicio()));
        startDate.setOnPreferenceClickListener(preference -> {
            launchBuilder(1, preference);
            return true;
        });

        Preference endingDate = findPreference("endingdate");
        assert endingDate != null : getString(R.string.preference_no_encontrado) + " endingDate";
        endingDate.setSummary(formatter.format(agenda.getFechaFinal()));
        endingDate.setOnPreferenceClickListener(preference -> {
            launchBuilder(2, preference);
            return true;
        });

        ListPreference beginningday = findPreference("beginningday");
        assert beginningday != null : getString(R.string.preference_no_encontrado) + " beginningday";
        beginningday.setEntries(R.array.dias_semana);
        beginningday.setEntryValues(R.array.dias_semana_values);
        this.beginningDay = agenda.getBeginningDay();
        beginningday.setSummary(getResources().getStringArray(R.array.dias_semana)[this.beginningDay]);


        beginningday.setOnPreferenceChangeListener((preference, newValue) -> {
            String selectedValue = newValue.toString();
            int index = beginningday.findIndexOfValue(selectedValue);
            CharSequence[] entries = beginningday.getEntries();
            if (index >= 0) {
                preference.setSummary(entries[index]);
            }
            this.beginningDay = index;
            return true;
        });

        String currentValue = beginningday.getValue();
        if (currentValue != null) {
            int index = beginningday.findIndexOfValue(currentValue);
            if (index >= 0) {
                beginningday.setSummary(beginningday.getEntries()[index]);
            }
        }

        MultiSelectListPreference notificaciones = findPreference("notificationfrequency");
        assert notificaciones != null : getString(R.string.preference_no_encontrado) + " notificaciones";
        notificaciones.setEntries(R.array.frecuencia_notifs);
        notificaciones.setEntryValues(R.array.frecuencia_notifs_values);

        notificationFrequencies = new HashSet<>(sharedPreferences.getStringSet("notificationfrequency", new HashSet<>()));
        notificaciones.setValues(notificationFrequencies);
        notificaciones.setSummary(Arrays.toString(notificationFrequencies.toArray()).replaceAll("[\\[\\]]", ""));

        notificaciones.setOnPreferenceChangeListener((preference, newValue) -> {
            notificationFrequencies = (Set<String>) newValue;
            notificaciones.setSummary(Arrays.toString(notificationFrequencies.toArray()).replaceAll("[\\[\\]]", ""));
            if(!notificationFrequencyChanged) notificationFrequencyChanged = true;
            return true;
        });

        ListPreference languagelocale = findPreference("language");
        assert languagelocale != null : getString(R.string.preference_no_encontrado) + " language";
        languagelocale.setEntries(R.array.lenguajes);
        languagelocale.setEntryValues(R.array.lenguajes_values);

        languagelocale.setValue(sharedPreferences.getString("language", "es"));

        languagelocale.setOnPreferenceChangeListener((preference, newValue) -> {
            String selectedValue = newValue.toString();
            Lingver.getInstance().setLocale(this.context, selectedValue);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("language", selectedValue);
            editor.apply();
            remoteRecreate.doRecreate();
            return true;
        });

        Preference aboutPreference = findPreference("about");
        assert aboutPreference != null : getString(R.string.preference_no_encontrado) + " aboutPreference";
        aboutPreference.setOnPreferenceClickListener(preference -> {
            NotaDialogFragment dialogFragment = new NotaDialogFragment();
            dialogFragment.setShowAbout(true);
            dialogFragment.show(((FragmentActivity) this.context).getSupportFragmentManager(), "Acerca de la aplicación");
            return true;
        });

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
            etFecha.setText(formatter.format(newDate.getTime()));
        },newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH),
                newCalendar.get(Calendar.DAY_OF_MONTH));
    }

    private void launchBuilder(int preferenceNumber, Preference preference) {
        this.builder = new AlertDialog.Builder(this.context);
        builder.setTitle(getString(R.string.fecha));

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

        builder.setPositiveButton(getString(R.string.aceptar), (dialog, which) ->{
            if(input.getText().toString().isEmpty()){
                input.setError(getString(R.string.fecha_es_obligatoria));
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
            preference.setSummary(input.getText());
        });
        builder.setNegativeButton(getString(R.string.cancelar), (dialog, which) ->{
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
        SharedPreferences.Editor editor = this.sharedPreferences.edit();
        editor.putStringSet("notificationfrequency", notificationFrequencies);
        editor.apply();

        if(notificationFrequencyChanged){
            List<Evento> eventos = helper.getEventos();
            eventos.forEach(e -> {
                scheduler.clearAllNotifications(e);
                scheduler.scheduleEventNotifications(e);
            });
        }

        agenda.setWeekLength((byte) this.weekLength);
        agenda.setBeginningDay((byte) this.beginningDay);
        assert helper.actualizarAgenda(agenda) : getString(R.string.error_modificando) + getString(R.string.agenda_minuscula);

        Timber.i("%s%s", getString(R.string.exito_modificando), getString(R.string.agenda_minuscula));
    }

    public interface RemoteRecreate{
        void doRecreate();
    }

}