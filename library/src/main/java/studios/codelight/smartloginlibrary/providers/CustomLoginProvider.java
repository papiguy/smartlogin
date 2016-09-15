package studios.codelight.smartloginlibrary.providers;

import android.app.Activity;
import android.content.Intent;
import android.os.Parcel;
import android.support.v7.app.AppCompatActivity;

import studios.codelight.smartloginlibrary.session.SmartUser;

/**
 * Created by nitin on 9/9/16.
 */

public class CustomLoginProvider extends LoginProvider {

    public static Creator<CustomLoginProvider> CREATOR = new Creator<CustomLoginProvider>() {
        @Override
        public CustomLoginProvider createFromParcel(Parcel parcel) {
            return new CustomLoginProvider(parcel);
        }

        @Override
        public CustomLoginProvider[] newArray(int i) {
            return new CustomLoginProvider[0];
        }
    };

    protected CustomLoginProvider(Parcel in) {
        super(in);
    }

    public CustomLoginProvider() {
        super();
    }

    @Override
    public boolean signIn(SmartUser user, AppCompatActivity callingActivity) {
        return false;
    }


    @Override
    public boolean signUp(SmartUser newUser, Activity callingActivity) {




        return false;
    }

    @Override
    public boolean logout(SmartUser user) {
        return false;
    }

    @Override
    public void onActivityResult(Activity callingActivity, int resultCode, Intent data, int requestCode) {

    }

    @Override
    public boolean isLoggedIn(SmartUser user) {
        return false;
    }


    public SmartUser populateCustomUserWithUserName(String username, String email, String password){
        SmartUser user = new SmartUser();
        user.setEmail(email);
        user.setUsername(username);
        user.setPassword(password);
        user.setGender(-1);
        return user;
    }

    public SmartUser populateCustomUserWithEmail(String username, String email, String password){
        SmartUser user = new SmartUser();
        user.setEmail(email);
        user.setUsername(username);
        user.setPassword(password);
        user.setGender(-1);
        return user;
    }
}
