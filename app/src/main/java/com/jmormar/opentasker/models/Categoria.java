package com.jmormar.opentasker.models;

import lombok.Data;

/**
 * <h1>Categoría</h1>
 * <p>Esta clase define lo que es una categoría. Se podría ver como si fuese una asignatura.</p>
 */
@Data
public class Categoria {
    private int idCategoria;
    private String nombre;
    private int color;
    private int idAgenda;
}
