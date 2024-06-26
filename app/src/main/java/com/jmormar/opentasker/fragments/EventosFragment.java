package com.jmormar.opentasker.fragments;

import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.jmormar.opentasker.R;
import com.jmormar.opentasker.activities.builders.NewEventoActivity;
import com.jmormar.opentasker.activities.modifiers.ModifyEventosActivity;
import com.jmormar.opentasker.adapters.EventoAdapter;
import com.jmormar.opentasker.models.Evento;
import com.jmormar.opentasker.util.DBHelper;
import com.jmormar.opentasker.util.SwipeGesture;
import com.jmormar.opentasker.widgets.WidgetEventos;

import java.util.List;
import java.util.stream.Collectors;

import timber.log.Timber;

public class EventosFragment extends Fragment implements EventoAdapter.OnEventoClickListener {

    private static final String ARG_PARAM1 = "param1", ARG_PARAM2 = "param2";
    private Context context;
    private RecyclerView recyclerViewEventos, recyclerViewEventosCompletados;
    private List<Evento> eventos, eventosHechos;
    private DBHelper helper;
    private TextView tvEventosNoData, tvCompletedNoData;

    public static EventosFragment newInstance(String param1, String param2) {
        EventosFragment fragment = new EventosFragment();
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
        View rootView = inflater.inflate(R.layout.fragment_eventos, container, false);

        this.helper = DBHelper.getInstance(this.context);
        this.context = requireContext();

        this.recyclerViewEventos = rootView.findViewById(R.id.rv_eventos_eventos);
        this.recyclerViewEventosCompletados = rootView.findViewById(R.id.rv_eventos_completed);

        recyclerViewEventos.suppressLayout(true);
        recyclerViewEventosCompletados.suppressLayout(true);

        this.tvEventosNoData = rootView.findViewById(R.id.tv_eventos_nodata);
        this.tvCompletedNoData = rootView.findViewById(R.id.tv_completedeventos_nodata);

        tvCompletedNoData.setVisibility(View.VISIBLE);
        tvEventosNoData.setVisibility(View.VISIBLE);

        recyclerViewEventos.setLayoutManager(new LinearLayoutManager(this.context));
        recyclerViewEventos.setItemAnimator(new DefaultItemAnimator());
        recyclerViewEventosCompletados.addItemDecoration(new DividerItemDecoration(this.context, DividerItemDecoration.VERTICAL));

        recyclerViewEventosCompletados.setLayoutManager(new LinearLayoutManager(this.context));
        recyclerViewEventosCompletados.setItemAnimator(new DefaultItemAnimator());
        recyclerViewEventos.addItemDecoration(new DividerItemDecoration(this.context, DividerItemDecoration.VERTICAL));

        addSwipeGestures();

        addListenerToButton(rootView);
        cargarEventos();

        return rootView;
    }

    private void addSwipeGestures() {
        new ItemTouchHelper(new SwipeGesture(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT, this.context) {
            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                int position = viewHolder.getBindingAdapterPosition();
                Evento evento = eventos.get(position);
                switch (direction) {
                    case ItemTouchHelper.LEFT:
                        assert helper.deleteEvento(evento.getIdEvento()) : getString(R.string.error_borrando) + getString(R.string.evento);
                        Timber.i("%s%s", getString(R.string.exito_borrando), getString(R.string.evento));
                        cargarEventos();
                        break;
                    case ItemTouchHelper.RIGHT:
                        evento.setHecho(!evento.isHecho());
                        assert helper.actualizarEvento(evento) : getString(R.string.error_modificando) + getString(R.string.evento);
                        Timber.i("%s%s", getString(R.string.exito_modificando), getString(R.string.evento));
                        cargarEventos();
                        break;
                }
            }
        }).attachToRecyclerView(recyclerViewEventos);

        new ItemTouchHelper(new SwipeGesture(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT, this.context) {
            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                int position = viewHolder.getBindingAdapterPosition();
                Evento evento = eventosHechos.get(position);
                switch (direction) {
                    case ItemTouchHelper.LEFT:
                        assert helper.deleteEvento(evento.getIdEvento()) : getString(R.string.error_borrando) + getString(R.string.evento);
                        Timber.i("%s%s", getString(R.string.exito_borrando), getString(R.string.evento));
                        cargarEventos();
                        break;
                    case ItemTouchHelper.RIGHT:
                        evento.setHecho(!evento.isHecho());
                        assert helper.actualizarEvento(evento) : getString(R.string.error_modificando) + getString(R.string.evento);
                        Timber.i("%s%s", getString(R.string.exito_modificando), getString(R.string.evento));
                        cargarEventos();
                        break;
                }
            }
        }).attachToRecyclerView(recyclerViewEventosCompletados);
    }

    private void addListenerToButton(View rootView) {
        FloatingActionButton btAdd = rootView.findViewById(R.id.fab_nuevo_evento);
        btAdd.setOnClickListener(v -> {
            Intent myIntent = new Intent(context, NewEventoActivity.class);
            startActivity(myIntent);
        });
    }

    private void cargarEventos() {
        if(helper == null) helper = DBHelper.getInstance(this.context);
        List<Evento> helperEventos = helper.getEventos();

        this.eventos = helperEventos.stream().filter(e -> !e.isHecho()).collect(Collectors.toList());
        this.eventosHechos = helperEventos.stream().filter(Evento::isHecho).collect(Collectors.toList());

        if(!eventos.isEmpty()){
            this.tvEventosNoData.setVisibility(View.GONE);
        } else{
            this.tvEventosNoData.setVisibility(View.VISIBLE);
        }

        if(!eventosHechos.isEmpty()){
            this.tvCompletedNoData.setVisibility(View.GONE);
        } else{
            this.tvCompletedNoData.setVisibility(View.VISIBLE);
        }

        EventoAdapter eventoAdapter = new EventoAdapter(this.context, eventos, "");
        eventoAdapter.setOnEventoClickListener(this);

        EventoAdapter eventoAdapterCompletados = new EventoAdapter(this.context, eventosHechos, "hechos");
        eventoAdapterCompletados.setOnEventoClickListener(this);

        recyclerViewEventos.setAdapter(eventoAdapter);
        recyclerViewEventosCompletados.setAdapter(eventoAdapterCompletados);

        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(this.context);
        ComponentName componentName = new ComponentName(this.context, WidgetEventos.class);
        appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetManager.getAppWidgetIds(componentName), R.id.lv_eventos);
    }

    @Override
    public void onEventoClick(int position, String identificadorAdapter) {
        int id;
        Intent myIntent = new Intent(this.context, ModifyEventosActivity.class);
        if(identificadorAdapter.equals("hechos")){
            id = eventosHechos.get(position).getIdEvento();
        } else{
            id = eventos.get(position).getIdEvento();
        }
        myIntent.putExtra("id", id);
        startActivity(myIntent);
    }

    @Override
    public void onResume() {
        super.onResume();
        cargarEventos();
    }
}