package com.jmormar.opentasker.activities.objectmodifiers;

import android.os.Bundle;
import android.widget.EditText;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.jmormar.opentasker.R;
import com.jmormar.opentasker.models.Categoria;
import com.jmormar.opentasker.util.DBHelper;

import top.defaults.colorpicker.ColorWheelView;

public class ModifyCategoriaActivity extends AppCompatActivity {
    private DBHelper helper;
    private EditText etNombre;
    private ColorWheelView colorWheelView;
    private Categoria categoria;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_modify_categoria);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        setElements();
        loadData();

        findViewById(R.id.bt_guardar).setOnClickListener(v -> actualizar());
    }

    private void actualizar() {
        if (etNombre.getText().toString().isEmpty()) {
            etNombre.setError("El nombre no puede estar vacio");
            return;
        }

        categoria.setNombre(etNombre.getText().toString());
        categoria.setColor(colorWheelView.getColor());

        assert helper.actualizarCategoria(categoria) : "No se ha podido actualizar la categoria";
        finish();
    }

    private void loadData() {
        int idCategoria = getIntent().getIntExtra("idCategoria", -1);
        assert idCategoria != -1 : "No se ha recibido el id de la categoria";

        this.categoria = helper.getCategoria(idCategoria);

        etNombre.setText(categoria.getNombre());
        colorWheelView.setColor(categoria.getColor(), false);
    }

    private void setElements() {
        this.helper = DBHelper.getInstance(this);

        this.etNombre = findViewById(R.id.et_nombre_categoria);
        this.colorWheelView = findViewById(R.id.color_wheel);
    }
}