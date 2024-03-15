package com.jmormar.opentasker.entities;

import java.util.Date;

import lombok.Data;

/**
 * <h1>Evento</h1>
 * <p>La clase Evento se podría ver como si fuese una tarea o un examen, dependiendo del tipo (Véase clase Tipo para más información).</p>
 */
@Data
public class Evento {
    private int idEvento;
    private String nombre;
    private Date fecha;
    //Ver como se almacenarían ids de tipo, categoría y agenda.
    private boolean hecho;
    private int idTipo;
    private int idCategoria;
    private int idAgenda;
}
