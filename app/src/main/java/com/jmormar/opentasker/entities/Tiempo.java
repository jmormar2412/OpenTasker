package com.jmormar.opentasker.entities;

import java.time.Period;

/**
 * <h1>Tiempo</h1>
 * <p>Los tiempos del pomodoro serán representados por esta clase. Pueden ser tanto tiempos normales como descansos.</p>
 */
public class Tiempo {
    private String nombre;
    //Como se almacena esto en la base de datos tio
    private Period tiempo;
    //No permitir poner nombre en rest (o sí no se)
    private boolean rest;
}
