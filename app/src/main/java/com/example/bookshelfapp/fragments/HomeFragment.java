package com.example.bookshelfapp.fragments;

import android.content.Context;
import android.os.Bundle;
import android.widget.ProgressBar;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.PagerSnapHelper;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SnapHelper;
import com.example.bookshelfapp.R;
import com.example.bookshelfapp.models.Book;
import com.example.bookshelfapp.utils.BookAdapterMain;

import java.util.ArrayList;
import java.util.List;


public class HomeFragment extends Fragment {

    private OnFragmentEventListener listener;

    private RecyclerView rvBooks;
    private ProgressBar pbLoading;
    private BookAdapterMain bookAdapterMain;
    private List<Book> books;

    public HomeFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main_home, container, false);

        books=new ArrayList<>();
        pbLoading = view.findViewById(R.id.pbLoading);
        setLoading(false);

        rvBooks = view.findViewById(R.id.rvBooks);
        bookAdapterMain = new BookAdapterMain(books,requireActivity());

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(requireActivity(), LinearLayoutManager.HORIZONTAL,false);
        rvBooks.setLayoutManager(linearLayoutManager);
        rvBooks.setAdapter(bookAdapterMain);
        SnapHelper snapHelper = new PagerSnapHelper();
        snapHelper.attachToRecyclerView(rvBooks);

        return view;
    }

    public interface OnFragmentEventListener{
        void onFragmentEvent(Bundle args) throws Exception;
    }

    public void loadResultsSearch(List<Book> searchedBooks){
        books.clear();
        books.addAll(searchedBooks);
        bookAdapterMain.notifyDataSetChanged();
        setLoading(false);
    }

    public void setLoading(boolean isLoading){
        if (isLoading){
            pbLoading.setVisibility(View.VISIBLE);
        }else if(!isLoading){
            pbLoading.setVisibility(View.GONE);
        }
    }

    @Override
    public void onAttach(Context context){
        super.onAttach(context);
        if (context instanceof OnFragmentEventListener){
            listener = (OnFragmentEventListener) context;
        }
    }

    @Override
    public void onDetach(){
        super.onDetach();
        listener = null;
    }
}