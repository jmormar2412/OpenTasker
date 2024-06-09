package com.jmormar.opentasker.models;

import java.time.Duration;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import lombok.Data;
import lombok.Setter;

/**
 * <h1>Hora</h1>
 * <p>Las horas son segmentos de una duración determinada dispuestos en un horario. Pueden durar un tiempo definido por los atributos "Hora de inicio" y "Hora final".</p>
 * <p>El atributo Categoría determinará a qué asignatura pertenece. (Véase {@link Categoria} para más información).</p>
 */
@Data
public class Hora {
    private Integer idHora;
    private LocalTime tiempoInicio;
    private Duration totalTiempo;
    private int diaSemana;
    private int idHorario;
    private int idCategoria;
    private boolean gap;
    public static LocalTime horaMinima, horaMaxima;
    @Setter
    private boolean updateMinMax = true;
    private static List<Hora> allHoras = new ArrayList<>();

    public Hora() {
        allHoras.add(this);
    }

    public void setTiempoInicio(LocalTime tiempoInicio) {
        this.tiempoInicio = tiempoInicio;
        if (totalTiempo != null && updateMinMax) {
            updateMinMax();
        }
    }

    public void setTotalTiempo(Duration totalTiempo) {
        this.totalTiempo = totalTiempo;
        if (tiempoInicio != null && updateMinMax) {
            updateMinMax();
        }
    }

    private static void updateMinMax() {
        horaMinima = allHoras.stream()
                .map(Hora::getTiempoInicio)
                .filter(Objects::nonNull)
                .min(LocalTime::compareTo)
                .orElse(null);

        horaMaxima = allHoras.stream()
                .filter(h -> h.getTiempoInicio() != null && h.getTotalTiempo() != null)
                .map(h -> h.getTiempoInicio().plus(h.getTotalTiempo()))
                .max(LocalTime::compareTo)
                .orElse(null);
    }

    public static void resetList(){
        allHoras = new ArrayList<>();
    }

}
