package ua.stopfan.bookshare.Database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by stopfan on 12/14/14.
 */
public class DBProvider extends SQLiteOpenHelper {

    public DBProvider(Context context) {
        super(context, "database", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL("create table library ("
            + "id integer primary key autoincrement,"
            + "book_id integer"
            + "book_name text"
            + "author_name text");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
