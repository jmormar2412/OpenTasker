package com.jmormar.opentasker.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.jmormar.opentasker.R;
import com.jmormar.opentasker.models.Categoria;
import com.jmormar.opentasker.models.Evento;
import com.jmormar.opentasker.models.Tipo;
import com.jmormar.opentasker.util.DBHelper;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class EventoAdapter extends RecyclerView.Adapter<EventoAdapter.EventoViewHolder> {
    private final List<Evento> eventos;
    private final DBHelper helper;
    private OnEventoClickListener mListener;
    private final String identificadorAdapter;


    public EventoAdapter(Context context, List<Evento> eventos, String identificadorAdapter) {
        this.eventos = eventos;
        helper = DBHelper.getInstance(context);
        this.identificadorAdapter = identificadorAdapter;
    }

    @NonNull
    @Override
    public EventoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_evento, parent, false);
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

    public void setOnEventoClickListener(OnEventoClickListener mListener) {
        this.mListener = mListener;
    }



    public class EventoViewHolder extends RecyclerView.ViewHolder {
        final TextView nombre;
        final TextView fecha;
        final TextView tipo;
        final TextView categoria;

        EventoViewHolder(@NonNull View itemView) {
            super(itemView);
            nombre = itemView.findViewById(R.id.evli_nombre);
            fecha = itemView.findViewById(R.id.evli_fecha);
            tipo = itemView.findViewById(R.id.evli_tipo);
            categoria = itemView.findViewById(R.id.evli_categoria);

            itemView.setOnClickListener(v -> {
                int position = getBindingAdapterPosition();
                if(position!=RecyclerView.NO_POSITION && mListener!=null) mListener.onEventoClick(position, identificadorAdapter);
            });
        }

        void bind(Evento evento) {
            nombre.setText(evento.getNombre());

            SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd", new Locale("es_ES"));
            fecha.setText(evento.getFecha() != null ? dateFormatter.format(evento.getFecha()) : "");

            Tipo tp = helper.getTipo(evento.getIdTipo());
            if (tp != null) tipo.setText(tp.getNombre());

            Categoria cat = helper.getCategoria(evento.getIdCategoria());
            if(cat != null){
                categoria.setText(cat.getNombre());
                itemView.setBackgroundColor(cat.getColor());
            }

            itemView.setVisibility(View.VISIBLE); // Make sure the itemView is visible
            itemView.setLayoutParams(new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        }
    }

    public interface OnEventoClickListener {
        void onEventoClick(int position, String identificadorAdapter);
    }
}