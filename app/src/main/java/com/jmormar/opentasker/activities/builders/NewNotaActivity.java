package com.jmormar.opentasker.activities.builders;

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

public class NewNotaActivity extends AppCompatActivity {

    private DBHelper helper;
    private List<Integer> posicionesCategoria;
    private int color;

    private Button btGuardar, btEscogerColor;
    private EditText titulo, texto;
    private Spinner spinnerCategoria;
    private CheckBox checkBoxIncludeCategory, checkBoxInherit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_nueva_nota);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        helper = DBHelper.getInstance(this);

        setElements();
        setListeners();
        populateSpinner();
    }

    private void setListeners() {
        btEscogerColor.setOnClickListener(v ->  showColorPicker());

        checkBoxIncludeCategory.setOnCheckedChangeListener((buttonView, isChecked) -> {
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
                btEscogerColor.setVisibility(View.GONE);
                return;
            }
            btEscogerColor.setVisibility(View.VISIBLE);
        }));

        btGuardar.setOnClickListener(v -> crearNota());
    }

    private void showColorPicker() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Escoger color");

        final ColorWheelView colorWheel = new ColorWheelView(this);
        builder.setView(colorWheel);

        builder.setPositiveButton("Escoger", (dialog, which) -> {
            this.color = colorWheel.getColor();
            this.btEscogerColor.setBackgroundColor(ColorManager.darkenColor(this.color));
        });
        builder.setNegativeButton("Cancelar", (dialog, which) -> dialog.cancel());

        builder.show();
    }

    private void setElements() {
        this.btGuardar = findViewById(R.id.bt_nuevanota_guardar);
        this.spinnerCategoria = findViewById(R.id.sp_nuevanota_categoria);
        this.titulo = findViewById(R.id.et_nuevanota_title);

        this.texto = findViewById(R.id.et_nuevanota_text);
        this.checkBoxInherit = findViewById(R.id.cb_nuevanota_inheritcolor);

        this.checkBoxIncludeCategory = findViewById(R.id.cb_nuevanota_includecategory);
        this.btEscogerColor = findViewById(R.id.bt_nuevanota_pickcolor);
    }

    private void populateSpinner(){
        posicionesCategoria = new ArrayList<>();

        Spinner scategorias = findViewById(R.id.sp_nuevanota_categoria);

        ArrayList<Categoria> cats = new ArrayList<>(helper.getCategorias());
        ArrayList<String> categorias = new ArrayList<>();

        cats.forEach(e -> {
            categorias.add(e.getNombre());
            posicionesCategoria.add(e.getIdCategoria());
        });

        ArrayAdapter<String> adcategorias = new ArrayAdapter<>(this,android.R.layout.simple_spinner_item,
                categorias);
        adcategorias.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        scategorias.setAdapter(adcategorias);
    }

    public void crearNota() {
        if(helper==null) helper = DBHelper.getInstance(this);

        Nota nota = new Nota();
        nota.setTitulo(titulo.getText().toString());
        nota.setTexto(texto.getText().toString());
        nota.setColor(ColorManager.darkenColor(this.color));

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
        insertado = helper.insertarNota(nota);
        if(insertado){
            setResult(RESULT_OK);
            finish();
        } else{
            btGuardar.setEnabled(true);
            btGuardar.setClickable(true);
            Toast.makeText(this, "Error al insertar nota", Toast.LENGTH_SHORT).show();
        }
    }
}