package com.example.bookshelfapp;

import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;

import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.datastore.preferences.core.PreferencesKeys;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.PagerSnapHelper;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SnapHelper;
import db.SQLiteBooksHelper;
import models.Book;
import models.Review;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import pl.droidsonroids.gif.GifImageView;
import utils.BookAdapterMain;
import utils.BookManager;
import utils.DataStoreManager;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity {

    SQLiteDatabase db;
    SQLiteBooksHelper DBHelper;

    private RecyclerView rvBooks;
    private List<Book> bookData = new ArrayList<>();
    private Book book = new Book();

    private String username;


    BookAdapterMain bookAdapterMain = new BookAdapterMain(bookData,this);
    String search;
    Button btnSearch;
    GifImageView gifLoading;

    List<Bitmap> bookImages = new ArrayList<>();

    private DataStoreManager dataStoreManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        //setTheme(R.style.Base_Theme_BookshelfApp_ActionBar);

        db = openOrCreateDatabase("Bookshelf", Context.MODE_PRIVATE, null);
        DBHelper = new SQLiteBooksHelper(this);


        dataStoreManager = DataStoreManager.getInstance(this);

        if(getIntent()!=null) {
            username = getIntent().getStringExtra("user");
            dataStoreManager.putString("logged_user", getIntent().getStringExtra("user"));
        }


        //deleteDatabase("Bookshelf");

        rvBooks = findViewById(R.id.rvBooks);
        EditText etSearch = findViewById(R.id.etSearch);

        gifLoading =  findViewById(R.id.gifLoading);
        gifLoading.setVisibility(View.INVISIBLE);


        btnSearch = findViewById(R.id.btnSearch);
        Button btnBookshelf = findViewById(R.id.btnBookshelf);

        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                rvBooks.setVisibility(View.INVISIBLE);
                search = etSearch.getText().toString();
                new WebScrapingTask().execute();
                if(!bookImages.isEmpty()){
                    bookImages.clear();
                }
                gifLoading.setVisibility(View.VISIBLE);

                btnSearch.setEnabled(false);
            }
        });
        btnBookshelf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent nextAct = new Intent(MainActivity.this, MyBookshelfActivity.class);
                startActivity(nextAct);
            }
        });


        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL,false);
        rvBooks.setLayoutManager(linearLayoutManager);
        rvBooks.setAdapter(bookAdapterMain);
        SnapHelper snapHelper = new PagerSnapHelper();
        snapHelper.attachToRecyclerView(rvBooks);



    }


    private class WebScrapingTask extends AsyncTask<Void, Void, List<Book>> {

        @Override
        protected List<Book> doInBackground(Void... voids) {
            List<Book> bookDataSearch = new ArrayList<>();

            try {
                Document document = Jsoup.connect("https://www.goodreads.com/search?utf8=%E2%9C%93&query=" + search).get();
                Elements bookElements = document.select("table tbody tr");

                for (Element libroElement : bookElements) {
                    String bookTitle = libroElement.select("a.bookTitle span[itemprop=\"name\"]").text();

                    String bookAuthor = libroElement.select("a.authorName span[itemprop=\"name\"]").text();

                    String bookYearSelect = libroElement.select("span.greyText.smallText.uitext").text();
                    Pattern patterYear = Pattern.compile("\\d{4}");
                    Matcher matcherYear = patterYear.matcher(bookYearSelect);
                    int bookYear =0;
                    if (matcherYear.find()) {
                        bookYear = Integer.valueOf(matcherYear.group());
                    }

                    String bookRatingSelect = libroElement.select("span.minirating").text();
                    Double bookRating =0.0;
                    Pattern patternRating = Pattern.compile("(\\d+.\\d+) avg rating");
                    Matcher matcherRating = patternRating.matcher(bookRatingSelect);
                    if(matcherRating.find()){
                        bookRating = Double.valueOf(matcherRating.group(1));
                    }

                    Bitmap bookCoverFull =null;
                    Bitmap bookCoverMini=null;

                    String bookCoverMiniSelect = libroElement.select("img").attr("src");
                    String strReplace = "https://images-na.ssl-images-amazon.com/images/S/compressed.photo.goodreads.com/books/$1i/$2.jpg";
                    Pattern patternImageUrl = Pattern.compile("https://i.gr-assets.com/images/S/compressed.photo.goodreads.com/books/(\\d+)i/(\\d+)._(.*)_.jpg");
                    Matcher matcherImageUrl = patternImageUrl.matcher(bookCoverMiniSelect);
                    if (matcherImageUrl.matches()) {
                        String imagenUrlFull = matcherImageUrl.replaceAll(strReplace);
                        bookCoverFull = downloadImage(imagenUrlFull);
                        bookImages.add(bookCoverFull);
                    }
                    bookCoverMini= downloadImage(bookCoverMiniSelect);


                    String bookUrlHref = libroElement.select("a.bookTitle").attr("href");
                    Document document2 = Jsoup.connect("https://www.goodreads.com"+bookUrlHref).get();
                    Log.d("librow", String.valueOf(document2));

                    String bookNumPagSelect = document2.select("p[data-testid=\"pagesFormat\"]").text();
                    String regex = "^\\d+";
                    Pattern patternNumPag = Pattern.compile(regex);
                    Matcher matcherNumPag = patternNumPag.matcher(bookNumPagSelect);
                    String strBookNumPag = "";
                    int book =0;
                    if (matcherNumPag.find()) {
                        strBookNumPag = matcherNumPag.group();
                        book =Integer.valueOf(strBookNumPag);
                    }


                    String bookDescription ="";
                    Element descriptionElement = document2.select("meta[name=description]").first();
                    String bookDescriptionHtml = descriptionElement.attr("content");
                    regex = "(?<=readers\\.|here\\.|ISBN [\\d]\\.)\\s*(.*)";
                    Pattern patternDescription = Pattern.compile(regex);
                    Matcher matcherDescription = patternDescription.matcher(bookDescriptionHtml);
                    if (matcherDescription.find()) {
                        bookDescription = matcherDescription.group(1).trim();
                    }

                    Elements genreElements = document2.select(".BookPageMetadataSection__genreButton a.Button--tag");
                    String bookGenres ="";
                    int genreCount =0;
                    if (!genreElements.isEmpty()) {
                        for (Element genre : genreElements) {
                            if (genreCount < 3) {
                                bookGenres = bookGenres.concat(genre.text());
                                if (genreCount < 2) {
                                    bookGenres = bookGenres.concat(", ");
                                }
                                genreCount++;
                            } else {
                                break;
                            }
                        }
                    }

                    int reviewCount = 0;

                    List<Review> reviews = new ArrayList<>();
                    Elements reviewElements = document2.select(".ReviewCard");
                    String reviewRatingSelect="";
                    String reviewText="";
                    for (Element reviewElement : reviewElements) {
                        if (reviewCount < 3) {
                            reviewText = reviewElement.select(".ReviewText__content span.Formatted").text();

                            reviewRatingSelect = reviewElement.select(".ShelfStatus span").attr("aria-label");
                            int reviewRating = 0;
                            regex = "(?<=Rating )\\d+";
                            Pattern patternReviewRating = Pattern.compile(regex);
                            Matcher matcherReviewRating = patternReviewRating.matcher(reviewRatingSelect);
                            if (matcherReviewRating.find()) {
                                String strReviewRating = matcherReviewRating.group();
                                reviewRating = Integer.parseInt(strReviewRating);
                            }
                            reviewCount++;

                            Review review = new Review(reviewRating, reviewText);
                            reviews.add(review);
                        }
                    }

                    bookDataSearch.add(new Book(bookTitle, bookAuthor, bookCoverFull,bookCoverMini, bookYear, bookRating, bookDescription,bookGenres, book,reviews));

                }

            } catch(IOException e){
                e.printStackTrace();
            }
            return bookDataSearch;
        }

        @Override
        protected void onPostExecute(List<Book> datosLibroBusqueda) {
            super.onPostExecute(datosLibroBusqueda);
            btnSearch.setEnabled(true);
            gifLoading.setVisibility(View.INVISIBLE);
            rvBooks.setVisibility(View.VISIBLE);

            bookData.clear();

            bookData.addAll(datosLibroBusqueda);
            bookAdapterMain.notifyDataSetChanged();
        }
    }



    private Bitmap downloadImage(String imageUrl) {
        try {
            InputStream inputStream = new URL(imageUrl).openStream();
            return BitmapFactory.decodeStream(inputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_superior_main, menu);
        return true;
    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.opcPreferencias) {
            Intent nextActiv = new Intent(MainActivity.this, SettingsActivity.class);
            startActivity(nextActiv);
            return true;
        } else if (item.getItemId() == R.id.opcCerrarSesion) {
            finish();
            Intent nextActiv = new Intent(MainActivity.this, LoginRegisterActivity.class);
            dataStoreManager.removeKey(PreferencesKeys.stringKey("logged_user"));
            dataStoreManager.removeKey(PreferencesKeys.booleanKey("keep_logged_in"));
            startActivity(nextActiv);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        List<Book> myBooks = BookManager.getInstance().getLibros();
        for (int i = 0; i < bookData.size(); i++) {
            Book libroBusqueda = bookData.get(i);
            for (Book book : myBooks) {
                if (libroBusqueda.getTitulo().equals(book.getTitulo())) {
                    book.setRecursoImagenFull(bookImages.get(i));
                    bookData.set(i, book);
                    break;
                }
            }
        }
        bookAdapterMain.notifyDataSetChanged();
    }


















}