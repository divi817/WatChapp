package es.fempa.pmdm.whatchapp;

public class Mensaje {

     String contenido;
     String autor;
     String fecha;


    public Mensaje(String contenido, String autor, String fecha) {
        this.contenido = contenido;
        this.autor = autor;
        this.fecha = fecha;
    }

    public Mensaje(){
        this.contenido = "";
        this.autor = "";
        this.fecha = "";
    }
}
