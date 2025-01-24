package db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class SQLiteBooksHelper extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "Bookshelf";


    public SQLiteBooksHelper(Context context){
        super(context, DATABASE_NAME,null,DATABASE_VERSION);
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
