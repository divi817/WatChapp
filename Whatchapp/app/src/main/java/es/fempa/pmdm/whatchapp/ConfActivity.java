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

    @Override
      protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_conf);

        tvip = (TextView) findViewById(R.id.textView);
        etip = (EditText) findViewById(R.id.direccionEt);
        btnconn = (Button) findViewById(R.id.connBut);
        etport = (EditText) findViewById(R.id.portEt);

        if(getIntent().getStringExtra("tipo") != ""){
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
        String p = etport.getText().toString();
        String i = etip.getText().toString();
        Intent it = new Intent(this, ChatActivity.class);
        it.putExtra("puerto", p);
        it.putExtra("ip", i);
        startActivity(it);
    }

    public void connS(View v){
        String p = etport.getText().toString();
        Intent it = new Intent(this, ChatActivity.class);
        it.putExtra("puerto", p);
        it.putExtra("ip", getIpAddress());
        startActivity(it);
    }

    //Aqui obtenemos la IP de nuestro terminal
    private String getIpAddress()
    {
        String ip = "";
        try
        {
            Enumeration<NetworkInterface> enumNetworkInterfaces = NetworkInterface.getNetworkInterfaces();
            while (enumNetworkInterfaces.hasMoreElements())
            {
                NetworkInterface networkInterface = enumNetworkInterfaces.nextElement();
                Enumeration<InetAddress> enumInetAddress = networkInterface.getInetAddresses();
                while (enumInetAddress.hasMoreElements())
                {
                    InetAddress inetAddress = enumInetAddress.nextElement();

                    if (inetAddress.isSiteLocalAddress())
                    {
                        ip += "IP de Servidor: " + inetAddress.getHostAddress() + "\n";
                    }

                }
            }
        } catch (SocketException e)
        {
            e.printStackTrace();
            ip += "¡Algo fue mal! " + e.toString() + "\n";
        }

        return ip;
    }
}
