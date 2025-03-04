package com.example.bookshelfapp.fragments;

import android.content.Context;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.example.bookshelfapp.R;


public class LoginRegisterFragment extends Fragment implements View.OnClickListener {

    private OnFragmentEventListener listener;
    Button btn;
    Boolean isSignup;

    public LoginRegisterFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Bundle args = getArguments();
        String layout = args.getString("layout");

        View view;
        if (layout=="signup") {
            view = inflater.inflate(R.layout.fragment_signup, container, false);
            btn = view.findViewById(R.id.btnRegistrar);
            isSignup =true;
        } else{
            view = inflater.inflate(R.layout.fragment_login, container, false);
            btn = view.findViewById(R.id.btnEntrar);
            isSignup =false;
        }
        btn.setOnClickListener(this);
        btn.setEnabled(true);

        return view;
    }

    @Override
    public void onClick(View v) {
        EditText etUsuario = getView().findViewById(R.id.etUsuario);
        EditText etContrasenia = getView().findViewById(R.id.etContrasenia);

        String username = etUsuario.getText().toString();
        String password = etContrasenia.getText().toString();

        Bundle args = new Bundle();
        args.putString("username", username);
        args.putString("password", password);

        if(isSignup) {
            EditText etEmail = getView().findViewById(R.id.etEmail);
            String email = etEmail.getText().toString();
            args.putString("email", email);
            btn.setEnabled(false);
        }

        args.putBoolean("signup_mode", isSignup);

        if (listener != null) {
            try {
                listener.onFragmentEvent(args);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }


    public interface OnFragmentEventListener{
        void onFragmentEvent(Bundle args) throws Exception;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentEventListener) {
            listener = (OnFragmentEventListener) context;
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        listener = null;
    }
}