package com.jmormar.opentasker.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.jmormar.opentasker.R;
import com.jmormar.opentasker.models.Categoria;
import com.jmormar.opentasker.models.Evento;
import com.jmormar.opentasker.models.Tipo;
import com.jmormar.opentasker.util.DBHelper;

import java.text.SimpleDateFormat;
import java.util.List;

public class EventoAdapter extends RecyclerView.Adapter<EventoAdapter.EventoViewHolder> {
    private List<Evento> eventos;
    private DBHelper helper;
    private OnEventoClickListener mListener;
    private boolean showHechos;



    public EventoAdapter(Context context, List<Evento> eventos, boolean showHechos) {
        this.eventos = eventos;
        helper = DBHelper.getInstance(context);
        this.showHechos = showHechos;
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

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    if(position!=RecyclerView.NO_POSITION && mListener!=null) mListener.onEventoClick(position);
                }
            });
        }

        void bind(Evento evento) {
            if (showHechos || !evento.isHecho()) {
                nombre.setText(evento.getNombre());
                SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd");
                fecha.setText(evento.getFecha() != null ? dateFormatter.format(evento.getFecha()) : "");
                Tipo tp = helper.getTipo(evento.getIdTipo());
                tipo.setText(tp.getNombre());
                Categoria cat = helper.getCategoria(evento.getIdCategoria());
                categoria.setText(cat.getNombre());
                itemView.setVisibility(View.VISIBLE); // Make sure the itemView is visible
                itemView.setLayoutParams(new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)); // Reset the layout params to default
            } else {
                itemView.setVisibility(View.GONE);
                itemView.setLayoutParams(new RecyclerView.LayoutParams(0, 0));
            }
        }
    }

    public interface OnEventoClickListener {
        void onEventoClick(int position);
    }
}