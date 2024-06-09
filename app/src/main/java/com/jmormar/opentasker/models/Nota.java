package com.jmormar.opentasker.models;


import lombok.Data;

/**
 * <h1>Nota</h1>
 * <p>Esta clase representa una nota, no una nota de examen ni de tarea, sino una nota de texto.</p>
 * <p>Una nota puede pertenecer a una categoría a modo de memoria y un color.</p>
 */
@Data
public class Nota {
    private int idNota;
    private String titulo;
    private String texto;
    private int color;
    private int idCategoria;
}
