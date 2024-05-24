package com.jmormar.opentasker.activities.objectmodifiers;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
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
import com.jmormar.opentasker.adapters.NotaAdapter;
import com.jmormar.opentasker.models.Categoria;
import com.jmormar.opentasker.models.Nota;
import com.jmormar.opentasker.util.DBHelper;

import java.util.ArrayList;
import java.util.List;

import top.defaults.colorpicker.ColorPickerPopup;

public class ModifyNotasActivity extends AppCompatActivity {
    private int idNota, color;
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
        Button escogerColor = findViewById(R.id.bt_modifynota_pickcolor);

        escogerColor.setOnClickListener(v ->
                new ColorPickerPopup.Builder(ModifyNotasActivity.this)
                        .showIndicator(false)
                        .showValue(false)
                        .enableBrightness(false)
                        .enableAlpha(false)
                        .okTitle("Escoger")
                        .cancelTitle("Cancelar")
                        .build()
                        .show(v, new ColorPickerPopup.ColorPickerObserver() {
                            @Override
                            public void onColorPicked(int color) {
                                ModifyNotasActivity.this.color = color;
                                ModifyNotasActivity.this.findViewById(R.id.bt_modifynota_pickcolor).setBackgroundColor(NotaAdapter.NotaViewHolder.darkenColor(color));
                            }
                        }));

        CheckBox checkBox = findViewById(R.id.cb_modifynota_includecategory);
        Spinner spinnerCategoria = findViewById(R.id.sp_modifynota_categoria);
        CheckBox checkBoxInherit = findViewById(R.id.cb_modifynota_inheritcolor);
        checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if(isChecked){
                spinnerCategoria.setVisibility(View.VISIBLE);
                checkBoxInherit.setVisibility(View.VISIBLE);
                return;
            }
            spinnerCategoria.setVisibility(View.GONE);
            checkBoxInherit.setChecked(false);
            checkBoxInherit.setVisibility(View.GONE);
        });

        checkBoxInherit.setOnCheckedChangeListener(((buttonView, isChecked) -> {
            if(isChecked) {
                escogerColor.setVisibility(View.GONE);
                return;
            }
            escogerColor.setVisibility(View.VISIBLE);
        }));

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        aceptar();
        populateSpinner();
        loadData();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_opciones_nota, menu);
        MenuItem menuItem = menu.getItem(0);
        assert menuItem.getTitle() != null : "No se ha recuperado el título de la opción";
        SpannableString spannableString = new SpannableString(menuItem.getTitle().toString());
        spannableString.setSpan(new ForegroundColorSpan(Color.WHITE), 0, spannableString.length(), 0);
        menuItem.setTitle(spannableString);
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
        int idNota = intent.getIntExtra("idNota", -1);

        assert idNota != -1 : "No se ha recuperado el id de la nota";

        Nota nota = helper.getNota(idNota);

        this.idNota = nota.getIdNota();

        EditText titulo = findViewById(R.id.et_modifynota_title);
        CheckBox checkBox = findViewById(R.id.cb_modifynota_includecategory);
        CheckBox checkBoxInherit = findViewById(R.id.cb_modifynota_inheritcolor);
        Spinner spinner = findViewById(R.id.sp_modifynota_categoria);
        EditText texto = findViewById(R.id.et_modifynota_text);

        titulo.setText(nota.getTitulo());
        if(nota.getIdCategoria()!=-1){
            checkBox.setChecked(true);
            spinner.setVisibility(View.VISIBLE);
            spinner.setSelection(posicionesCategoria.indexOf(nota.getIdCategoria()));
            if(nota.getColor() == helper.getCategoria(nota.getIdCategoria()).getColor()){
                checkBoxInherit.setChecked(true);
            }
        }
        texto.setText(nota.getTexto());

    }

    private void populateSpinner(){
        posicionesCategoria = new ArrayList<>();

        Spinner spinnerCategorias = findViewById(R.id.sp_modifynota_categoria);

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

    private void aceptar() {
        Spinner spinnerCategoria = findViewById(R.id.sp_modifynota_categoria);
        Button button = findViewById(R.id.bt_modifynota_guardar);
        EditText titulo = findViewById(R.id.et_modifynota_title);
        EditText texto = findViewById(R.id.et_modifynota_text);
        CheckBox checkBoxInherit = findViewById(R.id.cb_modifynota_inheritcolor);

        button.setOnClickListener(v -> {
            Nota nota = new Nota();
            nota.setIdNota(idNota);
            nota.setTitulo(titulo.getText().toString());
            nota.setTexto(texto.getText().toString());
            nota.setColor(this.color);

            if(spinnerCategoria.getVisibility() == View.VISIBLE){
                Integer idCategoria = posicionesCategoria.get(spinnerCategoria.getSelectedItemPosition());
                nota.setIdCategoria(idCategoria);
                if(checkBoxInherit.isChecked()){
                    Categoria cat = helper.getCategoria(idCategoria);
                    nota.setColor(cat.getColor());
                }
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
                showError();
            }
        });
    }

    private void borrarNota(){
        helper.deleteNota(idNota);
        finish();
    }

    private void showError() {
        String message;
        Resources res = getResources();
        int duration;
        duration = Toast.LENGTH_LONG;
        message=res.getString(R.string.error_bd);
        Context context = this.getApplicationContext();
        Toast toast = Toast.makeText(context, message, duration);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();
    }
}