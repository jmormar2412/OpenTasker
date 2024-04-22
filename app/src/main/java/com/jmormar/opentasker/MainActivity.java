package com.jmormar.opentasker;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import androidx.activity.OnBackPressedCallback;
import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;

import com.google.android.material.navigation.NavigationView;
import com.jmormar.opentasker.fragments.AjustesFragment;
import com.jmormar.opentasker.fragments.HomeFragment;
import com.jmormar.opentasker.fragments.HorarioFragment;
import com.jmormar.opentasker.fragments.NotasFragment;
import com.jmormar.opentasker.fragments.PomodoroFragment;
import com.jmormar.opentasker.models.Agenda;
import com.jmormar.opentasker.models.Categoria;
import com.jmormar.opentasker.models.Tipo;
import com.jmormar.opentasker.objectbuilders.NewEventoActivity;
import com.jmormar.opentasker.objectbuilders.NewNotaActivity;
import com.jmormar.opentasker.util.DBHelper;

import java.text.ParseException;
import java.text.SimpleDateFormat;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private DrawerLayout drawerLayout;
    private DBHelper helper;
    private static final String NOMBRE_PREFERENCIAS = "PreferenciasOpentasker";
    private static final String LLAVE_PRIMERA_INSERCION = "PrimeraInsercionHecha";
    //Implementar notasadapter para que se muestre como en el google keep.
    private ActivityResultLauncher<Intent> eventoResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        //cargarEventos();
                    }
                }
            });



    private ActivityResultLauncher<Intent> notaResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        //cargarNotas();
                    }
                }
            });

    private OnBackPressedCallback onBackPressedCallback=new OnBackPressedCallback(true) {
        @Override
        public void handleOnBackPressed(){atras();}
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getOnBackPressedDispatcher().addCallback(this, onBackPressedCallback);

        Toolbar toolbar=findViewById(R.id.toolbar);
        setSupportActionBar(findViewById(R.id.toolbar));

        drawerLayout = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle=new ActionBarDrawerToggle(
                this, drawerLayout, toolbar, R.string.abierto, R.string.cerrado
        );

        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView=findViewById(R.id.navigation_view);
        navigationView.setNavigationItemSelectedListener(this);

        MenuItem menuItem=navigationView.getMenu().getItem(0);
        onNavigationItemSelected(menuItem);
        menuItem.setChecked(true);

        if(!comprobarPrimerasInserciones()){
            realizarPrimerasInserciones();
            confirmarPrimerasInserciones();
        }
    }

    private void atras(){
        if(drawerLayout.isDrawerOpen(GravityCompat.START)){
            drawerLayout.closeDrawer(GravityCompat.START);
        } else{
            finish();
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int titleId = getTitulo(item);
        mostrarFragmento(titleId);
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    private int getTitulo(MenuItem item){
        int omenu=item.getItemId();
        if(omenu == R.id.nav_principal) return 1;
        if(omenu == R.id.nav_horario) return 2;
        if(omenu == R.id.nav_pomodoro) return 3;
        if(omenu == R.id.nav_notas) return 4;
        return 5;
    }

    private void mostrarFragmento(int fragmento){
        Fragment fragment;
        String titulo = switch (fragmento) {
            case 1 -> {
                fragment = HomeFragment.newInstance("", "");
                yield getString(R.string.inicio);
            }
            case 2 -> {
                fragment = HorarioFragment.newInstance("", "");
                yield getString(R.string.horario);
            }
            case 3 -> {
                fragment = PomodoroFragment.newInstance("", "");
                yield getString(R.string.pomodoro);
            }
            case 4 -> {
                fragment = NotasFragment.newInstance("", "");
                yield getString(R.string.notas);
            }
            default -> {
                fragment = AjustesFragment.newInstance("", "");
                yield getString(R.string.ajustes);
            }
        };

        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.home_content, fragment)
                .commit();
        setTitle(titulo);
    }

    public void irNuevoEvento(View view) {
        Intent myIntent = new Intent(this, NewEventoActivity.class);
        eventoResultLauncher.launch(myIntent);
    }

    public void irNuevaNota(View view) {
        Intent myIntent = new Intent(this, NewNotaActivity.class);
        notaResultLauncher.launch(myIntent);
    }

    //Comprobar primeras inserciones
    private boolean comprobarPrimerasInserciones(){
        SharedPreferences prefs = getSharedPreferences(NOMBRE_PREFERENCIAS, Context.MODE_PRIVATE);
        return prefs.getBoolean(LLAVE_PRIMERA_INSERCION, false);
    }

    private void realizarPrimerasInserciones(){
        if(helper == null) helper = DBHelper.getInstance(this);
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");

        Agenda agendaPrimaria = new Agenda();
        agendaPrimaria.setIdAgenda(0);
        agendaPrimaria.setWeekLength((byte) 5);
        agendaPrimaria.setBeginningDay((byte) 0);
        try {
            agendaPrimaria.setFechaInicio(dateFormat.parse("15/09/2001"));
            agendaPrimaria.setFechaFinal(dateFormat.parse("25/06/2002"));
        } catch (ParseException e) {
            System.err.println("No se ha podido leer la fecha -> realizarPrimerasInserciones()");
        }

        helper.insertarAgenda(agendaPrimaria);

        Tipo examen = new Tipo();
        examen.setNombre("Examen");

        Tipo tarea = new Tipo();
        tarea.setNombre("Tarea");

        Categoria mates = new Categoria();
        mates.setIdAgenda(0);
        mates.setNombre("Matemáticas");

        helper.insertarTipo(examen);
        helper.insertarTipo(tarea);

        helper.insertarCategoria(mates);
    }

    private void confirmarPrimerasInserciones() {
        SharedPreferences prefs = getSharedPreferences(NOMBRE_PREFERENCIAS, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean(LLAVE_PRIMERA_INSERCION, true);
        editor.apply();
    }
}