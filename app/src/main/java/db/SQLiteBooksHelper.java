package db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;
import com.example.bookshelfapp.LoginRegisterActivity;

import javax.crypto.SecretKey;
import java.util.Base64;

public class SQLiteBooksHelper extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "Bookshelf";

    private String loggedUsername;
    private int user_id;

    public SQLiteBooksHelper(Context context){
        super(context, DATABASE_NAME,null,DATABASE_VERSION);
    }

    public void setLoggedUser(String loggedUser) {
        this.loggedUsername = loggedUser;
        this.user_id=getLoggedUserId();
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
                +BookshelfContract.Books.GENRES+" TEXT NOT NULL)"); //TODO simulate ENUM type for genres column

        db.execSQL("CREATE TABLE "+BookshelfContract.Reviews.TABLE_NAME+" ("
                +BookshelfContract.Reviews.REVIEW_ID+" INTEGER PRIMARY KEY AUTOINCREMENT,"
                +BookshelfContract.Reviews.BOOK_ID+" INTEGER NOT NULL, "
                +BookshelfContract.Reviews.USER_ID+ " INTEGER NOT NULL,"
                +BookshelfContract.Reviews.CONTENT + " TEXT,"
                +BookshelfContract.Reviews.RATING + " NUMERIC NOT NULL,"
                + "FOREIGN KEY(" + BookshelfContract.Reviews.BOOK_ID + ") "
                + "REFERENCES " +BookshelfContract.Books.TABLE_NAME + "(" + BookshelfContract.Books.BOOK_ID + "), "
                + "FOREIGN KEY(" + BookshelfContract.Reviews.USER_ID + ") "
                + "REFERENCES " +BookshelfContract.Users.TABLE_NAME + "(" + BookshelfContract.Users.USER_ID + ")) ");


        db.execSQL("CREATE TABLE "+BookshelfContract.Users_Books.TABLE_NAME+" ("
                +BookshelfContract.Users_Books.USER_ID+" INTEGER NOT NULL,"
                +BookshelfContract.Users_Books.BOOK_ID+" INTEGER NOT NULL,"
                +BookshelfContract.Users_Books.STATUS+" TEXT NOT NULL," //TODO simulate ENUM type for status column
                +BookshelfContract.Users_Books.ADDED_DATE+" TEXT NOT NULL,"
                +BookshelfContract.Users_Books.UPDATED_DATE+" TEXT, "
                + "FOREIGN KEY(" + BookshelfContract.Reviews.USER_ID + ") "
                + "REFERENCES " +BookshelfContract.Users.TABLE_NAME + "(" + BookshelfContract.Users.USER_ID + "), "
                + "FOREIGN KEY(" + BookshelfContract.Reviews.BOOK_ID + ") "
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
                +BookshelfContract.Books.GENRES+" TEXT NOT NULL)"); //TODO simulate ENUM type for genres column

        db.execSQL("CREATE TABLE "+BookshelfContract.Reviews.TABLE_NAME+" ("
                +BookshelfContract.Reviews.REVIEW_ID+" INTEGER PRIMARY KEY AUTOINCREMENT,"
                +BookshelfContract.Reviews.BOOK_ID+" INTEGER NOT NULL, "
                +BookshelfContract.Reviews.USER_ID+ " INTEGER NOT NULL,"
                +BookshelfContract.Reviews.CONTENT + " TEXT,"
                +BookshelfContract.Reviews.RATING + " NUMERIC NOT NULL,"
                + "FOREIGN KEY(" + BookshelfContract.Reviews.BOOK_ID + ") "
                + "REFERENCES " +BookshelfContract.Books.TABLE_NAME + "(" + BookshelfContract.Books.BOOK_ID + "), "
                + "FOREIGN KEY(" + BookshelfContract.Reviews.USER_ID + ") "
                + "REFERENCES " +BookshelfContract.Users.TABLE_NAME + "(" + BookshelfContract.Users.USER_ID + ")) ");


        db.execSQL("CREATE TABLE "+BookshelfContract.Users_Books.TABLE_NAME+" ("
                +BookshelfContract.Users_Books.USER_ID+" INTEGER NOT NULL,"
                +BookshelfContract.Users_Books.BOOK_ID+" INTEGER NOT NULL,"
                +BookshelfContract.Users_Books.STATUS+" TEXT NOT NULL," //TODO simulate ENUM type for status column
                +BookshelfContract.Users_Books.ADDED_DATE+" TEXT NOT NULL,"
                +BookshelfContract.Users_Books.UPDATED_DATE+" TEXT, "
                + "FOREIGN KEY(" + BookshelfContract.Reviews.USER_ID + ") "
                + "REFERENCES " +BookshelfContract.Users.TABLE_NAME + "(" + BookshelfContract.Users.USER_ID + "), "
                + "FOREIGN KEY(" + BookshelfContract.Reviews.BOOK_ID + ") "
                + "REFERENCES " +BookshelfContract.Books.TABLE_NAME + "(" + BookshelfContract.Books.BOOK_ID + ")) ");
    }
}
