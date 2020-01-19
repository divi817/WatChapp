package es.fempa.pmdm.whatchapp;

public class Mensaje {

     //CLASE MENSAJE ATRIBUTS Y CONECTORES
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
