package com.jmormar.opentasker.fragments;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.jmormar.opentasker.R;
import com.jmormar.opentasker.adapters.PomodoroAdapter;
import com.jmormar.opentasker.models.Pomodoro;
import com.jmormar.opentasker.activities.TiemposActivity;
import com.jmormar.opentasker.util.DBHelper;

import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link PomodoroFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PomodoroFragment extends Fragment implements PomodoroAdapter.OnPomodoroClickListener {
    private static final String ARG_PARAM1 = "param1",ARG_PARAM2 = "param2";
    private Context context;
    TextView tvPomodorosNoData;
    private RecyclerView recyclerViewPomodoros;
    private DBHelper helper;
    private AlertDialog.Builder builder;

    public PomodoroFragment() {
    }

    public static PomodoroFragment newInstance(String param1, String param2) {
        PomodoroFragment fragment = new PomodoroFragment();
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
        // Inflate the layout for this fragment
        this.context = requireContext();
        this.helper = DBHelper.getInstance(this.context);
        View rootView = inflater.inflate(R.layout.fragment_pomodoro, container, false);

        this.recyclerViewPomodoros = rootView.findViewById(R.id.rv_pomodoros);
        this.tvPomodorosNoData = rootView.findViewById(R.id.tv_pomodoros_nodata);

        recyclerViewPomodoros.setLayoutManager(new LinearLayoutManager(this.context));

        FloatingActionButton btAddPomodoro = rootView.findViewById(R.id.fab_nuevo_pomodoro);
        btAddPomodoro.setOnClickListener(v -> this.builder.show());

        addSwipingFunctionality();

        createBuilder();
        cargarPomodoros();

        return rootView;
    }

    private void addSwipingFunctionality() {
        ItemTouchHelper.SimpleCallback swipeCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                int position = viewHolder.getAdapterPosition();
                Pomodoro pomodoro = helper.getPomodoros().get(position);
                helper.deletePomodoro(pomodoro.getIdPomodoro());
                cargarPomodoros();
            }
        };

        new ItemTouchHelper(swipeCallback).attachToRecyclerView(recyclerViewPomodoros);
    }

    private void createBuilder() {
        this.builder = new AlertDialog.Builder(this.context);
        builder.setTitle("Nombre del pomodoro");

        final EditText input = new EditText(this.context);
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        builder.setView(input);

        builder.setPositiveButton("OK", (dialog, which) ->{
            if(input.getText().toString().isEmpty()){
                input.setError("Campo requerido");
                return;
            }
            newPomodoro(input.getText().toString());
        });
        builder.setNegativeButton("Cancelar", (dialog, which) ->{
            dialog.cancel();
            createBuilder();
        } );
    }

    private void newPomodoro(String name) {
        Pomodoro pomodoro = new Pomodoro();
        pomodoro.setNombre(name);
        assert helper.insertarPomodoro(pomodoro) : "Error al insertar pomodoro";
        pomodoro = helper.getPomodoros().get(helper.getPomodoros().size() - 1);

        Intent myIntent = new Intent(this.context, TiemposActivity.class);
        myIntent.putExtra("idPomodoro", pomodoro.getIdPomodoro());
        startActivity(myIntent);
    }

    @Override
    public void onResume() {
        super.onResume();
        cargarPomodoros();
    }

    private void cargarPomodoros() {
        if(helper == null) helper = DBHelper.getInstance(this.context);
        List<Pomodoro> pomodoros = helper.getPomodoros();

        if(!pomodoros.isEmpty()){
            this.tvPomodorosNoData.setVisibility(View.GONE);
        } else{
            this.tvPomodorosNoData.setVisibility(View.VISIBLE);
        }

        PomodoroAdapter pomodoroAdapter = new PomodoroAdapter(pomodoros);
        pomodoroAdapter.setOnPomodoroClickListener(this);

        recyclerViewPomodoros.setAdapter(pomodoroAdapter);
        recyclerViewPomodoros.setLayoutManager(new LinearLayoutManager(this.context));
        recyclerViewPomodoros.addItemDecoration(new DividerItemDecoration(this.context, DividerItemDecoration.VERTICAL));
        recyclerViewPomodoros.setItemAnimator(new DefaultItemAnimator());
    }

    @Override
    public void onPomodoroClick(int position) {
        int idPomodoro = helper.getPomodoros().get(position).getIdPomodoro();
        Intent myIntent = new Intent(this.context, TiemposActivity.class);
        myIntent.putExtra("idPomodoro", idPomodoro);
        startActivity(myIntent);
    }
}