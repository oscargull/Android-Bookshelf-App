package utils;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import com.example.bookshelfapp.R;
import models.Book;

import java.util.List;

public class BookAdapterShelf extends ArrayAdapter<Book> {
    private Context context;
    private List<Book> libros;

    public BookAdapterShelf(@NonNull Context context, List<Book> libros) {
        super(context, 0, libros);
        this.context = context;
        this.libros = libros;
    }



    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Book libro = getItem(position);
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_book_list, parent, false);
        }
        TextView tvTitulo = convertView.findViewById(R.id.tvNombre3);
        TextView tvAutor = convertView.findViewById(R.id.tvAutor3);
        ImageView ivPortada = convertView.findViewById(R.id.ivPortada3);
        ProgressBar pbProgreso = convertView.findViewById(R.id.pbProgreso);

        if(libro.getEstado()==1){
            convertView.setBackgroundColor(ContextCompat.getColor(context, android.R.color.holo_green_light));
        }else if (libro.getEstado()==2){
            convertView.setBackgroundColor(ContextCompat.getColor(context, android.R.color.holo_blue_light));
        }else if(libro.getEstado()==3){
            convertView.setBackgroundColor(ContextCompat.getColor(context, android.R.color.darker_gray));
        }else if(libro.getEstado()==4){
            convertView.setBackgroundColor(ContextCompat.getColor(context, android.R.color.holo_red_light));
        }
        tvTitulo.setText(libro.getTitulo());
        tvAutor.setText(libro.getAutor());
        ivPortada.setImageBitmap(libro.getRecursoImagenMini());
        pbProgreso.setMax(100);
        pbProgreso.setProgress((libro.getNumPagLeidas()*100)/libro.getNumPag());


        return convertView;
    }

}
