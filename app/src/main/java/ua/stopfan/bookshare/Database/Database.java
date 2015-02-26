package ua.stopfan.bookshare.Database;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

/**
 * Created by stopfan on 12/14/14.
 */
public class Database {

    private Context context;
    private DBProvider dbProvider;
    private ContentValues cv;
    private SQLiteDatabase db;

    private final static String LOG_TAG = "Database";

    public Database(Context context) {
        this.context = context;
    }
}
