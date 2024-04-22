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
    private String nombre;
    private String texto;
    private int color;
    //Almacenar categoría de la nota.
    private int idCategoria;
    //Almacenar color de la nota.

    // Dar opción para heredar color de la categoría. Se hará con un setter en una clase externa
}
