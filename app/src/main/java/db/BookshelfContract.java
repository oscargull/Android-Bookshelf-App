package db;

import android.provider.BaseColumns;

public class BookshelfContract {

    public static class Users implements BaseColumns {
        public static final String TABLE_NAME = "users";

        public static final String USER_ID = "user_id";
        public static final String USERNAME ="username";
        public static final String EMAIL ="email";
        public static final String SALT ="salt";
        public static final String HASHED_PASSWORD = "hashed_password";
    }
    public static class Books implements BaseColumns{
        public static final String TABLE_NAME = "books";

        public static final String BOOK_ID = "book_id";
        public static final String TITLE = "title";
        public static final String AUTHOR = "author";
        public static final String GENRES = "genres";
    }

    public static class Reviews implements BaseColumns{
        public static final String TABLE_NAME = "reviews";

        public static final String REVIEW_ID = "review_id";
        public static final String BOOK_ID = "book_id";
        public static final String USER_ID = "user_id";
        public static final String CONTENT = "content";
        public static final String RATING = "rating";
    }


    public static class Users_Books implements BaseColumns{
        public static final String TABLE_NAME = "users_books";

        public static final String USER_ID = "user_id";
        public static final String BOOK_ID = "book_id";
        public static final String STATUS = "status";
        public static final String ADDED_DATE = "added_date";
        public static final String UPDATED_DATE = "updated_date";
    }
}
