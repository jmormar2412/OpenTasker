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
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.jmormar.opentasker.R;
import com.jmormar.opentasker.activities.builders.NewEventoActivity;
import com.jmormar.opentasker.activities.builders.NewNotaActivity;
import com.jmormar.opentasker.activities.modifiers.ModifyEventosActivity;
import com.jmormar.opentasker.activities.modifiers.ModifyNotasActivity;
import com.jmormar.opentasker.adapters.EventoAdapter;
import com.jmormar.opentasker.adapters.NotaAdapter;
import com.jmormar.opentasker.models.Evento;
import com.jmormar.opentasker.models.Nota;
import com.jmormar.opentasker.util.DBHelper;
import com.jmormar.opentasker.util.SwipeGesture;

import java.util.List;
import java.util.stream.Collectors;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link HomeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HomeFragment extends Fragment implements NotaAdapter.OnNoteClickListener, EventoAdapter.OnEventoClickListener{
    private static final String ARG_PARAM1 = "param1", ARG_PARAM2 = "param2";
    private DBHelper helper;
    private RecyclerView recyclerViewEventos, recyclerViewNotas;
    private List<Evento> eventos;
    private List<Nota> notas;
    private TextView tvEventosNoData, tvNotasNoData;
    private Context context;


    public HomeFragment() {}

    public static HomeFragment newInstance(String param1, String param2) {
        HomeFragment fragment = new HomeFragment();
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        this.context = requireContext();

        this.recyclerViewEventos = rootView.findViewById(R.id.rv_main_eventos);
        this.recyclerViewNotas = rootView.findViewById(R.id.rv_main_notas);

        this.tvEventosNoData = rootView.findViewById(R.id.tv_main_eventos_nodata);
        this.tvNotasNoData = rootView.findViewById(R.id.tv_main_notas_nodata);

        new ItemTouchHelper(new SwipeGesture(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT, this.context) {
            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                int position = viewHolder.getBindingAdapterPosition();
                Evento evento = eventos.get(position);
                switch (direction) {
                    case ItemTouchHelper.LEFT:
                        assert helper.deleteEvento(evento.getIdEvento()) : "No se pudo borrar el evento";
                        cargarEventos();
                        break;
                    case ItemTouchHelper.RIGHT:
                        evento.setHecho(!evento.isHecho());
                        assert helper.actualizarEvento(evento) : "No se pudo actualizar el evento";
                        cargarEventos();
                        break;
                }
            }
        }).attachToRecyclerView(recyclerViewEventos);


        recyclerViewNotas.setLayoutManager(new GridLayoutManager(this.context, 2));

        addListenersToButtons(rootView);

        cargarEventos();
        cargarNotas();

        return rootView;
    }

    private void addListenersToButtons(View rootView) {
        FloatingActionButton btAddEvento = rootView.findViewById(R.id.fab_nuevo_evento);
        FloatingActionButton btAddNota = rootView.findViewById(R.id.fab_nueva_nota);

        btAddEvento.setOnClickListener(v -> irNuevoEvento());

        btAddNota.setOnClickListener(v -> irNuevaNota());
    }

    private void cargarEventos() {
        if(helper == null) helper = DBHelper.getInstance(this.context);
        List<Evento> helperEventos = helper.getEventos();

        this.eventos = helperEventos.stream().filter(e -> !e.isHecho()).collect(Collectors.toList());

        if(!eventos.isEmpty()){
            this.tvEventosNoData.setVisibility(View.GONE);
        } else{
            this.tvEventosNoData.setVisibility(View.VISIBLE);
        }

        EventoAdapter eventoAdapter = new EventoAdapter(this.context, eventos, "unico");
        eventoAdapter.setOnEventoClickListener(this);

        recyclerViewEventos.setAdapter(eventoAdapter);
        recyclerViewEventos.setLayoutManager(new LinearLayoutManager(this.context));
        recyclerViewEventos.addItemDecoration(new DividerItemDecoration(this.context, DividerItemDecoration.VERTICAL));
        recyclerViewEventos.setItemAnimator(new DefaultItemAnimator());
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
    public void onResume() {
        super.onResume();
        cargarEventos();
        cargarNotas();
    }

    @Override
    public void onNoteClick(int position) {
        Intent myIntent = new Intent(this.context, ModifyNotasActivity.class);
        int idNota = notas.get(position).getIdNota();
        myIntent.putExtra("idNota", idNota);
        startActivity(myIntent);
    }

    @Override
    public void onEventoClick(int position, String identificadorAdapter) {
        Intent myIntent = new Intent(this.context, ModifyEventosActivity.class);
        int id = eventos.get(position).getIdEvento();
        myIntent.putExtra("id", id);
        startActivity(myIntent);
    }

    public void irNuevoEvento() {
        Intent myIntent = new Intent(this.context, NewEventoActivity.class);
        startActivity(myIntent);
    }

    public void irNuevaNota() {
        Intent myIntent = new Intent(this.context, NewNotaActivity.class);
        startActivity(myIntent);
    }
}