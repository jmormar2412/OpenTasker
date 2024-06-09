package com.jmormar.opentasker.activities;

import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.jmormar.opentasker.R;
import com.jmormar.opentasker.adapters.TipoAdapter;
import com.jmormar.opentasker.models.Tipo;
import com.jmormar.opentasker.util.DBHelper;
import com.jmormar.opentasker.util.SwipeGesture;

import java.util.List;

public class TipoActivity extends AppCompatActivity implements TipoAdapter.OnTipoClickListener {
    private RecyclerView rvTipos;
    private TextView tvNoData;
    private DBHelper helper;
    private List<Tipo> tipos;
    private AlertDialog.Builder addBuilder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_tipo);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        helper = DBHelper.getInstance(this);

        rvTipos = findViewById(R.id.rv_tipos);
        tvNoData = findViewById(R.id.tv_tipo_nodata);

        FloatingActionButton btAddTipo = findViewById(R.id.fab_nuevo_tipo);
        btAddTipo.setOnClickListener(v -> this.addBuilder.show());

        setSliding(rvTipos);
        createAddBuilder();
        cargarTipos();
    }

    private void createAddBuilder() {
        this.addBuilder = new AlertDialog.Builder(this);
        addBuilder.setTitle(getString(R.string.nombre_tipo));

        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        addBuilder.setView(input);

        addBuilder.setPositiveButton(getString(R.string.aceptar), (dialog, which) ->{
            if(input.getText().toString().isEmpty()){
                input.setError(getString(R.string.nombre_es_obligatorio));
                return;
            }
            newTipo(input.getText().toString());
        });
        addBuilder.setNegativeButton(getString(R.string.cancelar), (dialog, which) ->{
            dialog.cancel();
            createAddBuilder();
        } );
    }

    private void newTipo(String nombre) {
        Tipo tipo = new Tipo();
        tipo.setNombre(nombre);
        assert helper.insertarTipo(tipo) : getString(R.string.error_insertando) + getString(R.string.tipo_minuscula);
        cargarTipos();
    }

    private void setSliding(RecyclerView rvTipos) {
        new ItemTouchHelper(new SwipeGesture(0, ItemTouchHelper.LEFT, this) {
            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                Tipo tipo = tipos.get(viewHolder.getBindingAdapterPosition());
                assert helper.deleteTipo(tipo.getIdTipo());
                cargarTipos();
            }
        }).attachToRecyclerView(rvTipos);
    }

    private void cargarTipos() {
        if(helper == null) helper = DBHelper.getInstance(this);
        tipos = helper.getTipos();

        if(tipos.isEmpty()){
            tvNoData.setVisibility(View.VISIBLE);
        } else {
            tvNoData.setVisibility(View.GONE);
        }

        TipoAdapter adapter = new TipoAdapter(tipos);
        adapter.setOnTipoClickListener(this);

        rvTipos.setAdapter(adapter);
        rvTipos.setLayoutManager(new LinearLayoutManager(this));
        rvTipos.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        rvTipos.setItemAnimator(new DefaultItemAnimator());
    }

    @Override
    public void onTipoClick(int position) {
        launchModifyBuilder(tipos.get(position), position);
    }

    private void launchModifyBuilder(Tipo tipo, int position) {
        AlertDialog.Builder modifyBuilder = new AlertDialog.Builder(this);
        modifyBuilder.setTitle(getString(R.string.nombre_tipo));

        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        input.setText(tipo.getNombre());
        modifyBuilder.setView(input);

        modifyBuilder.setPositiveButton(getString(R.string.guardar), (dialog, which) ->{
            String inputText = input.getText().toString();

            if(inputText.isEmpty()){
                input.setError(getString(R.string.nombre_es_obligatorio));
                return;
            }

            if(inputText.equals(tipo.getNombre())) dialog.cancel();

            tipo.setNombre(inputText);

            assert helper.actualizarTipo(tipo) : getString(R.string.error_guardando) + getString(R.string.tipo_minuscula);
            assert rvTipos.getAdapter() != null : getString(R.string.adapter_no_nulo);
            rvTipos.getAdapter().notifyItemChanged(position);
        });
        modifyBuilder.setNegativeButton(getString(R.string.cancelar), (dialog, which) -> dialog.cancel());

        modifyBuilder.create().show();
    }
}