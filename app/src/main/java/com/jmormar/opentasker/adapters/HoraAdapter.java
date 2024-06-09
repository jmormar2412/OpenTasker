package com.jmormar.opentasker.adapters;

import static com.jmormar.opentasker.util.Constants.HOUR_HEIGHT;

import android.content.Context;
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
import com.jmormar.opentasker.models.Categoria;
import com.jmormar.opentasker.models.Hora;
import com.jmormar.opentasker.util.DBHelper;

import java.time.Duration;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import lombok.Setter;

public class HoraAdapter extends RecyclerView.Adapter<HoraAdapter.HoraViewHolder> {
    private final List<Hora> horas;
    private final DBHelper helper;
    private final String nombreDiaSemana;
    @Setter
    private OnHoraClickListener onHoraClickListener;

    public HoraAdapter(Context context, List<Hora> horas, String nombreDiaSemana) {
        horas.sort(Comparator.comparing(Hora::getTiempoInicio));
        this.helper = DBHelper.getInstance(context);
        this.horas = insertGaps(horas);
        this.nombreDiaSemana = nombreDiaSemana;
    }

    @NonNull
    @Override
    public HoraAdapter.HoraViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_hora, parent, false);
        return new HoraViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull HoraAdapter.HoraViewHolder holder, int position) {
        Hora hora = horas.get(position);

        holder.itemView.setOnClickListener(v -> {
            if (onHoraClickListener != null) onHoraClickListener.onHoraClick(hora.getIdHora());
        });

        holder.bind(hora);
    }

    @Override
    public int getItemCount() {
        return horas.size();
    }

    public class HoraViewHolder extends RecyclerView.ViewHolder {
        private final TextView horaTextView;
        private final View itemView;

        public HoraViewHolder(@NonNull View itemView) {
            super(itemView);
            horaTextView = itemView.findViewById(R.id.tv_acronimo_hora);
            this.itemView = itemView;
        }

        void bind(Hora hora) {
            ViewGroup.LayoutParams layoutParams = horaTextView.getLayoutParams();
            int durationInMinutes = (int) hora.getTotalTiempo().toMinutes();
            layoutParams.height = durationInMinutes * HOUR_HEIGHT;
            horaTextView.setLayoutParams(layoutParams);

            if (hora.isGap()) {
                itemView.setVisibility(View.INVISIBLE);
            } else {
                itemView.setVisibility(View.VISIBLE);
                if(hora.getIdCategoria() != -1){
                    Categoria categoria = helper.getCategoria(hora.getIdCategoria());
                    setBackgroundColor(categoria.getColor());
                    horaTextView.setText(categoria.getAcronimo());
                    return;
                }
                horaTextView.setText(HoraAdapter.this.nombreDiaSemana);
                setBackgroundColor(Color.GRAY);
            }
        }

        void setBackgroundColor(int color){
            LayerDrawable layerDrawable = (LayerDrawable) itemView.getBackground();
            GradientDrawable dynamicColorLayer = (GradientDrawable) layerDrawable.findDrawableByLayerId(R.id.dynamic_color_layer);
            dynamicColorLayer.setColor(color);
        }
    }

    private List<Hora> insertGaps(List<Hora> originalHoras) {
        List<Hora> adjustedHoras = new ArrayList<>();

        if (originalHoras.isEmpty()) {
            Hora dayNameHora = new Hora();
            dayNameHora.setTotalTiempo(Duration.ofHours(1));
            dayNameHora.setIdCategoria(-1);
            adjustedHoras.add(dayNameHora);
            return adjustedHoras;
        }

        Hora dayNameHora = new Hora();
        dayNameHora.setTotalTiempo(Duration.ofHours(1));
        dayNameHora.setIdCategoria(-1);
        adjustedHoras.add(dayNameHora);

        Hora firstHora = originalHoras.get(0);
        LocalTime minimumTime = Hora.horaMinima;

        if (minimumTime.isBefore(firstHora.getTiempoInicio())) {
            Duration gapDuration = Duration.between(minimumTime, firstHora.getTiempoInicio());
            Hora gapHora = new Hora();
            gapHora.setTiempoInicio(minimumTime);
            gapHora.setTotalTiempo(gapDuration);
            gapHora.setGap(true);
            adjustedHoras.add(gapHora);
        }

        for (int i = 0; i < originalHoras.size(); i++) {
            Hora currentHora = originalHoras.get(i);
            adjustedHoras.add(currentHora);

            if (i < originalHoras.size() - 1) {
                Hora nextHora = originalHoras.get(i + 1);
                LocalTime currentEndTime = currentHora.getTiempoInicio().plus(currentHora.getTotalTiempo());
                LocalTime nextStartTime = nextHora.getTiempoInicio();

                if (currentEndTime.isBefore(nextStartTime)) {
                    Duration gapDuration = Duration.between(currentEndTime, nextStartTime);
                    Hora gapHora = new Hora();
                    gapHora.setTiempoInicio(currentEndTime);
                    gapHora.setTotalTiempo(gapDuration);
                    gapHora.setGap(true);
                    adjustedHoras.add(gapHora);
                }
            }
        }

        return adjustedHoras;
    }

    public interface OnHoraClickListener{
        void onHoraClick(Integer idHora);
    }

}
