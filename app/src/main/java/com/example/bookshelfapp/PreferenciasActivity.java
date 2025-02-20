package com.example.bookshelfapp;

import android.content.SharedPreferences;
import android.os.Bundle;

import android.preference.PreferenceManager;
import android.util.Log;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class PreferenciasActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_preferencias);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        SharedPreferences misPreferencias = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
       /* misPreferencias.registerOnSharedPreferenceChangeListener((sharedPreferences, key) -> {
            if ("list_preference_idioma".equals(key)) {
                String selectedIdioma = sharedPreferences.getString(key, "es"); // "es" is the default value
                if ("es".equals(selectedIdioma)) {
                    Log.d("IdiomaSelected", "Idioma seleccionado: Español");
                } else if ("en".equals(selectedIdioma)) {
                    Log.d("IdiomaSelected", "Idioma seleccionado: Inglés");
                }
            }
            if (key == "switch_preference_modo_oscuro") {

            }

        });*/
    }
}