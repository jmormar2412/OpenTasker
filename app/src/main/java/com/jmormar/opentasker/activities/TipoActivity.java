package com.jmormar.opentasker.activities;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

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
        addBuilder.setTitle("Nombre del tipo");

        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        addBuilder.setView(input);

        addBuilder.setPositiveButton("OK", (dialog, which) ->{
            if(input.getText().toString().isEmpty()){
                input.setError("Campo requerido");
                return;
            }
            newTipo(input.getText().toString());
        });
        addBuilder.setNegativeButton("Cancelar", (dialog, which) ->{
            dialog.cancel();
            createAddBuilder();
        } );
    }

    private void newTipo(String nombre) {
        Tipo tipo = new Tipo();
        tipo.setNombre(nombre);
        assert helper.insertarTipo(tipo) : "Error al insertar tipo -> newTipo";
        cargarTipos();
    }

    private void setSliding(RecyclerView rvTipos) {
        ItemTouchHelper.SimpleCallback swipeCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                Tipo tipo = tipos.get(viewHolder.getAdapterPosition());
                assert helper.deleteTipo(tipo.getIdTipo());
                cargarTipos();
            }
        };

        new ItemTouchHelper(swipeCallback).attachToRecyclerView(rvTipos);
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
        modifyBuilder.setTitle("Nombre del tipo");

        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        input.setText(tipo.getNombre());
        modifyBuilder.setView(input);

        modifyBuilder.setPositiveButton("Actualizar", (dialog, which) ->{
            String inputText = input.getText().toString();

            if(inputText.isEmpty()){
                input.setError("Campo requerido");
                return;
            }

            if(inputText.equals(tipo.getNombre())) dialog.cancel();

            tipo.setNombre(inputText);

            assert helper.actualizarTipo(tipo) : "No se ha podido actualizar el tipo";
            assert rvTipos.getAdapter() != null : "El adapter no puede ser nulo";
            rvTipos.getAdapter().notifyItemChanged(position);
        });
        modifyBuilder.setNegativeButton("Cancelar", (dialog, which) ->{
            dialog.cancel();
        } );

        modifyBuilder.create().show();
    }
}