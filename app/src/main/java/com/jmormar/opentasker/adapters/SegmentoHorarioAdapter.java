package com.jmormar.opentasker.adapters;

import static com.jmormar.opentasker.util.Constants.HOUR_HEIGHT;

import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.LayerDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.jmormar.opentasker.R;
import com.jmormar.opentasker.models.Hora;
import com.jmormar.opentasker.util.TimeUtils;

import java.time.Duration;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

public class SegmentoHorarioAdapter extends RecyclerView.Adapter<SegmentoHorarioAdapter.SegmentoHorarioViewHolder> {
    private final List<Hora> horas;

    public SegmentoHorarioAdapter(LocalTime inicio, LocalTime fin) {
        this.horas = generateHoras(inicio, fin);
    }

    @NonNull
    @Override
    public SegmentoHorarioAdapter.SegmentoHorarioViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_hora, parent, false);
        return new SegmentoHorarioViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull SegmentoHorarioViewHolder holder, int position) {
        Hora hora = horas.get(position);
        holder.bind(hora);
    }

    @Override
    public int getItemCount() {
        return horas.size();
    }

    public static class SegmentoHorarioViewHolder extends RecyclerView.ViewHolder {
        private final TextView horaTextView;
        private final View itemView;

        public SegmentoHorarioViewHolder(@NonNull View itemView) {
            super(itemView);
            horaTextView = itemView.findViewById(R.id.tv_acronimo_hora);
            this.itemView = itemView;
        }

        void bind(Hora hora) {
            ViewGroup.LayoutParams layoutParams = horaTextView.getLayoutParams();
            int durationInMinutes = (int) hora.getTotalTiempo().toMinutes();
            layoutParams.height = durationInMinutes * HOUR_HEIGHT;
            horaTextView.setLayoutParams(layoutParams);

            if(!hora.isGap()){
                itemView.setVisibility(View.VISIBLE);
                horaTextView.setText(TimeUtils.getBaseHour(hora.getTiempoInicio()));
                setBackgroundColor(Color.GRAY);
                return;
            }
            itemView.setVisibility(View.INVISIBLE);
        }

        public void setBackgroundColor(int color){
            LayerDrawable layerDrawable = (LayerDrawable) itemView.getBackground();
            GradientDrawable dynamicColorLayer = (GradientDrawable) layerDrawable.findDrawableByLayerId(R.id.dynamic_color_layer);
            dynamicColorLayer.setColor(color);
        }
    }

    private List<Hora> generateHoras(LocalTime inicio, LocalTime fin) {
        List<Hora> horas = new ArrayList<>();
        long minutesBetween = ChronoUnit.MINUTES.between(inicio, fin);

        Hora gapHora = new Hora();
        gapHora.setTotalTiempo(Duration.ofHours(1));
        gapHora.setGap(true);
        horas.add(gapHora);

        while (minutesBetween > 0) {
            Hora hora = new Hora();
            hora.setUpdateMinMax(false);
            hora.setTiempoInicio(inicio);

            long minutesUntilNextHour = Math.min(TimeUtils.minutesUntilNextHour(inicio), minutesBetween);
            hora.setTotalTiempo(Duration.ofMinutes(minutesUntilNextHour));

            horas.add(hora);

            inicio = inicio.plusMinutes(minutesUntilNextHour);
            minutesBetween -= minutesUntilNextHour;
        }

        return horas;
    }


}
