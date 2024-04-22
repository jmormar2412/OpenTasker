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
import com.jmormar.opentasker.models.Nota;
import com.jmormar.opentasker.models.Tipo;
import com.jmormar.opentasker.util.DBHelper;

import java.text.SimpleDateFormat;
import java.util.List;

public class NotaAdapter extends RecyclerView.Adapter<NotaAdapter.NotaViewHolder> {
    private List<Nota> notas;
    private DBHelper helper;

    public NotaAdapter(Context context, List<Nota> notas) {
        this.notas = notas;
        helper = DBHelper.getInstance(context);
    }

    @NonNull
    @Override
    public NotaAdapter.NotaViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_evento, parent, false);
        return new NotaAdapter.NotaViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull NotaAdapter.NotaViewHolder holder, int position) {
        Nota evento = notas.get(position);
        holder.bind(evento);
    }

    @Override
    public int getItemCount() {
        return notas.size();
    }

    class NotaViewHolder extends RecyclerView.ViewHolder {
        TextView nombre;
        TextView fecha;
        TextView tipo;
        TextView categoria;

        NotaViewHolder(@NonNull View itemView) {
            super(itemView);
            nombre = itemView.findViewById(R.id.evli_nombre);
            fecha = itemView.findViewById(R.id.evli_fecha);
            tipo = itemView.findViewById(R.id.evli_tipo);
            categoria = itemView.findViewById(R.id.evli_categoria);
        }

        void bind(Nota evento) {
//            nombre.setText(evento.getNombre());
//            SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd");
//            fecha.setText(evento.getFecha() != null ? dateFormatter.format(evento.getFecha()) : "");
//            Tipo tp = helper.getTipo(evento.getIdTipo());
//            tipo.setText(tp.getNombre());
//            Categoria cat = helper.getCategoria(evento.getIdCategoria());
//            categoria.setText(cat.getNombre());
        }
    }
}
