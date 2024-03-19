package com.jmormar.opentasker;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;

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
import com.jmormar.opentasker.adapters.EventoAdapter;
import com.jmormar.opentasker.entities.Evento;
import com.jmormar.opentasker.entities.Nota;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private DrawerLayout drawerLayout;
    private ArrayList<Evento> eventos;
    private ArrayList<Nota> notas;
    //Implementar notasadapter para que se muestre como en el google keep.
    private EventoAdapter adapter;
    private ListView listView;

    private ActivityResultLauncher<Intent> eventoResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        cargarEventos();
                    }
                }
            });



    private ActivityResultLauncher<Intent> notaResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        cargarNotas();
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

        cargarEventos();
        cargarNotas();
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
                fragment = HomePage.newInstance("", "");
                yield getString(R.string.inicio);
            }
            case 2 -> {
                fragment = DisplayHorario.newInstance("", "");
                yield getString(R.string.horario);
            }
            case 3 -> {
                fragment = DisplayPomodoro.newInstance("", "");
                yield getString(R.string.pomodoro);
            }
            case 4 -> {
                fragment = Notas.newInstance("", "");
                yield getString(R.string.notas);
            }
            default -> {
                fragment = Ajustes.newInstance("", "");
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
        Intent myIntent = new Intent(this, NuevoEvento.class);
        eventoResultLauncher.launch(myIntent);
    }

    public void irNuevaNota(View view) {
        Intent myIntent = new Intent(this, NuevaNota.class);
        notaResultLauncher.launch(myIntent);
    }

    private void cargarEventos() {
    }

    private void cargarNotas() {

    }
}