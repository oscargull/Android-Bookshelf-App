package utils;

import android.content.Context;
import android.content.SharedPreferences;
import db.SQLiteBooksHelper;
import models.Book;

import java.util.ArrayList;
import java.util.List;

public class UserManager {
    //NO SRIVE
    private static UserManager instance;
    private SQLiteBooksHelper DBHelper;
    private String loggedUser;
    private Context context;

    private UserManager(Context con) {
        this.context = con.getApplicationContext(); // Avoid memory leaks
        DBHelper = new SQLiteBooksHelper(this.context);

       SharedPreferences prefs = context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE);
        loggedUser = prefs.getString("logged_user", null);
    }

    public static synchronized UserManager getInstance(Context con) {
        if (instance == null) {
            instance = new UserManager(con);
        }
        return instance;
    }

    public String getLoggedUser() {
        return loggedUser;
    }

    public void setLoggedUser(String loggedUser) {
        this.loggedUser = loggedUser;

      SharedPreferences prefs = context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("logged_user", loggedUser);
        editor.apply();
    }
}