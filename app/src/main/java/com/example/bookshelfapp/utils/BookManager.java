package com.example.bookshelfapp.utils;

import com.example.bookshelfapp.models.Book;

import java.util.ArrayList;
import java.util.List;

public class BookManager {
    private static BookManager instance;
    private List<Book> libros;
    private BookManager() {
        libros = new ArrayList<>();
    }

    public static synchronized BookManager getInstance() {
        if (instance == null) {
            instance = new BookManager();
        }
        return instance;
    }

    public void addLibro(Book libro) {
        for (int i = 0; i < libros.size(); i++) {
            Book existingLibro = libros.get(i);
            if (existingLibro.getTitulo().equals(libro.getTitulo())) {
                libros.set(i, libro);
                return;
            }
        }
        libros.add(libro);
    }

    public List<Book> getLibros() {
        return libros;
    }

    public void eliminarLibro(Book libro) {
        libros.remove(libro);
    }
}
