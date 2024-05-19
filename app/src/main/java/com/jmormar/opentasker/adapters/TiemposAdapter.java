package com.jmormar.opentasker.adapters;

import android.annotation.SuppressLint;
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
import com.jmormar.opentasker.models.Tiempo;

import java.util.List;

public class TiemposAdapter extends RecyclerView.Adapter<TiemposAdapter.TiemposViewHolder> {
    private final List<Tiempo> tiempos;

    public TiemposAdapter(List<Tiempo> tiempos) {
        this.tiempos = tiempos;
    }

    @NonNull
    @Override
    public TiemposAdapter.TiemposViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_tiempo, parent, false);
        return new TiemposViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull TiemposAdapter.TiemposViewHolder holder, int position) {
        Tiempo tiempo = tiempos.get(position);
        holder.bind(tiempo);
    }

    @Override
    public int getItemCount() {
        return tiempos.size();
    }

    public static class TiemposViewHolder extends RecyclerView.ViewHolder{
        int darkRed = Color.argb(100, 255, 0, 0);
        int darkGreen = Color.argb(100, 0, 255, 0);
        TextView tvTimer;
        boolean colored;
        View itemView;

        public TiemposViewHolder(@NonNull View itemView) {
            super(itemView);
            this.tvTimer = itemView.findViewById(R.id.tv_timer);
            this.itemView = itemView;
        }

        @SuppressLint({"SetTextI18n", "DefaultLocale"})
        public void bind(Tiempo tiempo){
            tvTimer.setText(((int) Math.floor((double) tiempo.getUpdatedSeconds() / 60)) + ":" + String.format("%02d", tiempo.getUpdatedSeconds() % 60));
            setBackgroundColor(tiempo.isRest()? darkGreen : darkRed);
            colored = true;
        }

        public void setBackgroundColor(int color){
            LayerDrawable layerDrawable = (LayerDrawable) itemView.getBackground();
            GradientDrawable dynamicColorLayer = (GradientDrawable) layerDrawable.findDrawableByLayerId(R.id.dynamic_color_layer);
            dynamicColorLayer.setColor(color);
        }
    }
}
