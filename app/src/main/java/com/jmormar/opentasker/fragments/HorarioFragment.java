package com.jmormar.opentasker.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.jmormar.opentasker.R;
import com.jmormar.opentasker.activities.builders.NewHoraActivity;
import com.jmormar.opentasker.activities.modifiers.ModifyHoraActivity;
import com.jmormar.opentasker.adapters.HoraAdapter;
import com.jmormar.opentasker.adapters.SegmentoHorarioAdapter;
import com.jmormar.opentasker.models.Hora;
import com.jmormar.opentasker.util.DBHelper;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class HorarioFragment extends Fragment implements HoraAdapter.OnHoraClickListener {
    private static final String ARG_PARAM1 = "param1",ARG_PARAM2 = "param2";
    private final RecyclerView[] recyclerViews = new RecyclerView[7];
    private RecyclerView rvTimeDisplay;
    private final HoraAdapter[] adapters = new HoraAdapter[7];
    private SegmentoHorarioAdapter segmentoHorarioAdapter;
    private Context context;
    private DBHelper helper;
    private List<List<Hora>> horasByDay;
    private int preferredStartingDay;

    public static HorarioFragment newInstance(String param1, String param2) {
        HorarioFragment fragment = new HorarioFragment();
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
        this.helper = DBHelper.getInstance(context);
        this.preferredStartingDay = helper.getAgenda().getBeginningDay();

        View view = inflater.inflate(R.layout.fragment_horario, container, false);

        this.rvTimeDisplay = view.findViewById(R.id.rv_timedisplay);
        recyclerViews[0] = view.findViewById(R.id.rv_first);
        recyclerViews[1] = view.findViewById(R.id.rv_second);
        recyclerViews[2] = view.findViewById(R.id.rv_third);
        recyclerViews[3] = view.findViewById(R.id.rv_fourth);
        recyclerViews[4] = view.findViewById(R.id.rv_fifth);
        recyclerViews[5] = view.findViewById(R.id.rv_sixth);
        recyclerViews[6] = view.findViewById(R.id.rv_seventh);

        rvTimeDisplay.setLayoutManager(new LinearLayoutManager(context));
        for (RecyclerView recyclerView : recyclerViews) {
            recyclerView.setLayoutManager(new LinearLayoutManager(context));
        }

        loadHoras();
        setAdapters();

        return view;
    }

    private void setAdapters() {
        calculateSegments();

        rvTimeDisplay.setAdapter(segmentoHorarioAdapter);
        rvTimeDisplay.setVisibility(View.VISIBLE);
        rvTimeDisplay.suppressLayout(true);

        for (int i = 0; i < adapters.length; i++) {
            int adjustedIndex = adjustDayOfWeek(i, preferredStartingDay);
            String dayName = getResources().getStringArray(R.array.dias_semana)[adjustedIndex];
            adapters[i] = new HoraAdapter(this.context, horasByDay.get(adjustedIndex), dayName);
            adapters[i].setOnHoraClickListener(this);
            recyclerViews[i].setAdapter(adapters[i]);
            recyclerViews[i].suppressLayout(true);
        }

        int weekLength = helper.getAgenda().getWeekLength();
        for (int i = 0; i < recyclerViews.length; i++) {
            if (i < weekLength) {
                recyclerViews[i].setVisibility(View.VISIBLE);
            } else {
                recyclerViews[i].setVisibility(View.GONE);
            }
        }
    }

    public void loadHoras() {
        Hora.resetList();
        if(helper.getHoras().isEmpty()){
            Hora.horaMaxima = null;
            Hora.horaMinima = null;
        }

        this.horasByDay = new ArrayList<>();
        for (int i = 0; i < 7; i++) {
            this.horasByDay.add(helper.getHorasByDayAndHorario(i));
        }
    }

    public void addNewHora() {
        Intent intent = new Intent(context, NewHoraActivity.class);
        startActivity(intent);
    }

    private int adjustDayOfWeek(int dayOfWeek, int preferredStartingDay) {
        return (dayOfWeek + preferredStartingDay) % 7;
    }

    private void calculateSegments(){
        LocalTime startTime = Hora.horaMinima;
        LocalTime endTime = Hora.horaMaxima;

        if(startTime == null || endTime == null){
            segmentoHorarioAdapter = new SegmentoHorarioAdapter(LocalTime.of(8, 0), LocalTime.of(15, 0));
        } else{
            segmentoHorarioAdapter = new SegmentoHorarioAdapter(startTime, endTime);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        loadHoras();
        setAdapters();
    }

    @Override
    public void onHoraClick(Integer idHora) {
        if(idHora == null) return;
        Intent intent = new Intent(this.context, ModifyHoraActivity.class);
        intent.putExtra("idHora", idHora);
        startActivity(intent);
    }
}