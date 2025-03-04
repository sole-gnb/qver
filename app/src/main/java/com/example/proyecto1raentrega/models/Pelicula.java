package com.example.proyecto1raentrega.models;
public class Pelicula {
    private String titulo;
    private int estreno;

    public Pelicula(){};
    public Pelicula(String titulo, int estreno) {
        this.titulo = titulo;
        this.estreno = estreno;
    }

    public String getTitulo() {
        return titulo;
    }
    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public int getEstreno() {
        return estreno;
    }
    public void setEstreno(int estreno) {
        this.estreno = estreno;
    }
}