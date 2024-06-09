package com.jmormar.opentasker.widgets;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.AdapterView;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.jmormar.opentasker.R;
import com.jmormar.opentasker.models.Categoria;
import com.jmormar.opentasker.models.Evento;
import com.jmormar.opentasker.util.DBHelper;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

public class EventosRemoteViewsFactory implements RemoteViewsService.RemoteViewsFactory {
    private final Context context;
    private List<Evento> eventos;
    private DBHelper helper;

    public EventosRemoteViewsFactory(Context context) {
        this.context = context;
    }

    @Override
    public void onCreate() {
        this.helper = DBHelper.getInstance(context);
        this.eventos = helper.getEventos().stream().filter(e -> !e.isHecho()).collect(Collectors.toList());
    }

    @Override
    public void onDataSetChanged() {
        this.eventos = helper.getEventos().stream().filter(e -> !e.isHecho()).collect(Collectors.toList());
    }

    @Override
    public void onDestroy() {
        this.eventos.clear();
    }

    @Override
    public int getCount() {
        return eventos.size();
    }

    @Override
    public RemoteViews getViewAt(int position) {
        if(position == AdapterView.INVALID_POSITION || position >= eventos.size()) return null;

        Evento evento = eventos.get(position);
        SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy", new Locale("es"));
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.item_evento);

        views.setTextViewText(R.id.evli_nombre, evento.getNombre());
        views.setTextViewText(R.id.evli_fecha, format.format(evento.getFecha()));
        int idCategoria = evento.getIdCategoria();
        if(idCategoria != -1){
            Categoria categoria = helper.getCategoria(idCategoria);
            views.setTextViewText(R.id.evli_categoria, categoria.getNombre());
            views.setInt(R.id.item_evento_layout, "setBackgroundColor", categoria.getColor());
        }
        int idTipo = evento.getIdTipo();
        if(idTipo != -1){
            views.setTextViewText(R.id.evli_tipo, helper.getTipo(idTipo).getNombre());
        }

        views.setTextColor(R.id.evli_nombre, Color.WHITE);
        views.setTextColor(R.id.evli_fecha, Color.WHITE);
        views.setTextColor(R.id.evli_categoria, Color.WHITE);
        views.setTextColor(R.id.evli_tipo, Color.WHITE);

        Intent fillInIntent = new Intent();
        fillInIntent.putExtra("id", evento.getIdEvento());
        views.setOnClickFillInIntent(R.id.item_evento_layout, fillInIntent);

        return views;
    }

    @Override
    public RemoteViews getLoadingView() {
        return null;
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public long getItemId(int position) {
        return eventos.get(position).getIdEvento();
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }
}
