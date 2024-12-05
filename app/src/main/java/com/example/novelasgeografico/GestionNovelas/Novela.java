package com.example.novelasgeografico.GestionNovelas;

import android.os.Parcel;
import android.os.Parcelable;

//Clase Novela que implementa Parcelable para pasar objetos entre actividades
public class Novela implements Parcelable {

    //Atributos
    private String id;
    private String titulo;
    private String autor;
    private int añoPublicacion;
    private String sinopsis;
    private boolean favorito;
    private double latitude;
    private double longitude;

    //Constructor vacío
    public Novela() {}

    //Constructor
    public Novela(String titulo, String autor, int añoPublicacion, String sinopsis) {
        this.titulo = titulo;
        this.autor = autor;
        this.añoPublicacion = añoPublicacion;
        this.sinopsis = sinopsis;
    }

    //Getters y Setters
    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }
    public String getTitulo() {
        return titulo;
    }
    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }
    public String getAutor() {
        return autor;
    }
    public void setAutor(String autor) {
        this.autor = autor;
    }
    public int getAñoPublicacion() {
        return añoPublicacion;
    }
    public void setAñoPublicacion(int añoPublicacion) {
        this.añoPublicacion = añoPublicacion;
    }
    public String getSinopsis() {
        return sinopsis;
    }
    public void setSinopsis(String sinopsis) {
        this.sinopsis = sinopsis;
    }
    public boolean getFavorito() {
        return favorito;
    }
    public void setFavorito(boolean favorito) {
        this.favorito = favorito;
    }
    public double getLatitude() {
        return latitude;
    }
    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }
    public double getLongitude() {
        return longitude;
    }
    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    //Metodo equals
    @Override
    public boolean equals(Object obj) {
        //Verificar si los objetos son iguales
        if (this == obj) {
            return true;
        }
        //Verificar si el objeto es nulo
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        //Convertir el objeto a Novela
        Novela novela = (Novela) obj;
        return titulo != null && titulo.equalsIgnoreCase(novela.titulo);
    }

    //Metodo hashCode
    @Override
    public int hashCode() {
        //Generar un hash basado en el título
        return titulo != null ? titulo.toLowerCase().hashCode() : 0;
    }

    //Constructor con parcel que sirve para pasar objetos entre actividades
    protected Novela(Parcel in) {
        titulo = in.readString();
        autor = in.readString();
        añoPublicacion = in.readInt();
        sinopsis = in.readString();
        favorito = in.readByte() != 0;
    }

    //Crear un objeto Parcelable de Novela a partir para pasar objetos entre actividades
    public static final Creator<Novela> CREATOR = new Creator<Novela>() {
        @Override
        public Novela createFromParcel(Parcel in) {
            return new Novela(in);
        }

        @Override
        public Novela[] newArray(int size) {
            return new Novela[size];
        }
    };

    //Metodo describeContents implementado de Parcelable
    @Override
    public int describeContents() {
        return 0;
    }

    //Metodo writeToParcel implementado de Parcelable
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(titulo);
        dest.writeString(autor);
        dest.writeInt(añoPublicacion);
        dest.writeString(sinopsis);
        dest.writeByte((byte) (favorito ? 1 : 0));
    }
}