package com.jmormar.opentasker.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
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

public class CategoriaActivity extends AppCompatActivity implements CategoriaAdapter.OnCategoriaClickListener{
    private DBHelper helper;
    private RecyclerView rvCategorias;
    private TextView tvNoData;
    private List<Categoria> categorias;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_categoria);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        this.helper = DBHelper.getInstance(this);
        this.rvCategorias = findViewById(R.id.rv_categorias);
        this.tvNoData = findViewById(R.id.tv_categoria_nodatos);

        findViewById(R.id.fab_nueva_categoria).setOnClickListener(v -> {
            Intent intent = new Intent(this, NewCategoriaActivity.class);
            startActivity(intent);
        });

        setSliding(rvCategorias);
        cargarCategorias();
    }

    private void cargarCategorias() {
        if(helper == null) helper = DBHelper.getInstance(this);
        categorias = helper.getCategorias();

        if(categorias.isEmpty()){
            tvNoData.setVisibility(View.VISIBLE);
        } else {
            tvNoData.setVisibility(View.GONE);
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
                assert helper.deleteCategoria(categoria.getIdCategoria()) : "No se pudo eliminar la categoria";
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