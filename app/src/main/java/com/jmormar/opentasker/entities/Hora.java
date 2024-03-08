package com.jmormar.opentasker.entities;

import java.time.LocalDateTime;
import java.time.Period;

import lombok.Data;

/**
 * <h1>Hora</h1>
 * <p>Las horas son segmentos de una duración determinada dispuestos en un horario. Pueden durar un tiempo definido por los atributos "Hora de inicio" y "Hora final".</p>
 * <p>El atributo Categoría determinará a qué asignatura pertenece. (Véase la clase Categoría para más información).</p>
 */
@Data
public class Hora {
    //Bua chaval vamos a utilizar la primera semana del mundo cíclicamente tio
    private int idHora;
    private LocalDateTime fechayTiempoInicio;
    private Period totalTiempo;
    private int idHorario;
}
