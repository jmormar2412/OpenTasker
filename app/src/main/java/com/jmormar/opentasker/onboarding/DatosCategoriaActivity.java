package com.jmormar.opentasker.onboarding;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.jmormar.opentasker.R;
import com.jmormar.opentasker.activities.builders.NewCategoriaActivity;
import com.jmormar.opentasker.activities.modifiers.ModifyCategoriaActivity;
import com.jmormar.opentasker.adapters.CategoriaAdapter;
import com.jmormar.opentasker.models.Categoria;
import com.jmormar.opentasker.util.DBHelper;
import com.jmormar.opentasker.util.SwipeGesture;

import java.util.List;

public class DatosCategoriaActivity extends AppCompatActivity implements CategoriaAdapter.OnCategoriaClickListener {
    private RecyclerView rvCategorias;
    private TextView tvNoDatos;
    private Button next;
    private DBHelper helper;
    private List<Categoria> categorias;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_datos_categoria);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        setElements();
        setSliding(rvCategorias);
        addListeners();
        cargarCategorias();
    }

    private void addListeners() {
        findViewById(R.id.fab_nueva_categoria).setOnClickListener(v -> {
            Intent intent = new Intent(this, NewCategoriaActivity.class);
            startActivity(intent);
        });

        next.setOnClickListener(v -> {
            if(!next.isEnabled()) return;

            Intent intent = new Intent(this, DatosTiposActivity.class);
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            startActivity(intent);
            finish();
        });
    }

    private void setElements() {
        rvCategorias = findViewById(R.id.rv_categorias);
        tvNoDatos = findViewById(R.id.tv_categoria_nodatos);
        next = findViewById(R.id.bt_next);

        helper = DBHelper.getInstance(this);
    }

    private void cargarCategorias() {
        categorias = helper.getCategorias();

        if(categorias.isEmpty()){
            tvNoDatos.setVisibility(View.VISIBLE);
            next.setEnabled(false);
        } else {
            tvNoDatos.setVisibility(View.GONE);
            next.setEnabled(true);
        }

        CategoriaAdapter adapter = new CategoriaAdapter(categorias);
        adapter.setOnCategoriaClickListener(this);

        rvCategorias.setAdapter(adapter);
        rvCategorias.setLayoutManager(new LinearLayoutManager(this));
        rvCategorias.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        rvCategorias.setItemAnimator(new DefaultItemAnimator());
    }

    private void setSliding(RecyclerView rvCategorias) {
        new ItemTouchHelper(new SwipeGesture(0, ItemTouchHelper.LEFT, this) {
            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                Categoria categoria = categorias.get(viewHolder.getBindingAdapterPosition());
                assert helper.deleteCategoria(categoria.getIdCategoria()) : getString(R.string.error_borrando) + getString(R.string.categoria_minuscula);
                cargarCategorias();
            }
        }).attachToRecyclerView(rvCategorias);
    }

    @Override
    public void onCategoriaClick(int position) {
        Intent intent = new Intent(this, ModifyCategoriaActivity.class);
        intent.putExtra("idCategoria", categorias.get(position).getIdCategoria());
        startActivity(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();
        cargarCategorias();
    }
}