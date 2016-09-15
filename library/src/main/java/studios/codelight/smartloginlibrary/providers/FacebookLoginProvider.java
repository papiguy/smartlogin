package studios.codelight.smartloginlibrary.providers;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.os.Parcel;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import studios.codelight.smartloginlibrary.R;
import studios.codelight.smartloginlibrary.session.SmartUser;

/**
 * Created by nitin on 9/9/16.
 */

public class FacebookLoginProvider extends LoginProvider implements FacebookCallback<LoginResult> {


    public static Creator<FacebookLoginProvider> CREATOR = new Creator<FacebookLoginProvider>() {
        @Override
        public FacebookLoginProvider createFromParcel(Parcel parcel) {
            return new FacebookLoginProvider(parcel);
        }

        @Override
        public FacebookLoginProvider[] newArray(int i) {
            return new FacebookLoginProvider[0];
        }
    };


    public FacebookLoginProvider() {
        super();
        setIdentifier(LoginProviderId.FACEBOOK);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            setProviderLogo(R.drawable.facebook_vector);
        } else {
            setProviderLogo(R.drawable.ic_facebook_white_36dp);
        }
    }


    protected FacebookLoginProvider(Parcel in) {
        super(in);
    }

    CallbackManager mCallbackManager;
    ProgressDialog progress;
    AccessTokenTracker accessTokenTracker;

    @Override
    public void sdkInitializer(Context context) {
        super.sdkInitializer(context);
        FacebookSdk.sdkInitialize(context);

        //Set the facebook app id and initialize sdk
        FacebookSdk.setApplicationId(appId());
        //Facebook login callback
        mCallbackManager = CallbackManager.Factory.create();

        accessTokenTracker = new AccessTokenTracker() {
            @Override
            protected void onCurrentAccessTokenChanged(
                    AccessToken oldAccessToken,
                    AccessToken currentAccessToken) {
                if (AccessToken.getCurrentAccessToken() == null) {
                    //TODO transmit an event that user has been signed out
                }

            }
        };

        accessTokenTracker.startTracking();
    }

    @Override
    public boolean isLoggedIn(SmartUser user) {
        if (user != null) {
            if (AccessToken.getCurrentAccessToken() != null) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean signIn(final SmartUser user, final AppCompatActivity callingActivity) {
        progress = ProgressDialog.show(callingActivity, "", getString(R.string.logging_holder), true);
        Toast.makeText(callingActivity, "Facebook login", Toast.LENGTH_SHORT).show();
        LoginManager.getInstance().logInWithPublishPermissions(callingActivity, appPermissions());
        LoginManager.getInstance().registerCallback(mCallbackManager, this);


        return true;
    }

    @Override
    public boolean signUp(SmartUser newUser, Activity callingActivity) {

        //this functionality is not supported by this provider
        return false;
    }

    @Override
    public boolean logout(SmartUser user) {
        LoginManager.getInstance().logOut();
        return true;
    }

    public void onActivityResult(Activity callingActivity, int resultCode, Intent data, int requestCode) {
        mCallbackManager.onActivityResult(requestCode, resultCode, data);
    }


    public SmartUser populateFacebookUser(JSONObject object) {
        SmartUser facebookUser = new SmartUser();
        facebookUser.setGender(-1);
        try {
            if (object.has(FacebookFields.EMAIL))
                facebookUser.setEmail(object.getString(FacebookFields.EMAIL));
            if (object.has(FacebookFields.BIRTHDAY))
                facebookUser.setBirthday(object.getString(FacebookFields.BIRTHDAY));
            if (object.has(FacebookFields.GENDER)) {
                try {
                    SmartUser.Gender gender = SmartUser.Gender.valueOf(object.getString(FacebookFields.GENDER));
                    switch (gender) {
                        case male:
                            facebookUser.setGender(0);
                            break;
                        case female:
                            facebookUser.setGender(1);
                            break;
                    }
                } catch (Exception e) {
                    //if gender is not in the enum it is set to unspecified value (-1)
                    facebookUser.setGender(-1);
                    Log.e(getClass().getSimpleName(), e.getMessage());
                }
            }
            if (object.has(FacebookFields.LINK))
                facebookUser.setProfileLink(object.getString(FacebookFields.LINK));
            if (object.has(FacebookFields.ID))
                facebookUser.setUserId(object.getString(FacebookFields.ID));
            if (object.has(FacebookFields.NAME))
                facebookUser.setDisplayName(object.getString(FacebookFields.NAME));
            if (object.has(FacebookFields.FIRST_NAME))
                facebookUser.setFirstName(object.getString(FacebookFields.FIRST_NAME));
            if (object.has(FacebookFields.MIDDLE_NAME))
                facebookUser.setMiddleName(object.getString(FacebookFields.MIDDLE_NAME));
            if (object.has(FacebookFields.LAST_NAME))
                facebookUser.setLastName(object.getString(FacebookFields.LAST_NAME));
        } catch (JSONException e) {
            Log.e(getClass().getSimpleName(), e.getMessage());
            facebookUser = null;
        }
        return facebookUser;
    }

    @Override
    public void onSuccess(LoginResult loginResult) {
        progress.setMessage(getString(R.string.getting_data));
        GraphRequest request = GraphRequest.newMeRequest(loginResult.getAccessToken(), new GraphRequest.GraphJSONObjectCallback() {
            @Override
            public void onCompleted(JSONObject object, GraphResponse response) {
                progress.dismiss();
                SmartUser facebookUser = populateFacebookUser(object);
                if (facebookUser != null) {
                    loginListener().loginSuccessful(facebookUser);
                } else {
                    loginListener().loginFailed();
                }
            }
        });
        request.executeAsync();
    }

    @Override
    public void onCancel() {
        progress.dismiss();
        loginListener().loginFailed();
        Log.d("Facebook Login", "User cancelled the login process");
    }

    @Override
    public void onError(FacebookException error) {
        progress.dismiss();
        loginListener().loginFailed();
        Toast.makeText(context(), R.string.network_error, Toast.LENGTH_SHORT).show();
    }


    public static class FacebookFields {
        public static final String EMAIL = "email";
        public static final String ID = "id";
        public static final String BIRTHDAY = "birthday";
        public static final String GENDER = "gender";
        public static final String FIRST_NAME = "first_name";
        public static final String MIDDLE_NAME = "middle_name";
        public static final String LAST_NAME = "last_name";
        public static final String NAME = "name";
        public static final String LINK = "link";
    }

    public static ArrayList<String> getDefaultFacebookPermissions() {
        ArrayList<String> defaultPermissions = new ArrayList();
        defaultPermissions.add("public_profile");
        defaultPermissions.add("email");
        defaultPermissions.add("user_birthday");
        return defaultPermissions;
    }


}
