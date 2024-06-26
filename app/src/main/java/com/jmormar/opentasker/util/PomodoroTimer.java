package com.jmormar.opentasker.util;

import android.os.Handler;

import androidx.recyclerview.widget.RecyclerView;

import com.jmormar.opentasker.models.Tiempo;

import lombok.Getter;
import lombok.Setter;

public class PomodoroTimer {
    private final long totalTime;
    private long remainingTime;
    private boolean isPaused;
    private final Handler handler;
    private Runnable timerRunnable;
    private final RecyclerView.Adapter<?> adapter;
    private final int position;
    @Getter
    private Tiempo tiempo;
    @Setter
    private TimerCallback timerCallback;

    public PomodoroTimer(Tiempo next, RecyclerView.Adapter<?> adapter, int position) {
        this.totalTime = next.getSetSeconds() * 1000L;
        this.remainingTime = next.getUpdatedSeconds() * 1000L;
        this.isPaused = false;
        this.handler = new Handler();
        this.adapter = adapter;
        this.position = position;
        this.tiempo = next;
        initTimerRunnable();
    }

    private void initTimerRunnable() {
        timerRunnable = new Runnable() {
            @Override
            public void run() {
                if (!isPaused) {
                    remainingTime -= 1000;
                    tiempo.setUpdatedSeconds(getRemainingTime());
                    updateRecyclerView();
                    handler.postDelayed(this, 1000);
                    if(remainingTime <= 0) {
                        if (timerCallback != null) {
                            timerCallback.onTimerFinished(tiempo.isRest());
                        }
                        handler.removeCallbacks(this);
                    }
                }
            }
        };
    }

    public void start() {
        isPaused = false;
        handler.postDelayed(timerRunnable, 1000);
    }

    public void pause() {
        isPaused = true;
    }

    public void reset() {
        isPaused = true;
        remainingTime = totalTime;
        tiempo.setUpdatedSeconds(getRemainingTime());
        updateRecyclerView();
    }

    private void updateRecyclerView() {
        if (adapter != null) {
            adapter.notifyItemChanged(position);
        }
    }

    public interface TimerCallback{
        void onTimerFinished(boolean isRest);
    }

    public int getRemainingTime() {
        return ((int) (remainingTime / 1000));
    }
}

