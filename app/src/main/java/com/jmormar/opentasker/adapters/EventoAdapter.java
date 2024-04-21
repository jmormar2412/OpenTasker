package com.jmormar.opentasker.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.jmormar.opentasker.R;
import com.jmormar.opentasker.models.Evento;
import com.jmormar.opentasker.models.Tipo;
import com.jmormar.opentasker.util.DBHelper;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class EventoAdapter extends RecyclerView.Adapter<EventoAdapter.EventoViewHolder> {
    private List<Evento> eventos;
    private DBHelper helper;

    public EventoAdapter(Context context, List<Evento> eventos) {
        this.eventos = eventos;
        helper = DBHelper.getInstance(context);
    }

    @NonNull
    @Override
    public EventoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_evento, parent, false);
        return new EventoViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull EventoViewHolder holder, int position) {
        Evento evento = eventos.get(position);
        holder.bind(evento);
    }

    @Override
    public int getItemCount() {
        return eventos.size();
    }

    class EventoViewHolder extends RecyclerView.ViewHolder {
        TextView nombre;
        TextView fecha;
        TextView tipo;
        TextView categoria;

        EventoViewHolder(@NonNull View itemView) {
            super(itemView);
            nombre = itemView.findViewById(R.id.evli_nombre);
            fecha = itemView.findViewById(R.id.evli_fecha);
            tipo = itemView.findViewById(R.id.evli_tipo);
            categoria = itemView.findViewById(R.id.evli_categoria);
        }

        void bind(Evento evento) {
            nombre.setText(evento.getNombre());
            SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd");
            fecha.setText(evento.getFecha() != null ? dateFormatter.format(evento.getFecha()) : "");
            Tipo tp = helper.getTipo(evento.getIdTipo());
            tipo.setText(tp.getNombre());
        }
    }
}