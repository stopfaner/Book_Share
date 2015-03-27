package ua.stopfan.bookshare;

import android.app.Application;
import android.util.Log;

import com.parse.Parse;

import ua.stopfan.bookshare.Utilities.Constants;

/**
 * Created by stopfan on 3/22/15.
 */
public class App extends Application {

    private static String LOG_TAG = App.class.getName();

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(LOG_TAG, "App class started");

        Parse.enableLocalDatastore(getApplicationContext());
        Parse.initialize(getApplicationContext(), Constants.PARSE_APPLICATION_ID, Constants.PARSE_CLIEN_KEY);
    }
}
