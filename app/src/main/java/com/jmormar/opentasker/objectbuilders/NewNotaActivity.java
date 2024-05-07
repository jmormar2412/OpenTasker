package com.jmormar.opentasker.objectbuilders;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
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

public class NewNotaActivity extends AppCompatActivity {

    private DBHelper helper;
    private List<Integer> posicionesCategoria;
    private int color;

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
        Button escogerColor = findViewById(R.id.bt_nuevanota_pickcolor);

        escogerColor.setOnClickListener(v ->
                new ColorPickerPopup.Builder(NewNotaActivity.this)
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
                        NewNotaActivity.this.color = color;
                        NewNotaActivity.this.findViewById(R.id.bt_nuevanota_pickcolor).setBackgroundColor(NotaAdapter.NotaViewHolder.darkenColor(color));
                    }
                }));

        CheckBox checkBox = findViewById(R.id.cb_nuevanota_includecategory);
        Spinner spinnerCategoria = findViewById(R.id.sp_nuevanota_categoria);
        CheckBox checkBoxInherit = findViewById(R.id.cb_nuevanota_inheritcolor);
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

        populateSpinner();
    }

    private void populateSpinner(){
        posicionesCategoria = new ArrayList<>();

        Spinner scategorias = findViewById(R.id.sp_nuevanota_categoria);

        //Para el de categorias
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

    public void crearNota(View view) {
        if(helper==null) helper = DBHelper.getInstance(this);

        Button button = findViewById(R.id.bt_nuevanota_guardar);
        Spinner spinnerCategoria = findViewById(R.id.sp_nuevanota_categoria);
        EditText titulo = findViewById(R.id.et_nuevanota_title);
        EditText texto = findViewById(R.id.et_nuevanota_text);
        CheckBox checkBoxInherit = findViewById(R.id.cb_nuevanota_inheritcolor);

        Nota nota = new Nota();
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
        insertado = helper.insertarNota(nota);
        if(insertado){
            setResult(RESULT_OK);
            finish();
        } else{
            button.setEnabled(true);
            button.setClickable(true);
            showError();
        }
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