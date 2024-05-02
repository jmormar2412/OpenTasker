package com.jmormar.opentasker.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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
import com.jmormar.opentasker.adapters.EventoAdapter;
import com.jmormar.opentasker.adapters.NotaAdapter;
import com.jmormar.opentasker.models.Evento;
import com.jmormar.opentasker.models.Nota;
import com.jmormar.opentasker.objectbuilders.NewEventoActivity;
import com.jmormar.opentasker.objectbuilders.NewNotaActivity;
import com.jmormar.opentasker.objectmodifiers.ModifyEventosActivity;
import com.jmormar.opentasker.objectmodifiers.ModifyNotasActivity;
import com.jmormar.opentasker.util.DBHelper;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link HomeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HomeFragment extends Fragment implements NotaAdapter.OnNoteClickListener, EventoAdapter.OnEventoClickListener{
    private static final String ARG_PARAM1 = "param1", ARG_PARAM2 = "param2";
    private String mParam1, mParam2;
    private DBHelper helper;
    private RecyclerView recyclerViewEventos, recyclerViewNotas;
    private EventoAdapter eventoAdapter;
    private NotaAdapter notaAdapter;
    private TextView tvEventosNoData, tvNotasNoData;
    private Context context;


    public HomeFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment Main.
     */
    // TODO: Rename and change types and number of parameters
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
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
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

        ItemTouchHelper.SimpleCallback swipeCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                int position = viewHolder.getAdapterPosition();
                List<Evento> eventos = helper.getEventos();
                Evento evento = eventos.get(position);
                switch (direction) {
                    case ItemTouchHelper.LEFT:
                        helper.deleteEvento(helper.getEventos().get(position).getIdEvento());
                        cargarEventos();
                        break;
                    case ItemTouchHelper.RIGHT:
                        evento.setHecho(true);
                        helper.actualizarEvento(evento);
                        cargarEventos();
                        break;
                }
            }
        };


        new ItemTouchHelper(swipeCallback).attachToRecyclerView(recyclerViewEventos);

        addListenersToButtons(rootView);

        cargarEventos();
        cargarNotas();

        return rootView;
    }

    private void addListenersToButtons(View rootView) {
        FloatingActionButton btAddEvento = rootView.findViewById(R.id.fab_nuevo_evento);
        FloatingActionButton btAddNota = rootView.findViewById(R.id.fab_nueva_nota);

        btAddEvento.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                irNuevoEvento();
            }
        });

        btAddNota.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                irNuevaNota();
            }
        });
    }

    private void cargarEventos() {
        if(helper == null) helper = DBHelper.getInstance(this.context);
        List<Evento> eventos = helper.getEventos();

        if(!eventos.isEmpty()){
            this.tvEventosNoData.setVisibility(View.GONE);
        } else{
            this.tvEventosNoData.setVisibility(View.VISIBLE);
        }

        eventoAdapter = new EventoAdapter(this.context, eventos, false);
        eventoAdapter.setOnEventoClickListener(this);

        recyclerViewEventos.setAdapter(eventoAdapter);
        recyclerViewEventos.setLayoutManager(new LinearLayoutManager(this.context));
        recyclerViewEventos.addItemDecoration(new DividerItemDecoration(this.context, DividerItemDecoration.VERTICAL));
        recyclerViewEventos.setItemAnimator(new DefaultItemAnimator());
    }

    private void cargarNotas() {
        if(helper == null) helper = DBHelper.getInstance(this.context);
        List<Nota> notas = helper.getNotas();

        if(!notas.isEmpty()){
            this.tvNotasNoData.setVisibility(View.GONE);
        } else{
            this.tvNotasNoData.setVisibility(View.VISIBLE);
        }

        notaAdapter = new NotaAdapter(this.context, notas);
        notaAdapter.setOnNoteClickListener(this);

        recyclerViewNotas.setAdapter(notaAdapter);
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
        myIntent.putExtra("position", position);
        startActivity(myIntent);
    }

    @Override
    public void onEventoClick(int position) {
        Intent myIntent = new Intent(this.context, ModifyEventosActivity.class);
        myIntent.putExtra("position", position);
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