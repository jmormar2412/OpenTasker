package com.jmormar.opentasker;

import static com.jmormar.opentasker.util.Constants.LLAVE_PRIMERA_INSERCION;
import static com.jmormar.opentasker.util.Constants.NOMBRE_PREFERENCIAS;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Pair;
import android.view.MenuItem;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;

import com.google.android.material.navigation.NavigationView;
import com.jmormar.opentasker.fragments.AjustesFragment;
import com.jmormar.opentasker.fragments.EventosFragment;
import com.jmormar.opentasker.fragments.HomeFragment;
import com.jmormar.opentasker.fragments.HorarioFragment;
import com.jmormar.opentasker.fragments.NotasFragment;
import com.jmormar.opentasker.fragments.PomodoroFragment;
import com.jmormar.opentasker.models.Agenda;
import com.jmormar.opentasker.models.Categoria;
import com.jmormar.opentasker.models.Tipo;
import com.jmormar.opentasker.util.DBHelper;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{

    private DrawerLayout drawerLayout;
    private DBHelper helper;
    private Map<Integer, Pair<String, Fragment>> fragmentMap;

    private final OnBackPressedCallback onBackPressedCallback=new OnBackPressedCallback(true) {
        @Override
        public void handleOnBackPressed(){atras();}
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        fillFragmentoMap();
        helper = DBHelper.getInstance(this);

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

    private void fillFragmentoMap() {
        fragmentMap = new HashMap<>();
        fragmentMap.put(1, new Pair<>(getString(R.string.inicio), HomeFragment.newInstance("","")));
        fragmentMap.put(2, new Pair<>(getString(R.string.horario), HorarioFragment.newInstance("","")));
        fragmentMap.put(3, new Pair<>(getString(R.string.pomodoro), PomodoroFragment.newInstance("","")));
        fragmentMap.put(4, new Pair<>(getString(R.string.notas), NotasFragment.newInstance("","")));
        fragmentMap.put(5, new Pair<>(getString(R.string.eventos), EventosFragment.newInstance("","")));
        fragmentMap.put(6, new Pair<>(getString(R.string.ajustes), new AjustesFragment()));
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
        drawerLayout.closeDrawer(GravityCompat.START);
        int titleId = getTitulo(item);
        mostrarFragmento(titleId);
        return true;
    }

    private int getTitulo(MenuItem item){
        int omenu=item.getItemId();
        if(omenu == R.id.nav_principal) return 1;
        if(omenu == R.id.nav_horario) return 2;
        if(omenu == R.id.nav_pomodoro) return 3;
        if(omenu == R.id.nav_notas) return 4;
        if(omenu == R.id.nav_eventos) return 5;
        if(omenu == R.id.nav_ajustes) return 6;
        return -1;
    }

    private void mostrarFragmento(int fragmento){
        Pair<String, Fragment> fragmentPair = fragmentMap.get(fragmento);

        if (fragmentPair != null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.home_content, fragmentPair.second)
                    .commit();
            setTitle(fragmentPair.first);
        }
    }

    //Comprobar primeras inserciones
    private boolean comprobarPrimerasInserciones(){
        SharedPreferences prefs = getSharedPreferences(NOMBRE_PREFERENCIAS, Context.MODE_PRIVATE);
        return prefs.getBoolean(LLAVE_PRIMERA_INSERCION, false);
    }

    private void realizarPrimerasInserciones(){
        if(helper == null) helper = DBHelper.getInstance(this);
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", new Locale("es_ES"));

        Agenda agendaPrimaria = new Agenda();
        agendaPrimaria.setNombre("Agenda Primaria");
        agendaPrimaria.setWeekLength((byte) 5);
        agendaPrimaria.setBeginningDay((byte) 0);
        try {
            agendaPrimaria.setFechaInicio(dateFormat.parse("15/09/2001"));
            agendaPrimaria.setFechaFinal(dateFormat.parse("25/06/2002"));
        } catch (ParseException e) {
            System.err.println("No se ha podido leer la fecha -> realizarPrimerasInserciones()");
        }

        assert helper.insertarAgenda(agendaPrimaria) : "No se ha podido insertar la agenda";
        int idAgenda = helper.getAgenda().getIdAgenda();

        Tipo examen = new Tipo();
        examen.setNombre("Examen");

        Tipo tarea = new Tipo();
        tarea.setNombre("Tarea");

        Categoria mates = new Categoria();
        mates.setIdAgenda(idAgenda);
        mates.setNombre("Matemáticas");


        assert helper.insertarTipo(examen) : "No se ha podido insertar el tipo examen";
        assert helper.insertarTipo(tarea) : "No se ha podido insertar el tipo tarea";

        assert helper.insertarCategoria(mates) : "No se ha podido insertar la categoría matemáticas";
    }

    private void confirmarPrimerasInserciones() {
        SharedPreferences prefs = getSharedPreferences(NOMBRE_PREFERENCIAS, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean(LLAVE_PRIMERA_INSERCION, true);
        editor.apply();
    }
}