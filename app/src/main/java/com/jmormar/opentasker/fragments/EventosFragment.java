package com.jmormar.opentasker.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.jmormar.opentasker.R;
import com.jmormar.opentasker.adapters.EventoAdapter;
import com.jmormar.opentasker.models.Evento;
import com.jmormar.opentasker.objectbuilders.NewEventoActivity;
import com.jmormar.opentasker.objectmodifiers.ModifyEventosActivity;
import com.jmormar.opentasker.util.DBHelper;

import java.util.List;
import java.util.stream.Collectors;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link EventosFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class EventosFragment extends Fragment implements EventoAdapter.OnEventoClickListener {

    private static final String ARG_PARAM1 = "param1", ARG_PARAM2 = "param2";
    private String mParam1, mParam2;
    private Context context;
    private RecyclerView recyclerViewEventos, recyclerViewEventosCompletados;
    private EventoAdapter eventoAdapter, eventoAdapterCompletados;
    private List<Evento> eventos, eventosHechos;
    private DBHelper helper;
    private TextView tvEventosNoData, tvCompletedNoData;

    public EventosFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment EventosFragment.
     */
    // TODO: Rename and change types and number of parameters
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
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_eventos, container, false);

        this.helper = DBHelper.getInstance(this.context);
        this.context = requireContext();

        this.recyclerViewEventos = rootView.findViewById(R.id.rv_eventos_eventos);
        this.recyclerViewEventosCompletados = rootView.findViewById(R.id.rv_eventos_completed);

        this.tvEventosNoData = rootView.findViewById(R.id.tv_eventos_nodata);
        this.tvCompletedNoData = rootView.findViewById(R.id.tv_completedeventos_nodata);

        tvCompletedNoData.setVisibility(View.VISIBLE);
        tvEventosNoData.setVisibility(View.VISIBLE);

        ItemTouchHelper.SimpleCallback swipeCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                int position = viewHolder.getAdapterPosition();
                Evento evento = eventos.get(position);
                switch (direction) {
                    case ItemTouchHelper.LEFT:
                        helper.deleteEvento(evento.getIdEvento());
                        cargarEventos();
                        break;
                    case ItemTouchHelper.RIGHT:
                        evento.setHecho(!evento.isHecho());
                        helper.actualizarEvento(evento);
                        cargarEventos();
                        break;
                }
            }
        };

        ItemTouchHelper.SimpleCallback swipeCallbackHechos = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                int position = viewHolder.getAdapterPosition();
                Evento evento = eventosHechos.get(position);
                switch (direction) {
                    case ItemTouchHelper.LEFT:
                        helper.deleteEvento(evento.getIdEvento());
                        cargarEventos();
                        break;
                    case ItemTouchHelper.RIGHT:
                        evento.setHecho(!evento.isHecho());
                        helper.actualizarEvento(evento);
                        cargarEventos();
                        break;
                }
            }
        };

        new ItemTouchHelper(swipeCallback).attachToRecyclerView(recyclerViewEventos);
        new ItemTouchHelper(swipeCallbackHechos).attachToRecyclerView(recyclerViewEventosCompletados);

        addListenerToButton(rootView);

        cargarEventos();

        return rootView;
    }

    private void addListenerToButton(View rootView) {
        FloatingActionButton btAdd = rootView.findViewById(R.id.fab_nuevo_evento);
        btAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent myIntent = new Intent(context, NewEventoActivity.class);
                startActivity(myIntent);
            }
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

        eventoAdapter = new EventoAdapter(this.context, eventos, "");
        eventoAdapter.setOnEventoClickListener(this);

        eventoAdapterCompletados = new EventoAdapter(this.context, eventosHechos, "hechos");
        eventoAdapterCompletados.setOnEventoClickListener(this);

        recyclerViewEventos.setAdapter(eventoAdapter);
        recyclerViewEventos.setLayoutManager(new LinearLayoutManager(this.context));
        recyclerViewEventos.addItemDecoration(new DividerItemDecoration(this.context, DividerItemDecoration.VERTICAL));
        recyclerViewEventos.setItemAnimator(new DefaultItemAnimator());

        recyclerViewEventosCompletados.setAdapter(eventoAdapterCompletados);
        recyclerViewEventosCompletados.setLayoutManager(new LinearLayoutManager(this.context));
        recyclerViewEventosCompletados.addItemDecoration(new DividerItemDecoration(this.context, DividerItemDecoration.VERTICAL));
        recyclerViewEventosCompletados.setItemAnimator(new DefaultItemAnimator());
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