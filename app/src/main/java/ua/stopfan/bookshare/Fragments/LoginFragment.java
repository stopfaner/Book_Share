package ua.stopfan.bookshare.Fragments;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

import ua.stopfan.bookshare.MainActivity;
import ua.stopfan.bookshare.R;

/**
 * Created by denys on 4/20/15.
 */
public class LoginFragment extends Fragment {

    private final String LOG_TAG = LoginFragment.class.getSimpleName();

    private LoginButton loginButton;
    private TextView textView;

    private CallbackManager callbackManager;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(this.getActivity().getApplicationContext());
        callbackManager = CallbackManager.Factory.create();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.activity_login, container, false);

        textView = (TextView) rootView.findViewById(R.id.textView);
        textView.setTypeface(null, Typeface.BOLD);

        loginButton = (LoginButton) rootView.findViewById(R.id.login_button);
        loginButton.setReadPermissions("user_friends", "user_photos", "user_about_me");
        loginButton.setFragment(this);
        loginButton.setBackgroundResource(R.drawable.facebook);

        if(AccessToken.getCurrentAccessToken() == null) {
            loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
                @Override
                public void onSuccess(LoginResult loginResult) {
                    Log.d(LOG_TAG, "Success login");
                    startActivity(new Intent(getActivity().getApplicationContext(), MainActivity.class));
                }

                @Override
                public void onCancel() {
                    Log.d(LOG_TAG, "Cancel login");
                }

                @Override
                public void onError(FacebookException e) {
                    e.printStackTrace();
                }
            });
        } else {
            startActivity(new Intent(getActivity().getApplicationContext(), MainActivity.class));
        }

        return rootView;
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();

        if(AccessToken.getCurrentAccessToken() != null)
            startActivity(new Intent(getActivity().getApplicationContext(), MainActivity.class));
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

}
