package com.example.bookshelfapp;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;

import android.util.Log;
import android.view.View;
import android.widget.*;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import models.Book;
import models.Review;
import utils.BookManager;
import utils.ReviewAdapter;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class BookDetailActivity extends AppCompatActivity {

    private String[] opcionesDeEstado = new String[]{"Añadir","Leyendo","Leído","Leer más tarde","Abandonado"};
    private Date fechaLeido; String strFecha; TextView tvFecha;
    List<Review> reviews;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_book_detail);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        ImageView ivPortada  = findViewById(R.id.ivPortada2);

        TextView tvSinopsis = findViewById(R.id.tvSinopsis);
        TextView tvTitulo = findViewById(R.id.tvNombre2);
        TextView tvAutor = findViewById(R.id.tvAutor2);
        TextView tvAnio = findViewById(R.id.tvAnio2);
        TextView tvGeneros = findViewById(R.id.tvGeneros);
        TextView tvNumPag=findViewById(R.id.tvNumPag);
        NumberPicker npNumPagLeidas=findViewById(R.id.npNumPagLeidas);
        TextView tvOtros = findViewById(R.id.tvOtro);
        TextView tvOtros2 = findViewById(R.id.tvOtro2);
        TextView tvRating = findViewById(R.id.tvRating2);
        tvFecha = findViewById(R.id.tvFecha);
        Intent intent = getIntent();
        Book libro = (Book) intent.getParcelableExtra("Libro");

        reviews = libro.getReviews();
        Log.d("Reviews", "Reviews size: " + reviews.size());
        ListView lvReviews = findViewById(R.id.lvReviews);
        ReviewAdapter reviewAdapter = new ReviewAdapter(this, reviews);
        lvReviews.setAdapter(reviewAdapter);

        Spinner spEstado = findViewById(R.id.spEstado);

        tvTitulo.setText(libro.getTitulo());
        tvAutor.setText(libro.getAutor());
        tvAnio.setText(String.valueOf(libro.getAnio()));
        tvRating.setText(String.valueOf(libro.getRating()));
        ivPortada.setImageBitmap(libro.getRecursoImagenMini());
        tvSinopsis.setText(libro.getSinopsis());
        tvGeneros.setText(libro.getGeneros());
        tvNumPag.setText(String.valueOf(libro.getNumPag()));
        tvFecha.setText(String.valueOf(libro.getFechaLeido()));

        if(libro.getEstado()!=2){
            tvFecha.setVisibility(View.INVISIBLE);
        }
        tvOtros.setVisibility(View.INVISIBLE);
        tvOtros2.setVisibility(View.INVISIBLE);
        npNumPagLeidas.setVisibility(View.INVISIBLE);
        tvNumPag.setVisibility(View.INVISIBLE);

        ArrayAdapter<String> ArrayAdapterOpcionesDeEstado = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, opcionesDeEstado);
        spEstado.setAdapter(ArrayAdapterOpcionesDeEstado);

        npNumPagLeidas.setMinValue(0);
        npNumPagLeidas.setMaxValue(libro.getNumPag());
        npNumPagLeidas.setValue(libro.getNumPagLeidas());
        npNumPagLeidas.setWrapSelectorWheel(true);

        spEstado.setSelection(libro.getEstado());
        int estadoInicial=libro.getEstado();

        spEstado.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                switch (i){
                    case 0:
                        return;
                    case 1:
                        libro.setEstado(1);
                        tvOtros.setVisibility(View.VISIBLE);
                        tvOtros2.setVisibility(View.VISIBLE);
                        npNumPagLeidas.setVisibility(View.VISIBLE);
                        tvNumPag.setVisibility(View.VISIBLE);
                        tvFecha.setVisibility(View.INVISIBLE);
                        npNumPagLeidas.setEnabled(true);
                        break;
                    case 2:
                        libro.setEstado(2);
                        tvOtros.setVisibility(View.INVISIBLE);
                        tvOtros2.setVisibility(View.INVISIBLE);
                        npNumPagLeidas.setVisibility(View.INVISIBLE);
                        tvNumPag.setVisibility(View.INVISIBLE);
                        npNumPagLeidas.setValue(libro.getNumPag());
                        if(estadoInicial!=2){
                            mostrarCalendario();
                        }
                        libro.setFechaLeido(fechaLeido);
                        tvFecha.setText(String.valueOf(libro.getFechaLeido()));
                        tvFecha.setVisibility(View.VISIBLE);

                        break;
                    case 3:
                        libro.setEstado(3);
                        tvOtros.setVisibility(View.INVISIBLE);
                        tvOtros2.setVisibility(View.INVISIBLE);
                        npNumPagLeidas.setVisibility(View.INVISIBLE);
                        tvNumPag.setVisibility(View.INVISIBLE);
                        tvFecha.setVisibility(View.INVISIBLE);
                        break;
                    case 4:
                        libro.setEstado(4);
                        tvOtros.setVisibility(View.INVISIBLE);
                        tvOtros2.setVisibility(View.INVISIBLE);
                        npNumPagLeidas.setVisibility(View.INVISIBLE);
                        tvNumPag.setVisibility(View.INVISIBLE);
                        tvFecha.setVisibility(View.INVISIBLE);
                        npNumPagLeidas.setEnabled(false);
                        break;
                }
                BookManager.getInstance().addLibro(libro);
                Toast.makeText(getApplicationContext(), "Se ha actualizado el libro en la estantería", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                Log.d("estado","no selecionado");
            }
        });

        npNumPagLeidas.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker numberPicker, int i, int i1) {
                libro.setNumPagLeidas(npNumPagLeidas.getValue());
            }
        });
        tvFecha.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mostrarCalendario();
                libro.setFechaLeido(fechaLeido);
            }
        });
    }

    private void mostrarCalendario(){
        Calendar calendar = Calendar.getInstance();
        int anio = calendar.get(Calendar.YEAR);
        int mes = calendar.get(Calendar.MONTH);
        int dia = calendar.get(Calendar.DAY_OF_MONTH);
        DatePickerDialog datePickerDialog = new DatePickerDialog(BookDetailActivity.this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int anio, int mes, int dia) {
                Calendar fechaSelect = Calendar.getInstance();
                fechaSelect.set(anio, mes, dia);
                fechaLeido = fechaSelect.getTime();
                strFecha = String.format("%2d/%2d/%4d",dia,mes,anio);
                if (fechaLeido != null) {
                    tvFecha.setText("Leído el "+strFecha);
                } else {
                    tvFecha.setText("Clic para añadir fecha de leído");
                }
            }
        }, anio, mes, dia);
        datePickerDialog.show();
    }
}