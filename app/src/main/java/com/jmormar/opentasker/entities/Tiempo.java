package com.jmormar.opentasker.entities;

import java.time.Period;

import lombok.Data;

/**
 * <h1>Tiempo</h1>
 * <p>Los tiempos del pomodoro serán representados por esta clase. Pueden ser tanto tiempos normales como descansos.</p>
 */
@Data
public class Tiempo {
    private int idTiempo;
    private String nombre;
    private Period tiempo;
    private boolean rest;
    private int idPomodoro;
}
