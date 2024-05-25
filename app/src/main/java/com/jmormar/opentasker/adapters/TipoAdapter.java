package com.jmormar.opentasker.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.jmormar.opentasker.R;
import com.jmormar.opentasker.models.Tipo;

import java.util.List;

import lombok.Setter;

public class TipoAdapter extends RecyclerView.Adapter<TipoAdapter.TipoViewHolder> {
    private final List<Tipo> tipos;
    @Setter
    private OnTipoClickListener onTipoClickListener;

    public TipoAdapter(List<Tipo> tipos) {
        this.tipos = tipos;
    }
    @NonNull
    @Override
    public TipoAdapter.TipoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_tipo, parent, false);
        return new TipoAdapter.TipoViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull TipoAdapter.TipoViewHolder holder, int position) {
        Tipo tipo = tipos.get(position);
        holder.bind(tipo);
    }

    @Override
    public int getItemCount() {
        return tipos.size();
    }

    public class TipoViewHolder extends RecyclerView.ViewHolder {
        TextView nombre;

        TipoViewHolder(@NonNull View itemView) {
            super(itemView);
            this.nombre = itemView.findViewById(R.id.tv_nombre);

            itemView.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if(position!=RecyclerView.NO_POSITION && onTipoClickListener != null) onTipoClickListener.onTipoClick(position);
            });
        }

        void bind(Tipo tipo) {
            nombre.setText(tipo.getNombre());
        }
    }

    public interface OnTipoClickListener {
        void onTipoClick(int position);
    }
}
