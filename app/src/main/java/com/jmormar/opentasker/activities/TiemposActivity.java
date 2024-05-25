package com.jmormar.opentasker.activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Rect;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.jmormar.opentasker.R;
import com.jmormar.opentasker.activities.objectbuilders.NewTiempoActivity;
import com.jmormar.opentasker.adapters.TiemposAdapter;
import com.jmormar.opentasker.models.Pomodoro;
import com.jmormar.opentasker.models.Tiempo;
import com.jmormar.opentasker.util.DBHelper;
import com.jmormar.opentasker.util.PomodoroTimer;

import java.util.List;

public class TiemposActivity extends AppCompatActivity implements PomodoroTimer.TimerCallback {
    private Pomodoro pomodoro;
    private DBHelper helper;
    private TextView tvNombre;
    private RecyclerView rvTiempos;
    private ImageButton ibAdd, ibStartPause, ibReset, ibDelete;
    private CheckBox cbLooping;
    private boolean running, looping;
    private List<Tiempo> tiempos;
    private PomodoroTimer timer;
    private int position;
    private MediaPlayer aDescansar, lockIn, finish;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_tiempos);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        setElements();
        setListeners();
        setOnBackPressedCallback();
        loadDataFromIntent();
        getPosition();
    }

    @SuppressLint("NotifyDataSetChanged")
    @Override
    protected void onResume() {
        super.onResume();
        updateAdapter();
    }

    private void updateAdapter() {
        this.tiempos = helper.getTiempos(this.pomodoro.getIdPomodoro());
        this.rvTiempos.setAdapter(new TiemposAdapter(tiempos));
    }

    private void getPosition() {
        this.position = helper.getPosition(this.pomodoro.getIdPomodoro());
    }

    private void setOnBackPressedCallback() {
        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                wrapUp();
            }
        };
        getOnBackPressedDispatcher().addCallback(this, callback);
    }

    private void wrapUp(){
        int idPomodoro = this.pomodoro.getIdPomodoro();
        assert helper.actualizarOrInsertPosition(idPomodoro, position) : "No se pudo actualizar/insertar la posición";
        if(timer != null) pauseTimer();
        saveTiempos();
        finish();
    }

    private void saveTiempos() {
        tiempos.forEach(helper::actualizarTiempo);
    }

    private void setListeners() {

        this.ibAdd.setOnClickListener(v -> {
            if(running){
                Toast.makeText(this, "Pausa el pomodoro para editarlo", Toast.LENGTH_SHORT).show();
                return;
            }
            Intent myIntent = new Intent(this, NewTiempoActivity.class);
            myIntent.putExtra("idPomodoro", this.pomodoro.getIdPomodoro());

            if(!tiempos.isEmpty()){
                boolean activateRest = !tiempos.get(tiempos.size() - 1).isRest();
                myIntent.putExtra("activateRest", activateRest);
            }
            startActivity(myIntent);
        });

        this.ibStartPause.setOnClickListener(v -> {
            if(!running){
                if(!this.tiempos.isEmpty()){
                    this.ibStartPause.setImageResource(R.drawable.baseline_pause_circle_24);
                    startTimerWithTiempo(tiempos.get(position));
                }
                else Toast.makeText(this, "Añade primero algún tiempo", Toast.LENGTH_SHORT).show();
            } else{
                this.ibStartPause.setImageResource(R.drawable.baseline_play_circle_24);
                pauseTimer();
            }
        });

        this.ibReset.setOnClickListener(v -> reset());

        this.ibDelete.setOnClickListener(v -> {
            if(running){
                Toast.makeText(this, "Resetea el pomodoro para borrar tiempos", Toast.LENGTH_SHORT).show();
                return;
            }

            if(position >= tiempos.size()) position = 0;

            if(!tiempos.isEmpty()){
                assert helper.deleteTiempo(tiempos.get(tiempos.size()-1).getIdTiempo()) : "No se pudo borrar el tiempo";
                tiempos.remove(tiempos.size()-1);
                updateAdapter();
            } else{
                Toast.makeText(this, "No hay tiempos para borrar", Toast.LENGTH_SHORT).show();
            }
        });

        this.cbLooping.setOnCheckedChangeListener((buttonView, isChecked) -> {
            looping = isChecked;
        });

    }

    private void reset() {
        this.ibStartPause.setImageResource(R.drawable.baseline_play_circle_24);
        if(timer!=null) timer.reset();
        resetAllTiempos();
        this.position = 0;
        running = false;
    }

    private void pauseTimer() {
        timer.pause();
        Tiempo tiempo = timer.getTiempo();
        tiempo.setUpdatedSeconds(timer.getRemainingTime());
        running = false;
    }

    private void startTimerWithTiempo(Tiempo tiempo) {
        timer = new PomodoroTimer(tiempo, rvTiempos.getAdapter(), position);
        timer.setTimerCallback(this);
        timer.start();
        running = true;
    }

    private void setElements() {
        this.helper = DBHelper.getInstance(this);
        this.tvNombre = findViewById(R.id.tv_pomodoro_nombre);
        this.rvTiempos = findViewById(R.id.rv_tiempos);

        this.ibAdd = findViewById(R.id.add_tiempo);
        this.ibStartPause = findViewById(R.id.ib_startpause);
        this.ibReset = findViewById(R.id.ib_reset);

        this.aDescansar = MediaPlayer.create(this, R.raw.ringsound);
        this.lockIn = MediaPlayer.create(this, R.raw.lockin);
        this.finish = MediaPlayer.create(this, R.raw.success);

        this.cbLooping = findViewById(R.id.cb_looping);
        this.ibDelete = findViewById(R.id.ib_delete);
    }

    private void loadDataFromIntent() {
        Intent intent = getIntent();
        int idPomodoro = intent.getIntExtra("idPomodoro", -1);
        this.pomodoro = helper.getPomodoro(idPomodoro);

        assert pomodoro != null : "El pomodoro no puede ser nulo";

        this.tvNombre.setText(this.pomodoro.getNombre());
        this.tiempos = this.helper.getTiempos(this.pomodoro.getIdPomodoro());

        this.rvTiempos.setAdapter(new TiemposAdapter(tiempos));
        this.rvTiempos.setLayoutManager(new GridLayoutManager(this, 3));

        this.rvTiempos.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
                super.getItemOffsets(outRect, view, parent, state);
                outRect.set(5,5,5,5);
            }
        });
    }

    @Override
    public void onTimerFinished(boolean isRest) {
        this.position++;

        if(this.position >= this.tiempos.size()) {
            if (looping) {
                if(isRest) playSound(2);
                else playSound(1);

                resetAllTiempos();
                this.position = 0;
                startTimerWithTiempo(tiempos.get(position));
            } else{
                this.position = 0;
                reset();
                resetAllTiempos();
                playSound(3);
            }
        } else {
            if(isRest) playSound(2);
            else playSound(1);

            startTimerWithTiempo(tiempos.get(position));
        }
    }

    private void playSound(int soundNumber){
        switch (soundNumber){
            case 1 -> {
                if(aDescansar.isPlaying()) aDescansar.stop();
                aDescansar.start();
            }
            case 2 -> {
                if(lockIn.isPlaying()) lockIn.stop();
                lockIn.start();
            }
            case 3 -> {
                if(finish.isPlaying()) finish.stop();
                finish.start();
            }
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private void resetAllTiempos(){
        tiempos.forEach(Tiempo::resetSeconds);
        assert rvTiempos.getAdapter() != null : "El adapter no puede ser nulo";
        rvTiempos.getAdapter().notifyDataSetChanged();
    }
}