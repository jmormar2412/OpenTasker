package com.jmormar.opentasker.models;

import java.util.ArrayList;

import lombok.Data;

/**
 * <h1>Horario</h1>
 * <p>El horario va a tener las horas que hay a lo largo de la semana y las va a mostrar en una línea temporal con los días y todos (porqui)</p>
 */
@Data
public class Horario {
    //Decidir la forma de almacenar las horas, si directamente aquí o en arrays de días (me estoy muriendo ahora mismo)
    private int idHorario;
    private ArrayList<Hora> horas;
    private int idAgenda;
}