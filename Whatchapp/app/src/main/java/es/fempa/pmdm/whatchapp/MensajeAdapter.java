package es.fempa.pmdm.whatchapp;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import java.util.List;

public class MensajeAdapter extends ArrayAdapter<Mensaje> {

    public MensajeAdapter(Context context, int resource, List<Mensaje> objects) {
        super(context, resource, objects);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent){
        if(convertView==null){
            convertView = LayoutInflater.from(this.getContext()).inflate(R.layout.mensaje_layout, parent, false);
        }

        TextView tvMns = (TextView) convertView.findViewById(R.id.mensajeText);
        TextView tvUsr = (TextView) convertView.findViewById(R.id.userText);
        TextView tvFec = (TextView)convertView.findViewById(R.id.fechaText);

        Mensaje f = getItem(position);
        if(f!=null) {
            tvMns.setText(f.contenido);
            tvUsr.setText(f.autor);
            tvFec.setText(f.fecha);
        }

        return convertView;
    }

}