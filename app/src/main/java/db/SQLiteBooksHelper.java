package db;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.widget.Toast;
import com.example.bookshelfapp.LoginRegisterActivity;
import models.Book;
import models.Review;
import utils.UserManager;

import javax.crypto.SecretKey;
import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Date;
import java.util.List;

import static android.content.Context.MODE_PRIVATE;

public class SQLiteBooksHelper extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "Bookshelf";

    private String loggedUsername;
    private int user_id;
    private Context con;

    //UserManager um;

    public SQLiteBooksHelper(Context context){
        super(context, DATABASE_NAME,null,DATABASE_VERSION);
        this.con=context.getApplicationContext();

        SharedPreferences prefs = con.getSharedPreferences("user_prefs", MODE_PRIVATE);
      loggedUsername = prefs.getString("logged_user", null);
        //um = new UserManager(context);
    }



    public void saveLibro(Book libro, int estado){
        ContentValues valuesLibro = new ContentValues();
        valuesLibro.put(BookshelfContract.Books.TITLE, libro.getTitulo());
        valuesLibro.put(BookshelfContract.Books.AUTHOR, libro.getAutor());
        valuesLibro.put(BookshelfContract.Books.GENRES, libro.getGeneros());
        valuesLibro.put(BookshelfContract.Books.SINOPSIS, libro.getSinopsis());
        valuesLibro.put(BookshelfContract.Books.ANIO, libro.getAnio());
        valuesLibro.put(BookshelfContract.Books.RATING, libro.getRating());
        valuesLibro.put(BookshelfContract.Books.NUM_PAG, libro.getNumPag());

       ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        libro.getRecursoImagenMini().compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
        byte[] bytesImagen = byteArrayOutputStream.toByteArray();

        valuesLibro.put(BookshelfContract.Books.PORTADA, bytesImagen);



        SQLiteDatabase db = getWritableDatabase();
        db.insert(BookshelfContract.Books.TABLE_NAME,null, valuesLibro);


        user_id = getLoggedUserId();
        Log.d("user_id upd", String.valueOf(user_id));

        ContentValues valuesRelacion = new ContentValues();
        valuesRelacion.put(BookshelfContract.Users_Books.USER_ID,user_id);
        valuesRelacion.put(BookshelfContract.Users_Books.BOOK_ID,getLibroId(libro.getTitulo()));
        switch (estado) {
            case 1:
                Log.d("est",String.valueOf(estado));
                valuesRelacion.put(BookshelfContract.Users_Books.STATUS,"Leyendo");
                //valuesRelacion.put(BookshelfContract.Users_Books.PROGRESO,libro.getNumPag());
                break;
            case 2:
                valuesRelacion.put(BookshelfContract.Users_Books.STATUS,"Leido");
                break;
            case 3:
                valuesRelacion.put(BookshelfContract.Users_Books.STATUS,"Planeado");
                break;
            case 4:
                valuesRelacion.put(BookshelfContract.Users_Books.STATUS,"Abandonado");
                break;
        }
        db.insert(BookshelfContract.Users_Books.TABLE_NAME,null, valuesRelacion);

    }

    public void updateLibro(Book libro, int estado){
        SQLiteDatabase db = getWritableDatabase();

        String strEstado="";
        String strFechaLeido="";
        int progresoPag=0;
        switch (estado) {
            case 1:
                strEstado = "Leyendo";
                setProgresoLibro(libro.getTitulo(),libro.getNumPagLeidas());
                break;
            case 2:
                strEstado = "Leido";
               setFechaLeidoLibro(libro.getTitulo(),libro.getFechaLeido());
                break;
            case 3:
                strEstado = "Planeado";
                break;
            case 4:
                strEstado = "Abandonado";
                break;
        }
        String query = "UPDATE "+BookshelfContract.Users_Books.TABLE_NAME+" SET "
                + BookshelfContract.Users_Books.STATUS + " = ?, "
                + BookshelfContract.Users_Books.PROGRESO + " = ?, "
                + BookshelfContract.Users_Books.FECHA_LEIDO + " = ? "
                    + " WHERE " + BookshelfContract.Users_Books.USER_ID + " = ? "
                    + " AND " + BookshelfContract.Users_Books.BOOK_ID + " = ?";

        db.execSQL(query,new String[]{strEstado,String.valueOf(progresoPag),strFechaLeido,String.valueOf(user_id),String.valueOf(getLibroId(libro.getTitulo()))});
    }

    public List<Book> getLibros(){
        List<Book> libros = new ArrayList<>();
        user_id = getLoggedUserId();
        SQLiteDatabase db = getReadableDatabase();

        Cursor c = db.rawQuery("SELECT * FROM "+BookshelfContract.Books.TABLE_NAME
                +" WHERE "+BookshelfContract.Books.BOOK_ID
                +" IN (SELECT " +BookshelfContract.Users_Books.BOOK_ID
                        +" FROM "+BookshelfContract.Users_Books.TABLE_NAME
                        +" WHERE "+BookshelfContract.Users_Books.USER_ID+" = ?)",new String[]{String.valueOf(user_id)});



        if (c!= null && c.moveToFirst()){
            while(c.moveToNext()){
                String titulo = c.getString(1);
                String autor = c.getString(2);
                String generos = c.getString(3);
                String sinopsis = c.getString(4);
                int anio = c.getInt(5);
                double rating = c.getDouble(6);
                byte[] portadaBytes = c.getBlob(7);
                int numPag = c.getInt(8);

                Bitmap recursoImagenMini = BitmapFactory.decodeByteArray(portadaBytes, 0, portadaBytes.length);
                //Bitmap recursoImagenMini = getMiniatureImage(recursoImagenFull); // Assuming you want a smaller version of the image



                int book_id = c.getInt(0);
                List<Review> reviews = new ArrayList<>();
                reviews = getReviews(book_id);


                //estado y progreso
                int estado=getEetadoLibro(book_id);
                int progresoPags=getProgresoLibro(book_id);



                Book libro = new Book(titulo, autor, null, recursoImagenMini, anio, rating, sinopsis, generos, numPag, reviews);
                libro.setEstado(estado);
                libro.setNumPagLeidas(progresoPags);

                libros.add(libro);



            }
        }
        return libros;
    }

    public void eliminarLibro(String titulo){
        int book_id = getLibroId(titulo);
        SQLiteDatabase db = getWritableDatabase();

        String query = "DELETE FROM "+BookshelfContract.Users_Books.TABLE_NAME
                +" WHERE "+BookshelfContract.Users_Books.BOOK_ID+" = ? "
                    +"AND "+ BookshelfContract.Users_Books.USER_ID +" = ? ";

        db.execSQL(query,new String[]{String.valueOf(book_id),String.valueOf(getLoggedUserId())});
    }


    public int getLibroId(String titulo){
        SQLiteDatabase db = getReadableDatabase();

        int book_id=0;
        String query = "SELECT "+BookshelfContract.Books.BOOK_ID
                +" FROM "+BookshelfContract.Books.TABLE_NAME
                + " WHERE " + BookshelfContract.Books.TITLE
                +" = ?";

        Cursor c = db.rawQuery(query,new String[]{titulo});
        if (c!=null && c.moveToFirst()){
            book_id = c.getInt(0);
        }
        return book_id;


    }

    public boolean isLibroNuevo(String titulo){
        SQLiteDatabase db = getReadableDatabase();

        int book_id = getLibroId(titulo);
        user_id = getLoggedUserId();
        Log.d("bookid",String.valueOf(book_id));
        Log.d("userid",String.valueOf(user_id));
        Cursor c = db.rawQuery("SELECT 1 FROM " + BookshelfContract.Users_Books.TABLE_NAME
                    + " WHERE " + BookshelfContract.Users_Books.BOOK_ID + " = ?"
                    + " AND " + BookshelfContract.Users_Books.USER_ID + " = ?"
                    + " LIMIT 1", new String[]{String.valueOf(book_id), String.valueOf(user_id)});
        if (c.moveToFirst()){
            return false;
        }else{
            return true;
        }

        }




    public void setProgresoLibro(String titulo, int progresoPag){
        SQLiteDatabase db = getWritableDatabase();

        String query = "UPDATE "+BookshelfContract.Users_Books.TABLE_NAME+" SET "
                + BookshelfContract.Users_Books.PROGRESO + " = ? "
                + " WHERE " + BookshelfContract.Users_Books.USER_ID + " = ? "
                + " AND " + BookshelfContract.Users_Books.BOOK_ID + " = ?";

        db.execSQL(query,new String[]{String.valueOf(progresoPag),String.valueOf(user_id),String.valueOf(getLibroId(titulo))});
    }

    public int getProgresoLibro(int book_id){
        user_id = getLoggedUserId();
        SQLiteDatabase db = getReadableDatabase();
        int progresoPag=0;


        Cursor c = db.rawQuery("SELECT "+BookshelfContract.Users_Books.PROGRESO
                    +" FROM "+BookshelfContract.Users_Books.TABLE_NAME
                    +" WHERE "+BookshelfContract.Users_Books.BOOK_ID+" = ?",new String[]{String.valueOf(book_id)});
        if(c!=null && c.moveToFirst()){
            progresoPag = c.getInt(0);
        }
        c.close();

        return progresoPag;
    }

    public int getEetadoLibro(int book_id){
        user_id = getLoggedUserId();
        SQLiteDatabase db = getReadableDatabase();
        int estado = 0;


        Cursor c = db.rawQuery("SELECT "+BookshelfContract.Users_Books.STATUS
                +" FROM "+BookshelfContract.Users_Books.TABLE_NAME
                +" WHERE "+BookshelfContract.Users_Books.BOOK_ID+" = ?",new String[]{String.valueOf(book_id)});
        if(c!=null && c.moveToFirst()){
            String strEstado = c.getString(0);
            switch (strEstado){
                case "Leyendo":
                    estado =1;
                    break;
                case "Leido":
                    estado =2;
                    break;
                case "Planeado":
                    estado =3;
                    break;
                case "Abandonado":
                    estado =4;
                    break;
            }
        }
        c.close();
        return estado;
    }



    public void setFechaLeidoLibro(String titulo, Date fecha){
        if (fecha != null){


        SQLiteDatabase db = getWritableDatabase();
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
        String strFechaLeido = sdf.format(fecha);

        String query = "UPDATE "+BookshelfContract.Users_Books.TABLE_NAME+" SET "
                + BookshelfContract.Users_Books.FECHA_LEIDO + " = ? "
                + " WHERE " + BookshelfContract.Users_Books.USER_ID + " = ? "
                + " AND " + BookshelfContract.Users_Books.BOOK_ID + " = ?";

        Log.d("fecha", strFechaLeido);

        db.execSQL(query,new String[]{strFechaLeido,String.valueOf(user_id),String.valueOf(getLibroId(titulo))});
    }
    }

    public void addReview(String contenido, String titulo, int rating){
        ContentValues values = new ContentValues();
        user_id=getLoggedUserId();
        values.put(BookshelfContract.Reviews.BOOK_ID,getLibroId(titulo));
        values.put(BookshelfContract.Reviews.USER_ID,user_id);
        values.put(BookshelfContract.Reviews.CONTENT,contenido);
        values.put(BookshelfContract.Reviews.RATING,rating);

        SQLiteDatabase db = getWritableDatabase();
        db.insert(BookshelfContract.Reviews.TABLE_NAME,null,values);
    }

    public List<Review> getReviews (int book_id){
        List<Review> reviews = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        user_id = getLoggedUserId();

        Cursor c = db.rawQuery("SELECT * FROM "+BookshelfContract.Reviews.TABLE_NAME
                +" WHERE "+BookshelfContract.Books.BOOK_ID+" = ?",new String[]{String.valueOf(book_id)});
        if(c!=null && c.moveToFirst()){
            while(c.moveToNext()){
                String texto = c.getString(3);
                int rating = c.getInt(4);
                Review rev = new Review(rating,texto);
                reviews.add(rev);

            }
        }
        return reviews;
    }



    public void registerUser(String username, String email, String[] hash){
        ContentValues values = new ContentValues();
        values.put(BookshelfContract.Users.USERNAME, username);
        values.put(BookshelfContract.Users.EMAIL,email);
        values.put(BookshelfContract.Users.SALT,hash[0]);
        values.put(BookshelfContract.Users.HASHED_PASSWORD,hash[1]);
        SQLiteDatabase db = getWritableDatabase();
        db.insert(BookshelfContract.Users.TABLE_NAME,null,values);
    }

    public boolean logIn(Context con, String username, String password) throws Exception {
        SQLiteDatabase db = getReadableDatabase();
        Cursor c = db.rawQuery("SELECT *"
                +" FROM "+BookshelfContract.Users.TABLE_NAME
                +" WHERE "+BookshelfContract.Users.USERNAME+" = ?",new String[]{username});
        if (c!=null && c.moveToFirst()){
            String strSalt = c.getString(3);
            String strHash = c.getString(4);
            char[] pass = password.toCharArray();
            byte[] salt = Base64.getDecoder().decode(strSalt);

           LoginRegisterActivity temp =new LoginRegisterActivity();
            SecretKey key = temp.pbkdf2(pass,salt);
            String resultHash = Base64.getEncoder().encodeToString(key.getEncoded());
            if(strHash.equals(resultHash)){
                return true;
            }else{
                Toast.makeText(con, "Contraseña incorrecta",Toast.LENGTH_SHORT).show();
                return false;
            }
        }else{
            Toast.makeText(con, "Usuario no encontrado",Toast.LENGTH_SHORT).show();
            return false;
        }
    }

    public int getLoggedUserId(){
        Log.d("logged user",loggedUsername);
        SQLiteDatabase db= getReadableDatabase();
        Cursor c = db.rawQuery("SELECT "+BookshelfContract.Users.USER_ID
                +" FROM "+BookshelfContract.Users.TABLE_NAME
                +" WHERE "+ BookshelfContract.Users.USERNAME +" = ?",new String[]{loggedUsername});
        int user_id=0;
        if(c!=null && c.moveToFirst()) {
            user_id = c.getInt(0);
        }
        return user_id;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE "+BookshelfContract.Users.TABLE_NAME+" ("
                +BookshelfContract.Users.USER_ID+" INTEGER PRIMARY KEY AUTOINCREMENT,"
                +BookshelfContract.Users.USERNAME+" TEXT NOT NULL,"
                +BookshelfContract.Users.EMAIL+" TEXT NOT NULL,"
                +BookshelfContract.Users.SALT+" TEXT NOT NULL,"
                +BookshelfContract.Users.HASHED_PASSWORD+" TEXT NOT NULL)");

        db.execSQL("CREATE TABLE "+BookshelfContract.Books.TABLE_NAME+" ("
                +BookshelfContract.Books.BOOK_ID+" INTEGER PRIMARY KEY AUTOINCREMENT,"
                +BookshelfContract.Books.TITLE+" TEXT NOT NULL,"
                +BookshelfContract.Books.AUTHOR+" TEXT NOT NULL,"
                +BookshelfContract.Books.GENRES+" TEXT,"
                +BookshelfContract.Books.SINOPSIS+" TEXT,"
                +BookshelfContract.Books.ANIO+" INTEGER,"
                +BookshelfContract.Books.RATING+" NUMERIC,"
                +BookshelfContract.Books.PORTADA+" BLOB,"
                +BookshelfContract.Books.NUM_PAG+" NUMERIC)");

        db.execSQL("CREATE TABLE "+BookshelfContract.Reviews.TABLE_NAME+" ("
                +BookshelfContract.Reviews.REVIEW_ID+" INTEGER PRIMARY KEY AUTOINCREMENT,"
                +BookshelfContract.Reviews.BOOK_ID+" INTEGER NOT NULL, "
                +BookshelfContract.Reviews.USER_ID+ " INTEGER NOT NULL,"
                +BookshelfContract.Reviews.CONTENT + " TEXT,"
                +BookshelfContract.Reviews.RATING + " NUMERIC,"
                + "FOREIGN KEY(" + BookshelfContract.Reviews.BOOK_ID + ") "
                + "REFERENCES " +BookshelfContract.Books.TABLE_NAME + "(" + BookshelfContract.Books.BOOK_ID + "), "
                + "FOREIGN KEY(" + BookshelfContract.Reviews.USER_ID + ") "
                + "REFERENCES " +BookshelfContract.Users.TABLE_NAME + "(" + BookshelfContract.Users.USER_ID + ")) ");


        db.execSQL("CREATE TABLE "+BookshelfContract.Users_Books.TABLE_NAME+" ("
                +BookshelfContract.Users_Books.USER_ID+" INTEGER NOT NULL,"
                +BookshelfContract.Users_Books.BOOK_ID+" INTEGER NOT NULL,"
                +BookshelfContract.Users_Books.STATUS+" TEXT "
                    +" CHECK ("+BookshelfContract.Users_Books.STATUS+" IN ('Leyendo','Leido','Planeado','Abandonado')),"
                +BookshelfContract.Users_Books.PROGRESO+" INTEGER,"
                +BookshelfContract.Users_Books.FECHA_LEIDO+" TEXT, "
                + "FOREIGN KEY(" + BookshelfContract.Users_Books.USER_ID + ") "
                + "REFERENCES " +BookshelfContract.Users.TABLE_NAME + "(" + BookshelfContract.Users.USER_ID + "), "
                + "FOREIGN KEY(" + BookshelfContract.Users_Books.BOOK_ID + ") "
                + "REFERENCES " +BookshelfContract.Books.TABLE_NAME + "(" + BookshelfContract.Books.BOOK_ID + ")) ");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("CREATE TABLE "+BookshelfContract.Users.TABLE_NAME+" ("
                +BookshelfContract.Users.USER_ID+" INTEGER PRIMARY KEY AUTOINCREMENT,"
                +BookshelfContract.Users.USERNAME+" TEXT NOT NULL,"
                +BookshelfContract.Users.EMAIL+" TEXT NOT NULL,"
                +BookshelfContract.Users.SALT+" TEXT NOT NULL,"
                +BookshelfContract.Users.HASHED_PASSWORD+" TEXT NOT NULL)");

        db.execSQL("CREATE TABLE "+BookshelfContract.Books.TABLE_NAME+" ("
                +BookshelfContract.Books.BOOK_ID+" INTEGER PRIMARY KEY AUTOINCREMENT,"
                +BookshelfContract.Books.TITLE+" TEXT NOT NULL,"
                +BookshelfContract.Books.AUTHOR+" TEXT NOT NULL,"
                +BookshelfContract.Books.GENRES+" TEXT,"
                +BookshelfContract.Books.SINOPSIS+" TEXT,"
                +BookshelfContract.Books.ANIO+" INTEGER,"
                +BookshelfContract.Books.RATING+" NUMERIC,"
                +BookshelfContract.Books.PORTADA+" BLOB,"
                +BookshelfContract.Books.NUM_PAG+" NUMERIC)");

        db.execSQL("CREATE TABLE "+BookshelfContract.Reviews.TABLE_NAME+" ("
                +BookshelfContract.Reviews.REVIEW_ID+" INTEGER PRIMARY KEY AUTOINCREMENT,"
                +BookshelfContract.Reviews.BOOK_ID+" INTEGER NOT NULL, "
                +BookshelfContract.Reviews.USER_ID+ " INTEGER NOT NULL,"
                +BookshelfContract.Reviews.CONTENT + " TEXT,"
                +BookshelfContract.Reviews.RATING + " NUMERIC,"
                + "FOREIGN KEY(" + BookshelfContract.Reviews.BOOK_ID + ") "
                + "REFERENCES " +BookshelfContract.Books.TABLE_NAME + "(" + BookshelfContract.Books.BOOK_ID + "), "
                + "FOREIGN KEY(" + BookshelfContract.Reviews.USER_ID + ") "
                + "REFERENCES " +BookshelfContract.Users.TABLE_NAME + "(" + BookshelfContract.Users.USER_ID + ")) ");


        db.execSQL("CREATE TABLE "+BookshelfContract.Users_Books.TABLE_NAME+" ("
                +BookshelfContract.Users_Books.USER_ID+" INTEGER NOT NULL,"
                +BookshelfContract.Users_Books.BOOK_ID+" INTEGER NOT NULL,"
                +BookshelfContract.Users_Books.STATUS+" TEXT "
                    +" CHECK ("+BookshelfContract.Users_Books.STATUS+" IN ('Leyendo','Leido','Planeado','Abandonado')),"
                +BookshelfContract.Users_Books.PROGRESO+" INTEGER,"
                +BookshelfContract.Users_Books.FECHA_LEIDO+" TEXT, "
                + "FOREIGN KEY(" + BookshelfContract.Users_Books.USER_ID + ") "
                + "REFERENCES " +BookshelfContract.Users.TABLE_NAME + "(" + BookshelfContract.Users.USER_ID + "), "
                + "FOREIGN KEY(" + BookshelfContract.Users_Books.BOOK_ID + ") "
                + "REFERENCES " +BookshelfContract.Books.TABLE_NAME + "(" + BookshelfContract.Books.BOOK_ID + ")) ");
    }
}
