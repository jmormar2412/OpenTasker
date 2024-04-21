package com.jmormar.opentasker.models;

import lombok.Data;

/**
 * <h1>Tipo</h1>
 * <p>El tipo del evento puede ser un examen, una tarea o alguna otra cosa. Esto puede ser personalizado por el usuario.</p>
 */

@Data
public class Tipo {
    private int idTipo;
    private String nombre;
}
