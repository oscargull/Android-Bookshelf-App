package com.example.bookshelfapp.models;

import android.graphics.Bitmap;
import android.os.Parcel;
import android.os.Parcelable;
import androidx.annotation.NonNull;

import java.util.Date;
import java.util.List;


public class Book implements Parcelable {
    private String titulo;
    private String autor;
    private Bitmap recursoImagenFull;
    private Bitmap recursoImagenMini;
    private double rating;
    private int anio;
    private String sinopsis;
    private String generos;
    private int numPag;
    private int numPagLeidas;
    private int estado;
    private Date fechaLeido;
    private List<Review> reviews;


    //CONSTRUCTORES
    public Book(String titulo, String autor, Bitmap recursoImagenFull, Bitmap recursoImagenMini, int anio, double rating, String sinopsis, String generos, int numPag, List<Review> reviews) {
        this.titulo = titulo;
        this.autor = autor;
        this.recursoImagenFull = recursoImagenFull;
        this.recursoImagenMini=recursoImagenMini;
        this.anio = anio;
        this.rating = rating;
        this.sinopsis=sinopsis;
        this.generos=generos;
        this.numPag=numPag;

        this.reviews = reviews;
    }

    public Book(){
    }

    protected Book(Parcel in) {
        titulo = in.readString();
        autor = in.readString();
        //recursoImagenFull = in.readParcelable(Bitmap.class.getClassLoader());
        recursoImagenMini = in.readParcelable(Bitmap.class.getClassLoader());
        rating = in.readDouble();
        anio = in.readInt();
        sinopsis = in.readString();
        generos = in.readString();
        numPag = in.readInt();
        numPagLeidas = in.readInt();
        estado = in.readInt();
        long timestamp = in.readLong();
        if (timestamp != 0) {
            fechaLeido = new Date(timestamp);
        } else {
            fechaLeido = null;
        }
        reviews = in.createTypedArrayList(Review.CREATOR);
    }

    public static final Creator<Book> CREATOR = new Creator<Book>() {
        @Override
        public Book createFromParcel(Parcel in) {
            return new Book(in);
        }

        @Override
        public Book[] newArray(int size) {
            return new Book[size];
        }
    };

    public void addReview(int rating, String text) {
        this.reviews.add(new Review(rating, text));
    }
    public List<Review> getReviews() {
        return reviews;
    }
    //GETTERs Y SETTERs
    public Bitmap getRecursoImagenFull() {return recursoImagenFull;}
    public void setRecursoImagenFull(Bitmap recursoImagenFull) {this.recursoImagenFull = recursoImagenFull;}
    public Bitmap getRecursoImagenMini() {
        return recursoImagenMini;
    }
    public void setRecursoImagenMini(Bitmap recursoImagenMini) {
        this.recursoImagenMini = recursoImagenMini;
    }
    public String getTitulo() {return titulo;}
    public void setTitulo(String titulo) {this.titulo = titulo;}
    public String getAutor() {return autor;}
    public void setAutor(String autor) {this.autor = autor;}
    public int getAnio() {return anio;}
    public void setAnio(int anio) {this.anio = anio;}
    public double getRating() {
        return rating;
    }
    public void setRating(double rating) {
        this.rating = rating;
    }
    public String getSinopsis() {
        return sinopsis;
    }
    public void setSinopsis(String sinopsis) {
        this.sinopsis = sinopsis;
    }
    public String getGeneros() {
        return generos;
    }
    public void setGeneros(String generos) {
        this.generos = generos;
    }
    public int getNumPag() {
        return numPag;
    }
    public void setNumPag(int numPag) {
        this.numPag = numPag;
    }
    public int getNumPagLeidas() {
        return numPagLeidas;
    }
    public void setNumPagLeidas(int numPagLeidas) {
        this.numPagLeidas = numPagLeidas;
    }
    public int getEstado() {
        return estado;
    }
    public void setEstado(int estado) {
        this.estado = estado;
    }
    public Date getFechaLeido() {
        return fechaLeido;
    }
    public void setFechaLeido(Date fechaLeido) {
        this.fechaLeido = fechaLeido;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel parcel, int i) {
        parcel.writeString(titulo);
        parcel.writeString(autor);
        //parcel.writeParcelable(recursoImagenFull, i);
        parcel.writeParcelable(recursoImagenMini, i);
        parcel.writeDouble(rating);
        parcel.writeInt(anio);
        parcel.writeString(sinopsis);
        parcel.writeString(generos);
        parcel.writeInt(numPag);
        parcel.writeInt(numPagLeidas);
        parcel.writeInt(estado);
        if (fechaLeido != null) {
            parcel.writeLong(fechaLeido.getTime());
        } else {
            parcel.writeLong(0);
        }
        parcel.writeTypedList(reviews);
    }
}
