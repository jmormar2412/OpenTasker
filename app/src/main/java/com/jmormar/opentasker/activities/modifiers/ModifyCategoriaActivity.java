package com.jmormar.opentasker.activities.modifiers;

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

public class ModifyCategoriaActivity extends AppCompatActivity {
    private DBHelper helper;
    private EditText etNombre, etAcronimo;
    private ColorWheelView colorWheelView;
    private Categoria categoria;
    private boolean darkened;

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


        colorWheelView.subscribe((color, fromUser, shouldPropagate) ->{
            if(shouldPropagate) darkened = false;
        }) ;

        findViewById(R.id.bt_guardar).setOnClickListener(v -> actualizar());
    }

    private void actualizar() {
        if (etNombre.getText().toString().isEmpty()) {
            etNombre.setError(getString(R.string.nombre_es_obligatorio));
            return;
        }

        if (etAcronimo.getText().toString().isEmpty()) {
            etAcronimo.setError(getString(R.string.acronimo_es_obligatorio));
            return;
        }

        categoria.setNombre(etNombre.getText().toString());
        categoria.setAcronimo(etAcronimo.getText().toString());

        if (darkened) categoria.setColor(colorWheelView.getColor());
        else categoria.setColor(ColorManager.darkenColor(colorWheelView.getColor()));

        assert helper.actualizarCategoria(categoria) : getString(R.string.error_guardando) + getString(R.string.categoria_minuscula);
        finish();
    }

    private void loadData() {
        int idCategoria = getIntent().getIntExtra("idCategoria", -1);
        assert idCategoria != -1 : getString(R.string.error_obteniendo_id);

        this.categoria = helper.getCategoria(idCategoria);

        etNombre.setText(categoria.getNombre());
        etAcronimo.setText(categoria.getAcronimo());
        colorWheelView.setColor(categoria.getColor(), false);

        darkened = true;
    }

    private void setElements() {
        this.helper = DBHelper.getInstance(this);

        this.etNombre = findViewById(R.id.et_nombre_categoria);
        this.colorWheelView = findViewById(R.id.color_wheel);

        this.etAcronimo = findViewById(R.id.et_acronimo_categoria);
    }
}