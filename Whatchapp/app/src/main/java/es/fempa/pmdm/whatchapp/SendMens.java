package es.fempa.pmdm.whatchapp;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;

public class SendMens extends LinearLayout {
    //Java del componente compuesto para enviar mensajes
    EditText et;
    Button btn;

    public SendMens(Context context) {
        super(context);
        String cont = Context.LAYOUT_INFLATER_SERVICE;
        LayoutInflater li = (LayoutInflater) getContext().getSystemService(cont);
        li.inflate(R.layout.send_mens, this, true);

        et = (EditText) findViewById(R.id.sendText);
        btn = (Button) findViewById(R.id.sendBtn);
    }

    public SendMens(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        String cont = Context.LAYOUT_INFLATER_SERVICE;
        LayoutInflater li = (LayoutInflater) getContext().getSystemService(cont);
        li.inflate(R.layout.send_mens, this, true);

        et = (EditText) findViewById(R.id.sendText);
        btn = (Button) findViewById(R.id.sendBtn);
    }

}
