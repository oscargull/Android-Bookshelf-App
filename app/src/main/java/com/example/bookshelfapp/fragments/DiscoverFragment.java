package com.example.bookshelfapp.fragments;

import android.content.Context;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.example.bookshelfapp.R;

public class DiscoverFragment extends Fragment {

    private ShelfFragment.OnFragmentEventListener listener;

    public DiscoverFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_main_discover, container, false);
    }

    public interface OnFragmentEventListener{
        void onFragmentEvent(Bundle args) throws Exception;
    }

    @Override
    public void onAttach(Context context){
        super.onAttach(context);
        if (context instanceof ShelfFragment.OnFragmentEventListener){
            listener = (ShelfFragment.OnFragmentEventListener) context;
        }
    }

    @Override
    public void onDetach(){
        super.onDetach();
        listener = null;
    }
}