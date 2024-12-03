package com.example.bookshelfapp;

import android.content.Intent;
import android.os.Bundle;

import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import models.Book;
import utils.BookAdapterShelf;
import utils.BookManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class MyBookshelfActivity extends AppCompatActivity {
    private BookAdapterShelf arrayAdapterLibrosLeyendo;
    private BookAdapterShelf arrayAdapterLibrosLeidos;
    private BookAdapterShelf arrayAdapterLibrosParaLeer;
    private BookAdapterShelf arrayAdapterLibrosAbandonados;
    List<Book> libros;
    Map<Integer, List<Book>> librosMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_my_bookshelf);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        ListView lvLibrosLeidos = findViewById(R.id.lvLibrosLeidos);
        ListView lvLibrosLeyendo = findViewById(R.id.lvLibrosLeyendo);
        ListView lvLibrosParaLeer = findViewById(R.id.lvLibrosParaLeer);
        ListView lvLibrosAbandonados = findViewById(R.id.lvLibrosAbandonados);

        libros = BookManager.getInstance().getLibros();
        agruparLibros(libros);

        arrayAdapterLibrosLeyendo = new BookAdapterShelf(this, librosMap.getOrDefault(1, new ArrayList<>()));
        arrayAdapterLibrosLeidos = new BookAdapterShelf(this, librosMap.getOrDefault(2, new ArrayList<>()));
        arrayAdapterLibrosParaLeer = new BookAdapterShelf(this, librosMap.getOrDefault(3, new ArrayList<>()));
        arrayAdapterLibrosAbandonados = new BookAdapterShelf(this, librosMap.getOrDefault(4, new ArrayList<>()));

        lvLibrosLeidos.setAdapter(arrayAdapterLibrosLeidos);
        lvLibrosLeyendo.setAdapter(arrayAdapterLibrosLeyendo);
        lvLibrosParaLeer.setAdapter(arrayAdapterLibrosParaLeer);
        lvLibrosAbandonados.setAdapter(arrayAdapterLibrosAbandonados);

        lvLibrosLeyendo.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                List<Book> librosLeyendo = librosMap.getOrDefault(1, null);
                if (librosLeyendo != null && i >= 0 && i < librosLeyendo.size()) {
                    Intent nextAct = new Intent(MyBookshelfActivity.this, BookDetailActivity.class);
                    nextAct.putExtra("Libro", librosLeyendo.get(i));
                    startActivity(nextAct);
                }
            }
        });

        lvLibrosLeidos.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                List<Book> librosLeidos = librosMap.getOrDefault(2, null);
                if (librosLeidos != null && i >= 0 && i < librosLeidos.size()) {
                    Intent nextAct = new Intent(MyBookshelfActivity.this, BookDetailActivity.class);
                    nextAct.putExtra("Libro", librosLeidos.get(i));
                    startActivity(nextAct);
                }
            }
        });

        lvLibrosParaLeer.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                List<Book> librosParaLeer = librosMap.getOrDefault(3, null);
                if (librosParaLeer != null && i >= 0 && i < librosParaLeer.size()) {
                    Intent nextAct = new Intent(MyBookshelfActivity.this, BookDetailActivity.class);
                    nextAct.putExtra("Libro", librosParaLeer.get(i));
                    startActivity(nextAct);
                }
            }
        });

        lvLibrosAbandonados.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                List<Book> librosAbandonados = librosMap.getOrDefault(4, null);
                if (librosAbandonados != null && i >= 0 && i < librosAbandonados.size()) {
                    Intent nextAct = new Intent(MyBookshelfActivity.this, BookDetailActivity.class);
                    nextAct.putExtra("Libro", librosAbandonados.get(i));
                    startActivity(nextAct);
                }
            }
        });

    }

    private void agruparLibros(List<Book> libros) {
        librosMap = new TreeMap<>();
        for (Book libro : libros) {
            int estado = libro.getEstado();
            if (!librosMap.containsKey(estado)) {
                librosMap.put(estado, new ArrayList<>());
            }
            librosMap.get(estado).add(libro);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        agruparLibros(libros);

        arrayAdapterLibrosLeyendo.clear();
        arrayAdapterLibrosLeyendo.addAll(librosMap.getOrDefault(1, new ArrayList<>()));
        arrayAdapterLibrosLeidos.clear();
        arrayAdapterLibrosLeidos.addAll(librosMap.getOrDefault(2, new ArrayList<>()));
        arrayAdapterLibrosParaLeer.clear();
        arrayAdapterLibrosParaLeer.addAll(librosMap.getOrDefault(3, new ArrayList<>()));
        arrayAdapterLibrosAbandonados.clear();
        arrayAdapterLibrosAbandonados.addAll(librosMap.getOrDefault(4, new ArrayList<>()));

        arrayAdapterLibrosLeidos.notifyDataSetChanged();
        arrayAdapterLibrosLeyendo.notifyDataSetChanged();
        arrayAdapterLibrosParaLeer.notifyDataSetChanged();
        arrayAdapterLibrosAbandonados.notifyDataSetChanged();
    }
}