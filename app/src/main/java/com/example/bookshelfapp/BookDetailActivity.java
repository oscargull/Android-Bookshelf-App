package com.example.bookshelfapp;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;

import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.FragmentManager;
import com.example.bookshelfapp.db.SQLiteBooksHelper;
import com.example.bookshelfapp.fragments.ReviewDialogFragment;
import com.example.bookshelfapp.models.Book;
import com.example.bookshelfapp.models.Review;
import com.example.bookshelfapp.utils.BookManager;
import com.example.bookshelfapp.utils.ReviewAdapter;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class BookDetailActivity extends AppCompatActivity  implements ReviewDialogFragment.MiListener {

    private String[] opcionesDeEstado = new String[]{"Añadir","Leyendo","Leído","Leer más tarde","Abandonado"};
    private Date fechaLeido; String strFecha; TextView tvFecha;
    List<Review> reviews;
    Button btnReview;

    Book libro;
    SQLiteDatabase db;
    SQLiteBooksHelper DBHelper;

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
        db = openOrCreateDatabase("Bookshelf", Context.MODE_PRIVATE, null);
        DBHelper = new SQLiteBooksHelper(this);

        ImageView ivPortada  = findViewById(R.id.ivPortada2);
        btnReview = findViewById(R.id.btnReview);

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
        libro = (Book) intent.getParcelableExtra("Libro");

       /* reviews = libro.getReviews();
        Log.d("Reviews", "Reviews size: " + reviews.size());
        ListView lvReviews = findViewById(R.id.lvReviews);
        ReviewAdapter reviewAdapter = new ReviewAdapter(this, reviews);
        lvReviews.setAdapter(reviewAdapter);
        setListViewHeightBasedOnChildren(lvReviews);*/
        poblarListaReviews();


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
                Log.d("lib titulo",libro.getTitulo());
                Log.d("libro",String.valueOf(DBHelper.isLibroNuevo(libro.getTitulo())));
                if(DBHelper.isLibroNuevo(libro.getTitulo())){
                    Log.d("libro","libro gto save");
                    DBHelper.saveLibro(libro, libro.getEstado());
                    Log.d("libro","libro guardado,");
                }else{
                    DBHelper.updateLibro(libro, libro.getEstado());
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
                DBHelper.setProgresoLibro(libro.getTitulo(),npNumPagLeidas.getValue());
            }
        });
        tvFecha.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mostrarCalendario();
                libro.setFechaLeido(fechaLeido);
                DBHelper.setFechaLeidoLibro(libro.getTitulo(), fechaLeido);
            }
        });
        btnReview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fm = getSupportFragmentManager(); // Use this for AndroidX
                ReviewDialogFragment dialogFragment = new ReviewDialogFragment();
                dialogFragment.show(fm, "review");
            }
        });
    }

    @Override
    public void onDialogFragmentEvent(String reviewText, int rating) {
        Log.d("BookDetailActivity", "Review: " + reviewText + ", Rating: " + rating);
        DBHelper.addReview(reviewText, libro.getTitulo(), rating);
        btnReview.setVisibility(View.INVISIBLE);
        libro.addReview(rating, reviewText);

        poblarListaReviews();

    }

    public void poblarListaReviews(){
        reviews = libro.getReviews();
        Log.d("Reviews", "Reviews size: " + reviews.size());
        ListView lvReviews = findViewById(R.id.lvReviews);
        ReviewAdapter reviewAdapter = new ReviewAdapter(this, reviews);
        lvReviews.setAdapter(reviewAdapter);
        setListViewHeightBasedOnChildren(lvReviews);
    }

        public void setListViewHeightBasedOnChildren (ListView listView){
            ListAdapter listAdapter = listView.getAdapter();
            if (listAdapter == null) return;

            int totalHeight = 0;
            for (int i = 0; i < listAdapter.getCount(); i++) {
                View listItem = listAdapter.getView(i, null, listView);
                listItem.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
                totalHeight += listItem.getMeasuredHeight();
            }

            ViewGroup.LayoutParams params = listView.getLayoutParams();
            params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
            listView.setLayoutParams(params);
            listView.requestLayout();
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