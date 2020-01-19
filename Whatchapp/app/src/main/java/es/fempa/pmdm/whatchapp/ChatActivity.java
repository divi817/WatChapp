package es.fempa.pmdm.whatchapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ListActivity;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.sql.BatchUpdateException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Enumeration;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import androidx.appcompat.app.AppCompatActivity;
public class ChatActivity extends AppCompatActivity {

    String user, ip, puerto;
    Boolean campos = false;

    TextView ipTv;
    SendMens sm;
    EditText mensTxt;
    ListView ch;
    Button envBtn;


    List<Mensaje> mensajes;
    MensajeAdapter ma;

    Socket socket;
    ServerSocket serverSocket;
    boolean ConectionEstablished;

    DataInputStream dataInputStream;
    DataOutputStream dataOutputStream;

    int mPuerto=4444;
    //Hilo para escuchar los mensajes que le lleguen por el socket
    GetMessagesThread HiloEscucha;


    /*Variable para el servidor*/
    WaitingClientThread HiloEspera;


    private void actualizar(){
        ma = new MensajeAdapter(this, R.layout.mensaje_layout, mensajes);
        ch.setAdapter(ma);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        sm = (SendMens) findViewById(R.id.enviarmensaje); //OBTENEMOS COMPONENTES
        ipTv = (TextView) findViewById(R.id.ipText);
        mensTxt = (EditText) findViewById(R.id.sendText);
        envBtn = (Button) findViewById(R.id.sendBtn);
        ch = (ListView) findViewById(R.id.chat);
        mensajes = new ArrayList<Mensaje>();

        envBtn.setOnClickListener(new View.OnClickListener() { //ACTIVAMOS ON CLICK DEL ENVIAR MENSAJE
            @Override
            public void onClick(View v) {
                String s = mensTxt.getText().toString();
                mensTxt.setText("");
                sendMessage(s);
            }
        });

        if(getIntent().getStringExtra("nombre") != "" && getIntent().getStringExtra("puerto") != "" &&
        getIntent().getStringExtra("ip") != ""){
            user = getIntent().getStringExtra("nombre");
            ip = getIntent().getStringExtra("ip");
            puerto = getIntent().getStringExtra("puerto");
            mPuerto = Integer.parseInt(puerto); //OBTENEMOS DATOS DE LA OTRA ACTIVIDAD Y CONECTAMOS
            if(ip.equals("servidor")){
                startServer();
            }
            else{
                startClient();
            }
            campos= true;
        }
        else{
            finish();
        }





    }

    public void volver(View v){
        ConectionEstablished=false;
        finish();
    }

    public void startServer()
    {

        (HiloEspera=new WaitingClientThread()).start();
    }

    public void startClient()
    {
        String TheIP=ip;
        if(TheIP.length()>1)
        {
            (new ClientConnectToServer(TheIP)).start();

            SetText("\nComenzamos Cliente!");
            AppenText("\nNos intentamos conectar al servidor: "+TheIP);
        }
    }

    public void AppenText(String text)
    {
        runOnUiThread(new appendUITextView(text+"\n"));
    }

    public void SetText(String text)
    {
        runOnUiThread(new setUITextView(text));
    }

    private class WaitingClientThread extends Thread
    {
        public void run()
        {
            //SetText("Esperando Usuario...");
            try
            {
                //Abrimos el socket

                try {
                    serverSocket = new ServerSocket(mPuerto);
                } catch (IOException e) {
                    Log.e("1","No puede escuchar en el puerto: " + mPuerto+e.toString());
                    System.exit(-1);
                }

                //Mostramos un mensaje para indicar que estamos esperando en la direccion ip y el puerto...
                AppenText("Dirección: "+getIpAddress()+" Puerto: "+serverSocket.getLocalPort());
                ip = getIpAddress();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        envBtn.setEnabled(false);
                        mensTxt.setEnabled(false);
                    }
                });


                //Creamos un socket que esta a la espera de una conexion de cliente
                socket = serverSocket.accept();

                //Una vez hay conexion con un cliente, creamos los streams de salida/entrada
                try {
                    dataInputStream = new DataInputStream(socket.getInputStream());
                    dataOutputStream = new DataOutputStream(socket.getOutputStream());
                }catch(Exception e){ e.printStackTrace();}

                ConectionEstablished=true;
                runOnUiThread(new Runnable() { // ACTIVAMOS ENVIAR MENSAJE
                    @Override
                    public void run() {
                        envBtn.setEnabled(true);
                        mensTxt.setEnabled(true);
                    }
                });
                //Iniciamos el hilo para la escucha y procesado de mensajes
                (HiloEscucha=new GetMessagesThread()).start();

                //Enviamos mensajes desde el servidor.
                (new EnvioMensajesServidor()).start();
                HiloEspera=null;
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }
    }

    private class ClientConnectToServer extends Thread
    {
        String mIp;
        public ClientConnectToServer(String ip){mIp=ip;}
        public void run()
        {
            //TODO Connect to server
            try {
                SetText("Conectando con el servidor: " + mIp);//Mostramos por la interfaz que nos hemos conectado al servidor


                    socket = new Socket(mIp, mPuerto);//Creamos el socket


                try {
                    dataInputStream = new DataInputStream(socket.getInputStream());
                    dataOutputStream = new DataOutputStream(socket.getOutputStream());
                }catch(Exception e){ e.printStackTrace();}

                ConectionEstablished=true;
                //Iniciamos el hilo para la escucha y procesado de mensajes
                (HiloEscucha=new GetMessagesThread()).start();

                new EnvioMensajesCliente().start();

            } catch (Exception e) {
                e.printStackTrace();
                AppenText("Error: " + e.getMessage());
                finish();
            }
        }

    }

    private class EnvioMensajesServidor extends Thread
    {
        public void run()
        {
            String messages[]={"Bienvenido usuario a mi chat", "¿Estás bien?", "Bueno, pues molt bé, pues adiós"};
            int sleeptime[]={1000, 2000, 2000};
            sendVariousMessages(messages, sleeptime);
            DisconnectSockets();
        }
    }

    private class EnvioMensajesCliente extends Thread
    {
        public void run()
        {
            String messages[]={"Hola servidor", "No mucho, pero no te voy a contar mi vida", "Pues adiós :("};
            int sleeptime[]={1000, 2000, 1000};
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            sendVariousMessages(messages, sleeptime);
            DisconnectSockets();
        }
    }

    private void DisconnectSockets()
    {
        if(ConectionEstablished)
        {
            runOnUiThread(new Runnable() {
                @Override
                public void run()
                {


                }
            });
            ConectionEstablished = false;

            if (HiloEscucha != null)
            {
                HiloEscucha.setExecuting(false);
                HiloEscucha.interrupt();
                HiloEspera = null;
            }

            try {
                if (dataInputStream != null)
                    dataInputStream.close();
            } catch (Exception e) {
            } finally {
                dataInputStream = null;
                try {
                    if (dataOutputStream != null)
                        dataOutputStream.close();
                } catch (Exception e) {
                } finally {
                    dataOutputStream = null;
                    try {
                        if (socket != null)
                            socket.close();
                    } catch (Exception e) {
                    } finally {
                        socket = null;
                    }
                }
            }
        }
    }

    private void sendVariousMessages(String[] msgs, int[] time)
    {
        if(msgs!=null && time!=null && msgs.length==time.length)
            for(int i=0; i<msgs.length; i++)
            {
                sendMessage(msgs[i]);
                try {
                    Thread.sleep(time[i]);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    return;
                }
            }
    }

    private void sendMessage(String txt)
    {
        new SendMessageSocketThread(txt).start();
    }

    private class SendMessageSocketThread extends Thread
    {
        private String msg;

        SendMessageSocketThread(String message)
        {
            msg=message;
        }

        @Override
        public void run()
        {
            try
            {
                dataOutputStream.writeUTF(msg);//Enviamos el mensaje

            }catch (IOException e)
            {
                e.printStackTrace();
            }
        }
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

    private class GetMessagesThread extends Thread
    {
        public boolean executing;
        private String line;


        public void run()
        {
            executing=true;

            while(executing)
            {
                line="";
                line=ObtenerCadena();//Obtenemos la cadena del buffer
                Date f = new Date(); //CREAMOS FORMATO DE FECHA
                DateFormat hourdateFormat = new SimpleDateFormat("HH:mm:ss dd/MM/yyyy");
                if(line!="" && line.length()!=0) {//Comprobamos que esa cadena tenga contenido
                    mensajes.add(new Mensaje(line, user, hourdateFormat.format(f)));//Procesamos la cadena recibida
                    actualizar();
                }
            }
        }

        public void setExecuting(boolean execute){executing=execute;}


        private String ObtenerCadena()
        {
            String cadena="";

            try {
                cadena=dataInputStream.readUTF();//Leemos del datainputStream una cadena UTF
                Log.d("ObtenerCadena", "Cadena reibida: "+cadena);

            }catch(Exception e)
            {
                e.printStackTrace();
                executing=false;
            }
            return cadena;
        }
    }

    protected class setUITextView implements Runnable
    {
        private String text;
        public setUITextView(String text){this.text=text;}
        public void run(){ipTv.setText(text);}
    }

    protected class appendUITextView implements Runnable
    {
        private String text;
        public appendUITextView(String text){this.text=text;}
        public void run(){ipTv.append(text);}
    }

    protected class actuView implements Runnable
    {

        public actuView(){}
        public void run(){actualizar();}
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        DisconnectSockets();
    }
}
