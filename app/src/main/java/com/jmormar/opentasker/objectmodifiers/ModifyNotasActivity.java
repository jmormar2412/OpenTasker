package com.jmormar.opentasker.objectmodifiers;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.jmormar.opentasker.R;
import com.jmormar.opentasker.models.Categoria;
import com.jmormar.opentasker.models.Nota;
import com.jmormar.opentasker.util.DBHelper;

import java.util.ArrayList;
import java.util.List;

public class ModifyNotasActivity extends AppCompatActivity {
    private int idNota;
    private DBHelper helper;
    private List<Integer> posicionesCategoria;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_modify_notas);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        helper = DBHelper.getInstance(this);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        addListeners();
        loadData();
        populateSpinner();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_opciones_nota, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.menu_delete) {
            borrarNota();
            return true;
        }
        return false;
    }

    private void loadData(){

        Intent intent = getIntent();
        int position = intent.getIntExtra("position", -1);

        if(position == -1){
            System.err.println("No se ha recuperado la posición -> loadData() en ModifyNotas");
            return;
        }

        if(helper == null) helper = DBHelper.getInstance(this);
        Nota nota = helper.getNotas().get(position);

        this.idNota = nota.getIdNota();

        EditText titulo = findViewById(R.id.et_modifynota_title);
        CheckBox checkBox = findViewById(R.id.cb_modifynota_includecategory);
        Spinner spinner = findViewById(R.id.sp_modifynota_categoria);
        EditText texto = findViewById(R.id.et_modifynota_text);

        titulo.setText(nota.getTitulo());
        if(nota.getIdCategoria()!=-1){
            checkBox.setChecked(true);
            spinner.setVisibility(View.VISIBLE);
        }
        texto.setText(nota.getTexto());

    }

    private void populateSpinner(){
        posicionesCategoria = new ArrayList<>();

        Spinner spinnerCategorias = (Spinner) findViewById(R.id.sp_modifynota_categoria);

        ArrayList<Categoria> categorias = new ArrayList<>(helper.getCategorias());
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

    private void addListeners(){

        CheckBox checkBox = findViewById(R.id.cb_modifynota_includecategory);
        Button button = findViewById(R.id.bt_modifynota_guardar);
        Spinner spinnerCategoria = findViewById(R.id.sp_modifynota_categoria);

        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    spinnerCategoria.setVisibility(View.VISIBLE);
                    return;
                }
                spinnerCategoria.setVisibility(View.GONE);
            }
        });

        EditText titulo = findViewById(R.id.et_modifynota_title);
        EditText texto = findViewById(R.id.et_modifynota_text);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Nota nota = new Nota();
                nota.setIdNota(idNota);
                nota.setTitulo(titulo.getText().toString());
                nota.setTexto(texto.getText().toString());

                if(spinnerCategoria.getVisibility() == View.VISIBLE){
                    nota.setIdCategoria(posicionesCategoria.get(spinnerCategoria.getSelectedItemPosition()));
                } else{
                    nota.setIdCategoria(-1);
                }

                boolean insertado;
                insertado = helper.actualizarNota(nota);
                if(insertado){
                    finish();
                } else{
                    button.setEnabled(true);
                    button.setClickable(true);
                    showError("error.IOException");
                }
            }
        });
    }

    private void borrarNota(){
        helper.deleteNota(idNota);
        finish();
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