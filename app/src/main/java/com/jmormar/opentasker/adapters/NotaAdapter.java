package com.jmormar.opentasker.adapters;

import android.content.Context;
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
import com.jmormar.opentasker.models.Nota;
import com.jmormar.opentasker.util.DBHelper;

import java.util.List;

import lombok.Setter;

public class NotaAdapter extends RecyclerView.Adapter<NotaAdapter.NotaViewHolder> {
    private final List<Nota> notas;
    private final DBHelper helper;
    @Setter
    private OnNoteClickListener onNoteClickListener;
    public NotaAdapter(Context context, List<Nota> notas) {
        this.notas = notas;
        helper = DBHelper.getInstance(context);
    }

    @NonNull
    @Override
    public NotaAdapter.NotaViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_nota, parent, false);
        return new NotaAdapter.NotaViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull NotaAdapter.NotaViewHolder holder, int position) {
        holder.itemView.setOnClickListener(v -> {
            if(onNoteClickListener != null) onNoteClickListener.onNoteClick(holder.getBindingAdapterPosition());
        });

        Nota nota = notas.get(position);
        holder.bind(nota);
    }

    @Override
    public int getItemCount() {
        return notas.size();
    }

    public class NotaViewHolder extends RecyclerView.ViewHolder {
        final TextView titulo;
        final TextView texto;
        final TextView categoria;
        final View itemView;

        NotaViewHolder(@NonNull View itemView) {
            super(itemView);
            titulo = itemView.findViewById(R.id.noli_titulo);
            texto = itemView.findViewById(R.id.noli_texto);
            categoria = itemView.findViewById(R.id.noli_categoria);
            this.itemView = itemView;
        }

        void bind(Nota nota) {
            titulo.setText(nota.getTitulo() == null?"":nota.getTitulo());
            if(nota.getIdCategoria() == -1){
                categoria.setVisibility(View.GONE);
            } else{
                categoria.setVisibility(View.VISIBLE);
                Categoria cat = helper.getCategoria(nota.getIdCategoria());
                if(cat != null) categoria.setText(cat.getNombre());
            }
            texto.setText(nota.getTexto()==null?"":nota.getTexto());
            setBackgroundColor(nota.getColor());
        }

        public void setBackgroundColor(int color){
            LayerDrawable layerDrawable = (LayerDrawable) itemView.getBackground();
            GradientDrawable dynamicColorLayer = (GradientDrawable) layerDrawable.findDrawableByLayerId(R.id.dynamic_color_layer);
            dynamicColorLayer.setColor(color);
        }
    }

    public interface OnNoteClickListener{
        void onNoteClick(int position);
    }
}
