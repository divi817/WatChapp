package es.fempa.pmdm.whatchapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

public class ConfActivity extends AppCompatActivity {

    TextView tvip;
    EditText etip;
    Button btnconn;
    EditText etport;
    SendMens sm;
    EditText etnom;

    @Override
      protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_conf);

        tvip = (TextView) findViewById(R.id.textView);
        etip = (EditText) findViewById(R.id.direccionEt);
        btnconn = (Button) findViewById(R.id.connBut);
        etport = (EditText) findViewById(R.id.portEt);
        etnom = (EditText) findViewById(R.id.usuEt);

        if(getIntent().getStringExtra("tipo") != ""){ //CAMBIAMOS FORMULARIO PARA CLIENTE O SERVIIDOR
            if(getIntent().getStringExtra("tipo").equals("server")){
                tvip.setVisibility(View.INVISIBLE);
                etip.setVisibility(View.INVISIBLE);
                btnconn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        connS(v);
                    }
                });
            }
            else{
                btnconn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        connC(v);
                    }
                });
            }
        }
    }

    public void volver(View v){
        finish();
    }

    public void connC(View v){
        String p = etport.getText().toString(); //OBTENEMOS DATOS Y ENVIAMOS EN EL INTENT
        String i = etip.getText().toString();
        String n = etnom.getText().toString();
        Intent it = new Intent(this, ChatActivity.class);
        it.putExtra("puerto", p);
        it.putExtra("ip", i);
        it.putExtra("nombre", n);
        startActivity(it);
    }

    public void connS(View v){
        String p = etport.getText().toString();//OBTENEMOS DATOS Y ENVIAMOS EN EL INTENT
        String n = etnom.getText().toString();
        Intent it = new Intent(this, ChatActivity.class);
        it.putExtra("puerto", p);
        it.putExtra("ip", "servidor");
        it.putExtra("nombre", n);
        startActivity(it);
    }
}
