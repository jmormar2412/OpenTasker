package com.jmormar.opentasker.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.jmormar.opentasker.R;
import com.jmormar.opentasker.adapters.NotaAdapter;
import com.jmormar.opentasker.models.Nota;
import com.jmormar.opentasker.objectbuilders.NewNotaActivity;
import com.jmormar.opentasker.objectmodifiers.ModifyNotasActivity;
import com.jmormar.opentasker.util.DBHelper;

import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link NotasFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class NotasFragment extends Fragment implements NotaAdapter.OnNoteClickListener {
    private static final String ARG_PARAM1 = "param1", ARG_PARAM2 = "param2";
    private Context context;
    private DBHelper helper;
    private RecyclerView recyclerViewNotas;
    private TextView tvNotasNoData;

    public NotasFragment() {}


    public static NotasFragment newInstance(String param1, String param2) {
        NotasFragment fragment = new NotasFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            getArguments().getString(ARG_PARAM1);
            getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        this.context = requireContext();
        this.helper = DBHelper.getInstance(this.context);
        View rootView = inflater.inflate(R.layout.fragment_notas, container, false);

        this.recyclerViewNotas = rootView.findViewById(R.id.rv_notas_notas);
        this.tvNotasNoData = rootView.findViewById(R.id.tv_notas_nodata);

        recyclerViewNotas.setLayoutManager(new GridLayoutManager(this.context, 2));

        FloatingActionButton btAddNota = rootView.findViewById(R.id.fab_nueva_nota);
        btAddNota.setOnClickListener(v -> irNuevaNota());

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        cargarNotas();
    }

    private void cargarNotas() {
        if(helper == null) helper = DBHelper.getInstance(this.context);
        List<Nota> notas = helper.getNotas();

        if(!notas.isEmpty()){
            this.tvNotasNoData.setVisibility(View.GONE);
        } else{
            this.tvNotasNoData.setVisibility(View.VISIBLE);
        }

        NotaAdapter notaAdapter = new NotaAdapter(this.context, notas);
        notaAdapter.setOnNoteClickListener(this);

        recyclerViewNotas.setAdapter(notaAdapter);
    }

    @Override
    public void onNoteClick(int position) {
        Intent myIntent = new Intent(this.context, ModifyNotasActivity.class);
        myIntent.putExtra("position", position);
        startActivity(myIntent);
    }

    public void irNuevaNota() {
        Intent myIntent = new Intent(this.context, NewNotaActivity.class);
        startActivity(myIntent);
    }
}