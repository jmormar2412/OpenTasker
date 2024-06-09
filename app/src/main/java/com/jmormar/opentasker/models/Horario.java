package com.jmormar.opentasker.models;

import lombok.Data;

/**
 * <h1>Horario</h1>
 * <p>El horario va a tener las horas que hay a lo largo de la semana y las va a mostrar en una línea temporal con los días y todos (porqui)</p>
 */
@Data
public class Horario {
    private int idHorario;
    private int idAgenda;
}
