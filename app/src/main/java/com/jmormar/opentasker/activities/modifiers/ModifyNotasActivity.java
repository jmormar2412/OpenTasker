package com.jmormar.opentasker.activities.modifiers;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.jmormar.opentasker.R;
import com.jmormar.opentasker.models.Categoria;
import com.jmormar.opentasker.models.Nota;
import com.jmormar.opentasker.util.ColorManager;
import com.jmormar.opentasker.util.DBHelper;

import java.util.ArrayList;
import java.util.List;

import top.defaults.colorpicker.ColorWheelView;

public class ModifyNotasActivity extends AppCompatActivity {
    private int color;
    private Nota nota;
    private DBHelper helper;
    private List<Integer> posicionesCategoria;

    private EditText titulo, texto;
    private CheckBox checkBox, checkBoxInherit;
    private Spinner spinner;
    private Button escogerColor, btGuardar;

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

        setElements();
        setListeners();
        populateSpinner();
        loadData();
    }

    private void setElements() {
        this.titulo = findViewById(R.id.et_modifynota_title);
        this.checkBox = findViewById(R.id.cb_modifynota_includecategory);
        this.checkBoxInherit = findViewById(R.id.cb_modifynota_inheritcolor);

        this.spinner = findViewById(R.id.sp_modifynota_categoria);
        this.texto = findViewById(R.id.et_modifynota_text);
        this.escogerColor = findViewById(R.id.bt_modifynota_pickcolor);

        this.btGuardar = findViewById(R.id.bt_modifynota_guardar);
    }

    private void setListeners() {
        escogerColor.setOnClickListener(v ->  showColorPicker());

        checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if(isChecked){
                spinner.setVisibility(View.VISIBLE);
                checkBoxInherit.setVisibility(View.VISIBLE);
                return;
            }
            spinner.setVisibility(View.GONE);
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

        btGuardar.setOnClickListener(v -> aceptar());
    }

    private void showColorPicker() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getString(R.string.escoger_color));

        final ColorWheelView colorWheel = new ColorWheelView(this);
        colorWheel.setColor(nota.getColor(), false);
        builder.setView(colorWheel);

        builder.setPositiveButton(getString(R.string.escoger_color), (dialog, which) -> {
            this.color = ColorManager.darkenColor(colorWheel.getColor());
            this.escogerColor.setBackgroundColor(this.color);
        });
        builder.setNegativeButton(getString(R.string.cancelar), (dialog, which) -> dialog.cancel());

        builder.show();
    }

    private void loadData(){

        Intent intent = getIntent();
        int idNota = intent.getIntExtra("idNota", -1);

        assert idNota != -1 : getString(R.string.error_obteniendo_id);

        this.nota = helper.getNota(idNota);
        this.color = nota.getColor();

        titulo.setText(nota.getTitulo());
        if(nota.getIdCategoria()!=-1){
            checkBox.setChecked(true);
            spinner.setVisibility(View.VISIBLE);
            spinner.setSelection(posicionesCategoria.indexOf(nota.getIdCategoria()));
            Categoria categoria = helper.getCategoria(nota.getIdCategoria());

            if(categoria != null && nota.getColor() == categoria.getColor()){
                checkBoxInherit.setChecked(true);
            }
        }
        texto.setText(nota.getTexto());

        escogerColor.setBackgroundColor(ColorManager.darkenColor(nota.getColor()));
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
        if(titulo.getText().toString().isEmpty()){
            titulo.setError(getString(R.string.titulo_es_obligatorio));
            return;
        }

        this.nota.setTitulo(titulo.getText().toString());
        this.nota.setTexto(texto.getText().toString());
        this.nota.setColor(this.color);

        if(spinner.getVisibility() == View.VISIBLE){
            Integer idCategoria = posicionesCategoria.get(spinner.getSelectedItemPosition());
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
            btGuardar.setEnabled(true);
            btGuardar.setClickable(true);
            Toast.makeText(this, getString(R.string.error_guardando) + getString(R.string.nota), Toast.LENGTH_SHORT).show();
        }
    }
}