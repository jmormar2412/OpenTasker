package com.jmormar.opentasker.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.jmormar.opentasker.R;
import com.jmormar.opentasker.models.Categoria;

import java.util.List;

import lombok.Setter;

public class CategoriaAdapter extends RecyclerView.Adapter<CategoriaAdapter.CategoriaViewHolder>{
    private List<Categoria> categorias;
    @Setter
    private OnCategoriaClickListener onCategoriaClickListener;

    public CategoriaAdapter(List<Categoria> categorias) {
        this.categorias = categorias;
    }

    @NonNull
    @Override
    public CategoriaAdapter.CategoriaViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_categoria, parent, false);
        return new CategoriaAdapter.CategoriaViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CategoriaAdapter.CategoriaViewHolder holder, int position) {
        Categoria categoria = categorias.get(position);
        holder.bind(categoria);
    }

    @Override
    public int getItemCount() {
        return categorias.size();
    }

    public class CategoriaViewHolder extends RecyclerView.ViewHolder{
        private TextView nombre;
        private View vColor;

        public CategoriaViewHolder(@NonNull View itemView) {
            super(itemView);
            this.nombre = itemView.findViewById(R.id.tv_nombre);
            this.vColor = itemView.findViewById(R.id.v_color);

            itemView.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if(position != RecyclerView.NO_POSITION && onCategoriaClickListener != null) onCategoriaClickListener.onCategoriaClick(position);
            });
        }

        void bind(Categoria categoria){
            nombre.setText(categoria.getNombre());
            vColor.setBackgroundColor(categoria.getColor());
        }
    }

    public interface OnCategoriaClickListener{
        void onCategoriaClick(int position);
    }
}
