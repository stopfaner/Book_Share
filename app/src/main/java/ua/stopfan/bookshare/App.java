package ua.stopfan.bookshare;

import android.app.Application;
import android.content.Intent;
import android.util.Log;

import com.facebook.AccessToken;
import com.facebook.FacebookSdk;
import com.parse.Parse;

import ua.stopfan.bookshare.Activities.LoginActivity;
import ua.stopfan.bookshare.Utilities.Constants;

/**
 * Created by stopfan on 3/22/15.
 */
public class App extends Application {

    private static String LOG_TAG = App.class.getName();

    @Override
    public void onCreate() {
        super.onCreate();

        FacebookSdk.sdkInitialize(getApplicationContext());

        Parse.enableLocalDatastore(getApplicationContext());
        Parse.initialize(getApplicationContext(), Constants.PARSE_APPLICATION_ID, Constants.PARSE_CLIEN_KEY);
    }
}
