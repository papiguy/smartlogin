package studios.codelight.smartloginlibrary.providers;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Parcel;
import android.support.v7.app.AppCompatActivity;

import com.linkedin.platform.LISession;
import com.linkedin.platform.LISessionManager;
import com.linkedin.platform.errors.LIAuthError;
import com.linkedin.platform.listeners.AuthListener;
import com.linkedin.platform.utils.Scope;

import java.util.ArrayList;

import studios.codelight.smartloginlibrary.R;
import studios.codelight.smartloginlibrary.session.SmartUser;

/**
 * Created by nitin on 9/9/16.
 */
public class LinkedInLoginProvider extends LoginProvider implements AuthListener {


    Creator<LinkedInLoginProvider> CREATOR = new Creator<LinkedInLoginProvider>() {
        @Override
        public LinkedInLoginProvider createFromParcel(Parcel parcel) {
            return new LinkedInLoginProvider(parcel);
        }

        @Override
        public LinkedInLoginProvider[] newArray(int i) {
            return new LinkedInLoginProvider[0];
        }
    };

    protected LinkedInLoginProvider(Parcel in) {
        super(in);
    }

    public LinkedInLoginProvider() {
        super();

        setIdentifier(LoginProviderId.LINKEDIN);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            setProviderLogo(R.drawable.linkedin_vector);
        } else {
            setProviderLogo(R.drawable.ic_linkedin_white_36dp);
        }
    }

    @Override
    public boolean signIn(SmartUser user, AppCompatActivity callingActivity) {

        LISessionManager.getInstance(context()).init(callingActivity, scopeFromPersmissions(appPermissions()), this, true);
        return false;
    }

    @Override
    public boolean signUp(SmartUser newUser, Activity callingActivity) {
        return false;
    }

    @Override
    public boolean logout(SmartUser user) {


        LISessionManager.getInstance(context()).clearSession();
        return true;
    }

    @Override
    public void onActivityResult(Activity callingActivity, int resultCode, Intent data, int requestCode) {
        // Add this line to your existing onActivityResult() method
        LISessionManager.getInstance(context()).onActivityResult(callingActivity, requestCode, resultCode, data);
    }

    @Override
    public boolean isLoggedIn(SmartUser user) {
        return false;
    }

    private Scope scopeFromPersmissions(ArrayList<String> permissions){
        Scope.LIPermission[] masterSet = new Scope.LIPermission[ ]{
                Scope.R_BASICPROFILE,
                Scope.R_CONTACTINFO,
                Scope.R_EMAILADDRESS,
                Scope.RW_COMPANY_ADMIN,
                Scope.W_SHARE,
                Scope.R_FULLPROFILE
        };
        Scope.LIPermission [] liPermissions = new Scope.LIPermission[permissions.size()];

        int i = 0;
        for(String permission : permissions){
            for(Scope.LIPermission liPermission : masterSet) {
                if (permission.equalsIgnoreCase(liPermission.getName())){
                    liPermissions[i++] = liPermission;
                    break;
                }
            }
        }

        return Scope.build(liPermissions);

    }

    @Override
    public void onAuthSuccess() {
        LISessionManager sessionManager = LISessionManager.getInstance(context());
        LISession session = sessionManager.getSession();

    }

    @Override
    public void onAuthError(LIAuthError error) {

    }
}
