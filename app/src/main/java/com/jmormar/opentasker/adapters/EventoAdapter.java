package com.jmormar.opentasker.adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.jmormar.opentasker.R;
import com.jmormar.opentasker.entities.Evento;
import com.jmormar.opentasker.entities.Tipo;
import com.jmormar.opentasker.util.DBHelper;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class EventoAdapter extends BaseAdapter {

    private Context context;
    private List<Evento> eventos;
    private EventosAdapterCallback callback;
    private DBHelper helper;

    public EventoAdapter(Context context, ArrayList<Evento> eventos){
        super();
        this.helper = DBHelper.getInstance(context);
        this.context=context;
        this.eventos=eventos;
    }

    @Override
    public int getCount() {
        return eventos.size();
    }

    @Override
    public Object getItem(int position) {
        if(eventos==null){
            return null;
        }
        return eventos.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        //Por favor no te cargues la totota :)
        if(eventos.isEmpty()) return null;

        View item=convertView;
        EventoWrapper evWrapper;
        if(item==null){
            evWrapper = new EventoWrapper();
            LayoutInflater inflater = ((Activity) context).getLayoutInflater();
            item = inflater.inflate(R.layout.list_evento, parent, false);
            evWrapper.nombre = item.findViewById(R.id.evli_nombre);
            evWrapper.fecha = item.findViewById(R.id.evli_fecha);
            evWrapper.tipo = item.findViewById(R.id.evli_tipo);
            evWrapper.categoria = item.findViewById(R.id.evli_categoria);
            item.setTag(evWrapper);
        } else{
            evWrapper=(EventoWrapper) item.getTag();
        }
        Evento evento = eventos.get(position);
        evWrapper.nombre.setText(evento.getNombre());
        SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd");
        if(evento.getFecha()!=null){
            evWrapper.fecha.setText(dateFormatter.format(evento.getFecha()));
        }
        else{
            evWrapper.fecha.setText("");
        }
        //TODO: preguntar al profe como no cargarme el móvil
        Tipo tp = helper.getTipo(evento.getIdTipo());
        evWrapper.tipo.setText(tp.getNombre());

        return item;
    }

    static class EventoWrapper{
        TextView nombre;
        TextView fecha;
        TextView tipo;
        TextView categoria;
    }

    public void setCallback(EventosAdapterCallback callback){
        this.callback = callback;
    }

    public interface EventosAdapterCallback{
        public void deletePressed(int position);
        public void editPressed(int position);
    }
}
