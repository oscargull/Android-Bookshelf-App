package com.example.bookshelfapp.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import com.example.bookshelfapp.models.Book;
import com.example.bookshelfapp.models.Review;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class WebScrapper  {

    private String query;
    private List<Book> bookList;

    public void searchLibros(String query, AsyncResponse callback){
        this.query=query;
        this.bookList=new ArrayList<>();

        new WebScrapingTask(callback).execute();
    }

    public interface AsyncResponse{
        void processFinish(Object output);
    }

    private class WebScrapingTask extends AsyncTask<Void, Void, List<Book>> {
        private AsyncResponse listener;

        public WebScrapingTask(AsyncResponse listener){
            this.listener=listener;
        }

        @Override
        protected List<Book> doInBackground(Void... voids) {
            List<Book> bookDataSearch = new ArrayList<>();
            List<Bitmap> bookImages = new ArrayList<>();

            try {
                Document document = Jsoup.connect("https://www.goodreads.com/search?utf8=%E2%9C%93&query=" + query).get();
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
            bookList.clear();
            bookList.addAll(datosLibroBusqueda);
            listener.processFinish(bookList);
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
}
