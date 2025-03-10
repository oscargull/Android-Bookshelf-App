package com.example.bookshelfapp.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.AdapterView;
import android.widget.ListView;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.example.bookshelfapp.BookDetailActivity;
import com.example.bookshelfapp.R;
import com.example.bookshelfapp.models.Book;
import com.example.bookshelfapp.utils.BookAdapterShelf;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class ShelfFragment extends Fragment {
    private BookAdapterShelf arrayAdapterLibrosLeyendo;
    private BookAdapterShelf arrayAdapterLibrosLeidos;
    private BookAdapterShelf arrayAdapterLibrosParaLeer;
    private BookAdapterShelf arrayAdapterLibrosAbandonados;
    private ListView lvLibrosLeidos ;
    private ListView lvLibrosLeyendo ;
    private ListView lvLibrosParaLeer ;
    private ListView lvLibrosAbandonados;

    private Map<Integer, List<Book>> booksMap;
    private ListView[] lists;

    private OnFragmentEventListener listener;

    public ShelfFragment(Map<Integer, List<Book>> booksMap) {
        this.booksMap=booksMap;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.fragment_main_shelf, container, false);
        lvLibrosLeidos = view.findViewById(R.id.lvLibrosLeidos);
        lvLibrosLeyendo = view.findViewById(R.id.lvLibrosLeyendo);
        lvLibrosParaLeer = view.findViewById(R.id.lvLibrosParaLeer);
        lvLibrosAbandonados = view.findViewById(R.id.lvLibrosAbandonados);

        loadLists();

        lists= new ListView[]{lvLibrosLeyendo, lvLibrosLeidos,lvLibrosParaLeer,lvLibrosAbandonados};

        for (int i = 0; i < lists.length; i++){
            final int pos = i+1;
            lists[i].setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    List<Book> bookList = booksMap.getOrDefault(pos, null);
                    if (bookList != null && position >= 0 && position < bookList.size()) {
                        Intent nextAct = new Intent(requireActivity(), BookDetailActivity.class);
                        nextAct.putExtra("Libro", bookList.get(position));
                        startActivity(nextAct);
                    }
                }
            });
        }

        return view;
    }

    public void loadLists(){
        arrayAdapterLibrosLeyendo = new BookAdapterShelf(requireActivity(), booksMap.getOrDefault(1, new ArrayList<>()));
        arrayAdapterLibrosLeidos = new BookAdapterShelf(requireActivity(), booksMap.getOrDefault(2, new ArrayList<>()));
        arrayAdapterLibrosParaLeer = new BookAdapterShelf(requireActivity(), booksMap.getOrDefault(3, new ArrayList<>()));
        arrayAdapterLibrosAbandonados = new BookAdapterShelf(requireActivity(), booksMap.getOrDefault(4, new ArrayList<>()));

        lvLibrosLeidos.setAdapter(arrayAdapterLibrosLeidos);
        lvLibrosLeyendo.setAdapter(arrayAdapterLibrosLeyendo);
        lvLibrosParaLeer.setAdapter(arrayAdapterLibrosParaLeer);
        lvLibrosAbandonados.setAdapter(arrayAdapterLibrosAbandonados);
    }

    public interface OnFragmentEventListener{
        void onFragmentEvent(Bundle args) throws Exception;
    }

    @Override
    public void onAttach(Context context){
        super.onAttach(context);
        if (context instanceof OnFragmentEventListener){
            listener = (OnFragmentEventListener) context;
        }
    }
    @Override
    public void onResume() {
        super.onResume();
        arrayAdapterLibrosLeidos.notifyDataSetChanged();
        arrayAdapterLibrosLeyendo.notifyDataSetChanged();
        arrayAdapterLibrosParaLeer.notifyDataSetChanged();
        arrayAdapterLibrosAbandonados.notifyDataSetChanged();
    }
    @Override
    public void onDetach(){
        super.onDetach();
        listener = null;
    }
}