package com.jmormar.opentasker.entities;

import lombok.Data;

/**
 * <h1>Pomodoro</h1>
 * <p>La clase pomodoro consta de tiempos y descansos, almacenados en una lista.</p>
 * <p>Se le dará al usuario la posibilidad de crear varios pomodoros, cada uno con sus tiempos y descansos.</p>
 */
@Data
public class Pomodoro {
    private int idPomodoro;
    private String nombre;
    // No se necesita lista de tiempos porque se guardarán todos los tiempos de un pomodoro bajo el mismo id de pomodoro.
}
