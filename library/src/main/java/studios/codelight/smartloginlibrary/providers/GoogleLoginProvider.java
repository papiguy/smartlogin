package studios.codelight.smartloginlibrary.providers;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Parcel;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;

import studios.codelight.smartloginlibrary.R;
import studios.codelight.smartloginlibrary.session.SmartUser;


/**
 * Created by nitin on 9/9/16.
 */
public class GoogleLoginProvider extends LoginProvider implements GoogleApiClient.OnConnectionFailedListener {


    private static final int RC_SIGN_IN = 105;

    Creator<GoogleLoginProvider> CREATOR = new Creator<GoogleLoginProvider>() {
        @Override
        public GoogleLoginProvider createFromParcel(Parcel parcel) {
            return new GoogleLoginProvider(parcel);
        }

        @Override
        public GoogleLoginProvider[] newArray(int i) {
            return new GoogleLoginProvider[0];
        }
    };
    //Google Sign in related
    private GoogleApiClient mGoogleApiClient;

    protected GoogleLoginProvider(Parcel in) {
        super(in);
    }

    public GoogleLoginProvider() {
        super();

        setIdentifier(LoginProviderId.GOOGLE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            setProviderLogo(R.drawable.google_plus_vector);
        } else {
            setProviderLogo(R.drawable.ic_google_plus_white_36dp);
        }
        setProviderSignInText(R.string.google_login_text);
    }

    @Override
    public boolean signIn(SmartUser user, AppCompatActivity callingActivity) {

        // User clicked the sign-in button, so begin the sign-in process and automatically
        // attempt to resolve any errors that occur.
        ProgressDialog progress = ProgressDialog.show(callingActivity, "", getString(R.string.logging_holder), true);


        // Configure sign-in to request the user's ID, email address, and basic
        // profile. ID and basic profile are included in DEFAULT_SIGN_IN.
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .requestProfile()
                .build();

        mGoogleApiClient = new GoogleApiClient.Builder(context())
                .enableAutoManage(callingActivity /* FragmentActivity */, this /* On~ConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        callingActivity.startActivityForResult(signInIntent, RC_SIGN_IN);
        progress.dismiss();

        return true;
    }

    @Override
    public boolean signUp(SmartUser newUser, Activity callingActivity) {
        return false;
    }

    @Override
    public boolean logout(SmartUser user) {
        if (mGoogleApiClient == null || !mGoogleApiClient.isConnected()) {
            return false;
        }
        final boolean[] logoutStatus = {false};
        Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(new ResultCallback<Status>() {
            @Override
            public void onResult(Status status) {
                if (status.isSuccess()) {
                    logoutStatus[0] = true;
                }
            }
        });
        return logoutStatus[0];
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        // Could not connect to Google Play Services.  The user needs to select an account,
        // grant permissions or resolve an error in order to sign in. Refer to the javadoc for
        // ConnectionResult to see possible error codes.
        final String TAG = "GOOGLE LOGIN";
        Log.d(TAG, "onConnectionFailed:" + connectionResult);
        Toast.makeText(context(), R.string.network_error, Toast.LENGTH_SHORT).show();
    }

    public void onActivityResult(Activity callingActivity, int resultCode, Intent data, int requestCode) {

        ProgressDialog progress = ProgressDialog.show(context(), "", getString(R.string.getting_data), true);
        GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
        Log.d("GOOGLE SIGN IN", "handleSignInResult:" + result.isSuccess());
        if (result.isSuccess()) {
            // Signed in successfully, show authenticated UI.
            GoogleSignInAccount acct = result.getSignInAccount();
            SmartUser googleUser = populateGoogleUser(acct);
            progress.dismiss();
            loginListener().loginSuccessful(googleUser);
        } else {
            Log.d("GOOGLE SIGN IN", "" + requestCode);
            // Signed out, show unauthenticated UI.
            progress.dismiss();
            Toast.makeText(context(), "Google Login Failed", Toast.LENGTH_SHORT).show();
            loginListener().loggedOut();
        }

        if (progress != null) {
            progress.dismiss();
        }
    }

    @Override
    public boolean isLoggedIn(SmartUser user) {
        return false;
    }


    public SmartUser populateGoogleUser(GoogleSignInAccount account){
        //Create a new google user
        SmartUser googleUser = new SmartUser();
        //populate the user
        googleUser.setDisplayName(account.getDisplayName());
        //googleUser.setIdToken(account.getIdToken());
        googleUser.setPhotoUrl(account.getPhotoUrl());
        googleUser.setEmail(account.getEmail());
        //googleUser.setServerAuthCode(account.getServerAuthCode());

        //return the populated google user
        return googleUser;
    }

}
