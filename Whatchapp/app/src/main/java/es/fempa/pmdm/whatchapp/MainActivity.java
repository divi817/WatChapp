package es.fempa.pmdm.whatchapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }
 // EVENTS CLICK DE LOS DIFERENTES BOTONES DEL MAIN
    public void serverb(View v){
        Intent i = new Intent(this, ConfActivity.class);
        i.putExtra("tipo", "server");
        startActivity(i);
    }

    public void clientb(View v){
        Intent i = new Intent(this, ConfActivity.class);
        i.putExtra("tipo", "cliente");
        startActivity(i);
    }

    public void aboutb(View v){
        Intent i = new Intent(this, AboutActivity.class);
        startActivity(i);
    }
}
