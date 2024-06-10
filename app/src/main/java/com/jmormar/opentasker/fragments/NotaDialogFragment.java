package com.jmormar.opentasker.fragments;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.LayerDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.DialogFragment;

import com.jmormar.opentasker.R;
import com.jmormar.opentasker.activities.modifiers.ModifyNotasActivity;
import com.jmormar.opentasker.models.Categoria;
import com.jmormar.opentasker.models.Nota;
import com.jmormar.opentasker.util.DBHelper;

import lombok.Setter;
import timber.log.Timber;

@Setter
public class NotaDialogFragment extends DialogFragment {
    private static Nota nota;
    private CargarNotasListener listener;
    private boolean showAbout;

    public static NotaDialogFragment newInstance(Nota displayNota) {
        NotaDialogFragment fragment = new NotaDialogFragment();
        nota = displayNota;
        return fragment;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        assert dialog.getWindow() != null;
        dialog.getWindow().setBackgroundDrawableResource(R.drawable.dialog_black_background);
        return dialog;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_nota_dialog, container, false);

        TextView tvTitulo = view.findViewById(R.id.tv_titulo), tvTexto = view.findViewById(R.id.tv_texto), tvCategoria = view.findViewById(R.id.tv_categoria);
        ImageButton btBorrar = view.findViewById(R.id.bt_borrar), btEditar = view.findViewById(R.id.bt_editar);

        if(showAbout){
            tvTitulo.setText(getString(R.string.about_app));
            tvTexto.setText(getString(R.string.about_text));
            btBorrar.setVisibility(View.GONE);
            btEditar.setVisibility(View.GONE);
            setDialogBackgroundColor(Color.GRAY);
            return view;
        }

        Context context = requireContext();
        DBHelper helper = DBHelper.getInstance(context);

        btBorrar.setVisibility(View.VISIBLE);
        btEditar.setVisibility(View.VISIBLE);

        if (nota != null) {
            tvTitulo.setText(nota.getTitulo());
            tvTexto.setText(nota.getTexto());
        }

        btBorrar.setOnClickListener(v ->
                new AlertDialog.Builder(context)
                .setTitle(getString(R.string.borrar_nota))
                .setMessage(getString(R.string.estas_seguro_de_borrar_fem) + getString(R.string.nota))
                .setPositiveButton(getString(R.string.si), (dialog, which) -> borrarNota(helper))
                .setNegativeButton(getString(R.string.no), (dialog, which) -> dialog.cancel())
                .show()
        );

        btEditar.setOnClickListener(v -> {
            Intent intent = new Intent(context, ModifyNotasActivity.class);
            intent.putExtra("idNota", nota.getIdNota());
            startActivity(intent);
            dismiss();
        });

        if(nota.getIdCategoria() == -1){
            tvCategoria.setVisibility(View.GONE);
        } else{
            tvCategoria.setVisibility(View.VISIBLE);
            Categoria cat = helper.getCategoria(nota.getIdCategoria());
            if(cat != null) tvCategoria.setText(cat.getNombre());
        }

        setDialogBackgroundColor(nota.getColor());

        return view;
    }

    private void borrarNota(DBHelper helper) {
        assert helper.deleteNota(nota.getIdNota());
        Timber.i("%s%s", getString(R.string.exito_borrando), getString(R.string.nota));
        listener.updateNotas();
        dismiss();
    }

    private void setDialogBackgroundColor(int color) {
        Dialog dialog = getDialog();
        if (dialog != null && dialog.getWindow() != null) {
            dialog.getWindow().getDecorView();
            GradientDrawable notaBackground = (GradientDrawable) ResourcesCompat.getDrawable(getResources(), R.drawable.dialog_rounded_corners, null);
            if (notaBackground != null) {
                notaBackground.setColor(color);

                LayerDrawable layerDrawable = new LayerDrawable(new Drawable[]{
                        ResourcesCompat.getDrawable(getResources(), R.drawable.dialog_black_background, null),
                        notaBackground
                });

                dialog.getWindow().setBackgroundDrawable(layerDrawable);
            }
        }
    }


    public interface CargarNotasListener{
        void updateNotas();
    }
}