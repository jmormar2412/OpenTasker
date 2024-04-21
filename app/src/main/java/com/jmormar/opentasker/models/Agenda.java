package com.jmormar.opentasker.models;

import java.util.Date;

import lombok.Data;

/**
 * <h1>Agenda</h1>
 * <p>La clase Agenda es por así decirlo el centro donde las demás clases se concentran.</p>
 * <p>Es por esto que un usuario sólo va a tener una agenda. No va a crear más.</p>
 */
@Data
public class Agenda {
    //Necesitamos el id para modificar la agenda, por muy inútil que parezca (el id).
    private int idAgenda;
    private String nombre;
    private Date fechaInicio;
    private Date fechaFinal;
    private byte beginningDay;
    //Tenemos que saber la longitud de la semana fuck
    private byte weekLength;
    //Creo que voy a tener que almacenar todas las cosas aquí eh
}
