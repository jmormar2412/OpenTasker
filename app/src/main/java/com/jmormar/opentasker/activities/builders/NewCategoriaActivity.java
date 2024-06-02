package com.jmormar.opentasker.activities.builders;

import android.os.Bundle;
import android.widget.EditText;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.jmormar.opentasker.R;
import com.jmormar.opentasker.models.Categoria;
import com.jmormar.opentasker.util.ColorManager;
import com.jmormar.opentasker.util.DBHelper;

import top.defaults.colorpicker.ColorWheelView;

public class NewCategoriaActivity extends AppCompatActivity {
    private EditText etNombre, etAcronimo;
    private ColorWheelView colorWheelView;
    private DBHelper helper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_nueva_categoria);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        this.etNombre = findViewById(R.id.et_nombre_categoria);
        this.colorWheelView = findViewById(R.id.color_wheel);
        this.etAcronimo = findViewById(R.id.et_acronimo_categoria);
        this.helper = DBHelper.getInstance(this);


        findViewById(R.id.bt_guardar).setOnClickListener((v) -> guardar());
    }

    private void guardar() {
        String nombre = this.etNombre.getText().toString();
        if (nombre.isEmpty()) {
            this.etNombre.setError("El nombre es obligatorio");
            return;
        }

        String acronimo = this.etAcronimo.getText().toString();
        if (acronimo.isEmpty()) {
            this.etNombre.setError("El acronimo es obligatorio");
            return;
        }

        Categoria categoria = new Categoria();
        categoria.setNombre(nombre);
        categoria.setColor(ColorManager.darkenColor(colorWheelView.getColor()));
        categoria.setIdAgenda(helper.getAgenda().getIdAgenda());

        assert helper.insertarCategoria(categoria) : "Error al insertar la categoria";
        finish();
    }
}