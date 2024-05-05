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
import com.jmormar.opentasker.util.DBHelper;

import java.util.List;

public class NotaAdapter extends RecyclerView.Adapter<NotaAdapter.NotaViewHolder> {
    private final List<Nota> notas;
    private final DBHelper helper;
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
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(onNoteClickListener != null) onNoteClickListener.onNoteClick(holder.getAdapterPosition());
            }
        });

        Nota nota = notas.get(position);
        holder.bind(nota);
    }

    @Override
    public int getItemCount() {
        return notas.size();
    }

    public void setOnNoteClickListener(OnNoteClickListener onNoteClickListener) {
        this.onNoteClickListener = onNoteClickListener;
    }

    public class NotaViewHolder extends RecyclerView.ViewHolder {
        TextView titulo;
        TextView texto;
        TextView categoria;

        NotaViewHolder(@NonNull View itemView) {
            super(itemView);
            titulo = itemView.findViewById(R.id.noli_titulo);
            texto = itemView.findViewById(R.id.noli_texto);
            categoria = itemView.findViewById(R.id.noli_categoria);
        }

        void bind(Nota nota) {
            titulo.setText(nota.getTitulo() == null?"":nota.getTitulo());
            if(nota.getIdCategoria() == -1){
                categoria.setText("");
            } else{
                Categoria cat = helper.getCategoria(nota.getIdCategoria());
                categoria.setText(cat.getNombre());
            }
            texto.setText(nota.getTexto()==null?"":nota.getTexto());
        }
    }

    public interface OnNoteClickListener{
        void onNoteClick(int position);
    }
}
