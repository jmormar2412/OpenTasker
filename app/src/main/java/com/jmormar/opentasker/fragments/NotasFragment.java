package com.jmormar.opentasker.fragments;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.jmormar.opentasker.R;
import com.jmormar.opentasker.activities.builders.NewNotaActivity;
import com.jmormar.opentasker.adapters.NotaAdapter;
import com.jmormar.opentasker.models.Nota;
import com.jmormar.opentasker.util.DBHelper;

import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link NotasFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class NotasFragment extends Fragment implements NotaAdapter.OnNoteClickListener, NotaDialogFragment.CargarNotasListener {
    private static final String ARG_PARAM1 = "param1", ARG_PARAM2 = "param2";
    private Context context;
    private DBHelper helper;
    private RecyclerView recyclerViewNotas;
    private TextView tvNotasNoData;
    private List<Nota> notas;

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

        recyclerViewNotas.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));

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
        this.notas = helper.getNotas();

        if(!notas.isEmpty()){
            this.tvNotasNoData.setVisibility(View.GONE);
        } else{
            this.tvNotasNoData.setVisibility(View.VISIBLE);
        }

        NotaAdapter notaAdapter = new NotaAdapter(this.context, notas);
        notaAdapter.setOnNoteClickListener(this);

        recyclerViewNotas.setAdapter(notaAdapter);
        setLayoutManager(getResources().getConfiguration().orientation);
    }

    private void setLayoutManager(int orientation) {
        int spanCount;
        if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
            spanCount = 4;
        } else {
            spanCount = 2;
        }
        recyclerViewNotas.setLayoutManager(new StaggeredGridLayoutManager(spanCount, StaggeredGridLayoutManager.VERTICAL));
    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        setLayoutManager(newConfig.orientation);
    }

    @Override
    public void onNoteClick(int position) {
        Nota nota = notas.get(position);

        NotaDialogFragment previewDialog = NotaDialogFragment.newInstance(nota);
        previewDialog.setListener(this);
        previewDialog.show(((FragmentActivity) this.context).getSupportFragmentManager(), "Vista previa de Nota");
    }

    public void irNuevaNota() {
        Intent myIntent = new Intent(this.context, NewNotaActivity.class);
        startActivity(myIntent);
    }

    @Override
    public void updateNotas() {
        onResume();
    }
}