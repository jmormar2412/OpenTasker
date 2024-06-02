package com.jmormar.opentasker.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.jmormar.opentasker.R;
import com.jmormar.opentasker.models.Pomodoro;

import java.util.List;

public class PomodoroAdapter extends RecyclerView.Adapter<PomodoroAdapter.PomodoroViewHolder> {

    private final List<Pomodoro> pomodoros;
    private PomodoroAdapter.OnPomodoroClickListener mListener;


    public PomodoroAdapter(List<Pomodoro> pomodoros) {
        this.pomodoros = pomodoros;
    }

    @NonNull
    @Override
    public PomodoroAdapter.PomodoroViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_pomodoro, parent, false);
        return new PomodoroAdapter.PomodoroViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull PomodoroAdapter.PomodoroViewHolder holder, int position) {
        Pomodoro pomodoro = pomodoros.get(position);
        holder.bind(pomodoro);
    }

    @Override
    public int getItemCount() {
        return pomodoros.size();
    }

    public void setOnPomodoroClickListener(PomodoroAdapter.OnPomodoroClickListener mListener) {
        this.mListener = mListener;
    }

    public class PomodoroViewHolder extends RecyclerView.ViewHolder {
        final TextView nombre;
        final ImageView editar;

        PomodoroViewHolder(@NonNull View itemView) {
            super(itemView);
            nombre = itemView.findViewById(R.id.poli_nombre);
            editar = itemView.findViewById(R.id.iv_edit);

            itemView.setOnClickListener(v -> {
                int position = getBindingAdapterPosition();
                if(position!=RecyclerView.NO_POSITION && mListener!=null) mListener.onPomodoroClick(position);
            });
            editar.setOnClickListener(v -> {
                int position = getBindingAdapterPosition();
                if(position!=RecyclerView.NO_POSITION && mListener!=null) mListener.onPomodoroEdit(position);
            });
        }

        void bind(Pomodoro pomodoro) {
            nombre.setText(pomodoro.getNombre());
        }
    }

    public interface OnPomodoroClickListener {
        void onPomodoroClick(int position);
        void onPomodoroEdit(int position);
    }

}
