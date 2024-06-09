package com.jmormar.opentasker;

import static com.jmormar.opentasker.util.Constants.LLAVE_PRIMERA_INSERCION;
import static com.jmormar.opentasker.util.Constants.NOMBRE_PREFERENCIAS;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Pair;
import android.view.Gravity;
import android.view.MenuItem;
import android.widget.ImageView;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.navigation.NavigationView;
import com.jmormar.opentasker.fragments.AjustesFragment;
import com.jmormar.opentasker.fragments.EventosFragment;
import com.jmormar.opentasker.fragments.HomeFragment;
import com.jmormar.opentasker.fragments.HorarioFragment;
import com.jmormar.opentasker.fragments.NotasFragment;
import com.jmormar.opentasker.fragments.PomodoroFragment;
import com.jmormar.opentasker.onboarding.LogoScreenActivity;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, AjustesFragment.RemoteRecreate {

    private DrawerLayout drawerLayout;
    private Map<Integer, Pair<String, Fragment>> fragmentMap;
    private ImageView toolbarAdd;
    private Toolbar toolbar;
    private final OnBackPressedCallback onBackPressedCallback=new OnBackPressedCallback(true) {
        @Override
        public void handleOnBackPressed(){atras();}
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if(!comprobarPrimerasInserciones()){
            Intent intent = new Intent(this, LogoScreenActivity.class);
            startActivity(intent);
            confirmarPrimerasInserciones();
            finish();
        }

        fillFragmentoMap();

        getOnBackPressedDispatcher().addCallback(this, onBackPressedCallback);

        this.toolbar=findViewById(R.id.toolbar);
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

        getSupportFragmentManager().addOnBackStackChangedListener(this::handleFragmentChange);

        handleFragmentChange();
    }

    private void handleFragmentChange() {
        Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.home_content);
        if (currentFragment instanceof HorarioFragment) {
            if (toolbarAdd == null) {
                toolbarAdd = new ImageView(this);
                toolbarAdd.setImageResource(R.drawable.ic_add_white);
                toolbarAdd.setContentDescription(getString(R.string.add_hora));
                toolbarAdd.setPadding(16, 16, 16, 16);
                toolbarAdd.setOnClickListener(v -> ((HorarioFragment) currentFragment).addNewHora());
                Toolbar.LayoutParams params = new Toolbar.LayoutParams(
                        Toolbar.LayoutParams.WRAP_CONTENT,
                        Toolbar.LayoutParams.WRAP_CONTENT,
                        Gravity.END
                );
                toolbar.addView(toolbarAdd, params);
            }
        } else {
            if (toolbarAdd != null) {
                toolbar.removeView(toolbarAdd);
                toolbarAdd = null;
            }
        }

        if (currentFragment instanceof AjustesFragment) {
            ((AjustesFragment) currentFragment).setRemoteRecreate(this);
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
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            Fragment currentFragment = fragmentManager.findFragmentById(R.id.home_content);

            if (currentFragment != null && currentFragment.getClass().equals(fragmentPair.second.getClass())) return;

            fragmentTransaction.replace(R.id.home_content, fragmentPair.second);

            fragmentTransaction.addToBackStack(fragmentPair.first);

            fragmentTransaction.commit();
            setTitle(fragmentPair.first);
        }
    }

    private boolean comprobarPrimerasInserciones(){
        SharedPreferences prefs = getSharedPreferences(NOMBRE_PREFERENCIAS, Context.MODE_PRIVATE);
        return prefs.getBoolean(LLAVE_PRIMERA_INSERCION, false);
    }

    private void confirmarPrimerasInserciones() {
        SharedPreferences prefs = getSharedPreferences(NOMBRE_PREFERENCIAS, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean(LLAVE_PRIMERA_INSERCION, true);
        editor.apply();
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("currentFragment", getCurrentFragmentId());
    }

    private int getCurrentFragmentId() {
        Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.home_content);
        if (currentFragment instanceof HomeFragment) return 1;
        if (currentFragment instanceof HorarioFragment) return 2;
        if (currentFragment instanceof PomodoroFragment) return 3;
        if (currentFragment instanceof NotasFragment) return 4;
        if (currentFragment instanceof EventosFragment) return 5;
        if (currentFragment instanceof AjustesFragment) return 6;
        return -1;
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
    super.onRestoreInstanceState(savedInstanceState);
        int currentFragmentId = savedInstanceState.getInt("currentFragment", -1);
        if (currentFragmentId != -1) {
            mostrarFragmento(currentFragmentId);
        }
    }

    @Override
    public void doRecreate() {
        recreate();
    }
}