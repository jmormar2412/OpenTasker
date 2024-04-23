package com.jmormar.opentasker.objectbuilders;

import android.content.Context;
import android.content.res.Resources;
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
import com.jmormar.opentasker.models.Categoria;
import com.jmormar.opentasker.models.Nota;
import com.jmormar.opentasker.models.Tipo;
import com.jmormar.opentasker.util.DBHelper;

import java.util.ArrayList;
import java.util.List;

public class NewNotaActivity extends AppCompatActivity {

    private DBHelper helper;
    private List<Integer> posicionesCategoria;

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

        CheckBox checkBox = findViewById(R.id.cb_nuevanota_includecategory);
        Button saveButton = findViewById(R.id.bt_nuevanota_guardar);
        addListeners(checkBox, saveButton);

        populateSpinner();
    }

    private void populateSpinner(){
        posicionesCategoria = new ArrayList<>();

        Spinner scategorias = (Spinner) findViewById(R.id.sp_nuevanota_categoria);

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

    private void addListeners(CheckBox checkBox, Button button){

        Spinner spinnerCategoria = findViewById(R.id.sp_nuevanota_categoria);
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

        EditText titulo = findViewById(R.id.et_nuevanota_title);
        EditText texto = findViewById(R.id.et_nuevanota_text);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Nota nota = new Nota();
                nota.setTitulo(titulo.getText().toString());
                nota.setTexto(texto.getText().toString());

                if(spinnerCategoria.getVisibility() == View.VISIBLE){
                    nota.setIdCategoria(posicionesCategoria.get(spinnerCategoria.getSelectedItemPosition()));
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
                    showError("error.IOException");
                }
            }
        });
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