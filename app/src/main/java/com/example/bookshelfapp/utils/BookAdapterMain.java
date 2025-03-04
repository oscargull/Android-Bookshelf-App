package com.example.bookshelfapp.utils;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.bookshelfapp.BookDetailActivity;
import com.example.bookshelfapp.R;
import com.example.bookshelfapp.models.Book;

import java.util.ArrayList;
import java.util.List;

public class BookAdapterMain extends RecyclerView.Adapter<BookAdapterMain.miViewHolder> {
    private List<Book> datosLibro;
    private Context context;
    private int posicion;

    public class miViewHolder extends RecyclerView.ViewHolder{
        public ImageView imagen;
        public TextView titulo;
        public TextView autor;
        public TextView anio;
        public TextView rating;

        public miViewHolder(@NonNull View itemView) {
            super(itemView);
            titulo = itemView.findViewById(R.id.tvNombre);
            autor = itemView.findViewById(R.id.tvAutor);
            anio = itemView.findViewById(R.id.tvAnio);
            imagen = itemView.findViewById(R.id.ivPortada);
            rating = itemView.findViewById(R.id.tvRating);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    posicion= getAdapterPosition();
                    Log.d("pos",String.valueOf(posicion));
                    Intent nextAct = new Intent(view.getContext(), BookDetailActivity.class);
                    nextAct.putExtra("Libro", datosLibro.get(posicion));
                    view.getContext().startActivity(nextAct);
                }
            });
        }
    }

    public BookAdapterMain(List<Book> libro, Context cont){
        this.datosLibro= new ArrayList<>();
        this.datosLibro=libro;
        this.context=cont;
    }


    @NonNull
    @Override
    public miViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View inflate = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_book_recycler, parent, false);
        return new miViewHolder(inflate);
    }
    @Override
    public void onBindViewHolder(@NonNull miViewHolder holder, int position) {
        Book libro = this.datosLibro.get(position);
        holder.titulo.setText(libro.getTitulo());
        holder.autor.setText(libro.getAutor());
        holder.anio.setText(String.valueOf(libro.getAnio()));
        holder.rating.setText(String.valueOf(libro.getRating()));
        holder.imagen.setImageBitmap(libro.getRecursoImagenFull());
    }
    @Override
    public int getItemCount() {
        return this.datosLibro.size();
    }



}