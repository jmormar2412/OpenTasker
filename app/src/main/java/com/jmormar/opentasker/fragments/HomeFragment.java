package com.jmormar.opentasker.fragments;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.jmormar.opentasker.R;
import com.jmormar.opentasker.adapters.EventoAdapter;
import com.jmormar.opentasker.adapters.NotaAdapter;
import com.jmormar.opentasker.models.Evento;
import com.jmormar.opentasker.models.Nota;
import com.jmormar.opentasker.util.DBHelper;

import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link HomeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HomeFragment extends Fragment {
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

        cargarEventos();
        cargarNotas();

        return rootView;
    }

    private void cargarEventos() {
        if(helper == null) helper = DBHelper.getInstance(this.context);
        List<Evento> eventos = helper.getEventos();
        if(!eventos.isEmpty()) this.tvEventosNoData.setVisibility(View.GONE);
        eventoAdapter = new EventoAdapter(this.context, eventos);
        recyclerViewEventos.setAdapter(eventoAdapter);
        recyclerViewEventos.setLayoutManager(new LinearLayoutManager(this.context));
        recyclerViewEventos.addItemDecoration(new DividerItemDecoration(this.context, DividerItemDecoration.VERTICAL));
        recyclerViewEventos.setItemAnimator(new DefaultItemAnimator());
    }

    private void cargarNotas() {
        if(helper == null) helper = DBHelper.getInstance(this.context);
        List<Nota> notas = helper.getNotas();
        if(!notas.isEmpty()) this.tvNotasNoData.setVisibility(View.GONE);
        notaAdapter = new NotaAdapter(this.context, notas);
        recyclerViewNotas.setAdapter(notaAdapter);
    }

    @Override
    public void onResume() {
        super.onResume();
        cargarEventos();
        cargarNotas();
    }
}