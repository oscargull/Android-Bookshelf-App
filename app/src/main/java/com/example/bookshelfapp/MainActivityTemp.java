package com.example.bookshelfapp;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;

import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.SearchView;
import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.MenuItemCompat;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.example.bookshelfapp.db.SQLiteBooksHelper;
import com.example.bookshelfapp.fragments.DiscoverFragment;
import com.example.bookshelfapp.fragments.HomeFragment;
import com.example.bookshelfapp.fragments.ShelfFragment;
import com.example.bookshelfapp.models.Book;
import org.jetbrains.annotations.NotNull;
import com.example.bookshelfapp.utils.WebScrapper;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class MainActivityTemp extends AppCompatActivity{

    private WebScrapper webScrapper;
    private FragmentManager fragmentManager;

    private HomeFragment homeFragment;
    private ShelfFragment shelfFragment;
    private DiscoverFragment discoverFragment;

    private SQLiteDatabase db;
    private SQLiteBooksHelper DBHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main_temp);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        webScrapper = new WebScrapper();
        fragmentManager = getSupportFragmentManager();

        db = openOrCreateDatabase("Bookshelf", Context.MODE_PRIVATE, null);
        DBHelper = new SQLiteBooksHelper(this);

        homeFragment = new HomeFragment();
        shelfFragment = new ShelfFragment(getUserBooks());
        discoverFragment= new DiscoverFragment();

        loadFragment(homeFragment);

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavView);
        bottomNavigationView.setSelectedItemId(R.id.tabHome);
        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull @NotNull MenuItem menuItem) {
                if(menuItem.getItemId() == R.id.tabHome){
                    loadFragment(homeFragment);
                }else if(menuItem.getItemId() == R.id.tabShelf){
                    loadFragment(shelfFragment);
                }else if(menuItem.getItemId() == R.id.tabDiscover) {
                    loadFragment(discoverFragment);
                }
                return true;
            }
        });
    }

    public void loadFragment(Fragment fragment){
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragmentLayoutMain, fragment);
        fragmentTransaction.commit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_superior_main, menu);


        MenuItem searchViewItem = menu.findItem(R.id.app_bar_search);
        final SearchView sv = (SearchView) MenuItemCompat.getActionView(searchViewItem);
        sv.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                sv.clearFocus();
                Fragment currentFragment = fragmentManager.findFragmentById(R.id.fragmentLayoutMain);

                if (currentFragment instanceof HomeFragment) {
                    ((HomeFragment) currentFragment).setLoading(true);
                    webScrapper.searchLibros(query, new WebScrapper.AsyncResponse() {
                        @Override
                        public void processFinish(Object output) {
                            List<Book> books = (List<Book>) output;
                            ((HomeFragment) currentFragment).loadResultsSearch(books);
                        }
                    });
                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

        return super.onCreateOptionsMenu(menu);
    }

    private Map<Integer, List<Book>> getUserBooks(){
        Map<Integer, List<Book>> booksMap = new TreeMap<>();
        List<Book> userBooksList = DBHelper.getLibros();

        for (Book book : userBooksList){
            int state = book.getEstado();
            if(!booksMap.containsKey(state)){
                booksMap.put(state,  new ArrayList<>());
            }
            booksMap.get(state).add(book);
        }

        return booksMap;
    }

}