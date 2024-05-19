package com.jmormar.opentasker.models;

import android.os.CountDownTimer;

import java.time.Duration;

import lombok.Data;

/**
 * <h1>Tiempo</h1>
 * <p>Los tiempos del pomodoro serán representados por esta clase. Pueden ser tanto tiempos normales como descansos.</p>
 */
@Data
public class Tiempo {
    private int idTiempo;
    private int setSeconds;
    private int updatedSeconds;
    private boolean rest;
    private int idPomodoro;
}
