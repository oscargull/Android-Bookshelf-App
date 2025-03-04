package com.example.bookshelfapp;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;

import android.util.Log;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import com.example.bookshelfapp.db.SQLiteBooksHelper;
import com.example.bookshelfapp.models.Book;
import com.example.bookshelfapp.utils.BookAdapterShelf;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class MyBookshelfActivity extends AppCompatActivity {
    private BookAdapterShelf arrayAdapterLibrosLeyendo;
    private BookAdapterShelf arrayAdapterLibrosLeidos;
    private BookAdapterShelf arrayAdapterLibrosParaLeer;
    private BookAdapterShelf arrayAdapterLibrosAbandonados;
    ListView lvLibrosLeidos ;
    ListView lvLibrosLeyendo ;
    ListView lvLibrosParaLeer ;
    ListView lvLibrosAbandonados;

    boolean eliminar;

    List<Book> libros;
    Map<Integer, List<Book>> librosMap;

    SQLiteDatabase db;
    SQLiteBooksHelper DBHelper;

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
        lvLibrosLeidos = findViewById(R.id.lvLibrosLeidos);
        lvLibrosLeyendo = findViewById(R.id.lvLibrosLeyendo);
        lvLibrosParaLeer = findViewById(R.id.lvLibrosParaLeer);
        lvLibrosAbandonados = findViewById(R.id.lvLibrosAbandonados);

        db = openOrCreateDatabase("Bookshelf", Context.MODE_PRIVATE, null);
        DBHelper = new SQLiteBooksHelper(this);
        //libros = BookManager.getInstance().getLibros();
        libros = DBHelper.getLibros();
        agruparLibros(libros);

        arrayAdapterLibrosLeyendo = new BookAdapterShelf(this, librosMap.getOrDefault(1, new ArrayList<>()));
        arrayAdapterLibrosLeidos = new BookAdapterShelf(this, librosMap.getOrDefault(2, new ArrayList<>()));
        arrayAdapterLibrosParaLeer = new BookAdapterShelf(this, librosMap.getOrDefault(3, new ArrayList<>()));
        arrayAdapterLibrosAbandonados = new BookAdapterShelf(this, librosMap.getOrDefault(4, new ArrayList<>()));

        lvLibrosLeidos.setAdapter(arrayAdapterLibrosLeidos);
        lvLibrosLeyendo.setAdapter(arrayAdapterLibrosLeyendo);
        lvLibrosParaLeer.setAdapter(arrayAdapterLibrosParaLeer);
        lvLibrosAbandonados.setAdapter(arrayAdapterLibrosAbandonados);

        registerForContextMenu(lvLibrosLeidos);
        registerForContextMenu(lvLibrosLeyendo);
        registerForContextMenu(lvLibrosParaLeer);
        registerForContextMenu(lvLibrosAbandonados);


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
        refrescar();
    }

    public void refrescar(){
        libros = DBHelper.getLibros();

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

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        getMenuInflater().inflate(R.menu.menu_contextual_libro, menu);
    }

    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        int pos = info.position;
        Log.d("item", String.valueOf(pos));
        int opcId =item.getItemId();
        AdapterView<?> parentView = (AdapterView<?>) info.targetView.getParent();

        if (parentView == lvLibrosLeyendo) {
            Log.d("item", "Leyendo selected");
            List<Book> librosLeyendo = librosMap.getOrDefault(1, new ArrayList<>());
            Book libro = librosLeyendo.get(pos);
            if (opcId == R.id.eliminarLibro) {
               showAlerta();
                DBHelper.eliminarLibro(libro.getTitulo());
                librosLeyendo.remove(pos);
            }
        } else if (parentView == lvLibrosLeidos) {
            Log.d("item", "Leídos selected");
            List<Book> librosLeidos = librosMap.getOrDefault(2, new ArrayList<>());
            Book libro = librosLeidos.get(pos);
            if (opcId == R.id.eliminarLibro) {
                showAlerta();

                DBHelper.eliminarLibro(libro.getTitulo());
                librosLeidos.remove(pos);
            }
        } else if (parentView == lvLibrosParaLeer) {
            Log.d("item", "Para Leer selected");
            List<Book> librosParaLeer = librosMap.getOrDefault(3, new ArrayList<>());
            Book libro = librosParaLeer.get(pos);
            if (opcId == R.id.eliminarLibro) {
                showAlerta();

                DBHelper.eliminarLibro(libro.getTitulo());
                librosParaLeer.remove(pos);
            }
        } else if (parentView == lvLibrosAbandonados) {
            Log.d("item", "Abandonados selected");
            List<Book> librosAbandonados = librosMap.getOrDefault(4, new ArrayList<>());
            Book libro = librosAbandonados.get(pos);
            if (opcId == R.id.eliminarLibro) {
                showAlerta();

                DBHelper.eliminarLibro(libro.getTitulo());
                librosAbandonados.remove(pos);


            }
        }
        refrescar();


        return super.onContextItemSelected(item);
    }
    private boolean showAlerta() {
        eliminar = true;
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Conirmación")
                .setMessage("¿Está seguro de eliminar el libro de la estantería?")

                .setPositiveButton("Sí", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        eliminar=true;
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        eliminar=true;
                        dialog.dismiss();
                    }
                })
                .setCancelable(false);

        AlertDialog dialog = builder.create();
        dialog.show();
        return eliminar;
    }


}