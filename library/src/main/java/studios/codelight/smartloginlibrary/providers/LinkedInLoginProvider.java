package studios.codelight.smartloginlibrary.providers;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Parcel;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.linkedin.platform.APIHelper;
import com.linkedin.platform.LISession;
import com.linkedin.platform.LISessionManager;
import com.linkedin.platform.errors.LIApiError;
import com.linkedin.platform.errors.LIAuthError;
import com.linkedin.platform.listeners.ApiListener;
import com.linkedin.platform.listeners.ApiResponse;
import com.linkedin.platform.listeners.AuthListener;
import com.linkedin.platform.utils.Scope;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import studios.codelight.smartloginlibrary.R;
import studios.codelight.smartloginlibrary.session.SmartUser;

/**
 * Created by nitin on 9/9/16.
 */
public class LinkedInLoginProvider extends LoginProvider implements AuthListener {


    private static final String API_LINKEDIN_COM = "api.linkedin.com";
    private static final String GET_USER_PROFILE_ENDPOINT = "https://" + API_LINKEDIN_COM + "/v1/people/~:(id,first-name,last-name,public-profile-url,picture-url,email-address,formatted-name,date-of-birth)?format=json";
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
        setProviderSignInText(R.string.linkedin_login_text);
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

    private Scope scopeFromPersmissions(ArrayList<String> permissions) {
        Scope.LIPermission[] masterSet = new Scope.LIPermission[]{
                Scope.R_BASICPROFILE,
                Scope.R_CONTACTINFO,
                Scope.R_EMAILADDRESS,
                Scope.RW_COMPANY_ADMIN,
                Scope.W_SHARE,
                Scope.R_FULLPROFILE
        };

        Scope.LIPermission[] liPermissions;

        if (permissions == null || permissions.size() == 0) {
            liPermissions = masterSet;
        } else {
            liPermissions = new Scope.LIPermission[permissions.size()];

            int i = 0;
            for (String permission : permissions) {
                for (Scope.LIPermission liPermission : masterSet) {
                    if (permission.equalsIgnoreCase(liPermission.getName())) {
                        liPermissions[i++] = liPermission;
                        break;
                    }
                }
            }
        }
        return Scope.build(liPermissions);
    }

    @Override
    public void onAuthSuccess() {
        LISessionManager sessionManager = LISessionManager.getInstance(context());
        LISession session = sessionManager.getSession();
        boolean accessTokenValid = session.isValid();
        if (!accessTokenValid) {
            loginListener().loginFailed();
            return;
        }
        getUserDetails();
    }

    @Override
    public void onAuthError(LIAuthError error) {
        loginListener().loginFailed();
    }

    public void getUserDetails() {
        APIHelper apiHelper = APIHelper.getInstance(context());
        apiHelper.getRequest(context(), GET_USER_PROFILE_ENDPOINT, new OnLoginResponseHandler());
    }

    public static class LinkedInFields {
        public static final String EMAIL = "emailAddress";
        public static final String ID = "id";
        public static final String PICTURE_URL = "pictureUrl";
        public static final String FIRST_NAME = "firstName";
        public static final String LAST_NAME = "lastName";
        public static final String NAME = "formattedName";
        public static final String LINK = "publicProfileUrl";
    }

    class OnLoginResponseHandler implements ApiListener {
        @Override
        public void onApiSuccess(ApiResponse s) {
            SmartUser linkedinUser = populateLinkedInUser(s.getResponseDataAsJson());
            if (linkedinUser != null) {
                loginListener().loginSuccessful(linkedinUser);
            } else {
                loginListener().loginFailed();
            }
        }

        @Override
        public void onApiError(LIApiError error) {
            loginListener().loginFailed();
        }

        public SmartUser populateLinkedInUser(JSONObject object) {
            SmartUser linkedInUser = new SmartUser();
            linkedInUser.setProviderId(LoginProviderId.LINKEDIN);
            linkedInUser.setGender(-1);
            try {
                if (object.has(LinkedInFields.EMAIL))
                    linkedInUser.setEmail(object.getString(LinkedInFields.EMAIL));

                if (object.has(LinkedInFields.LINK))
                    linkedInUser.setProfileLink(object.getString(LinkedInFields.LINK));
                if (object.has(LinkedInFields.ID)) {
                    linkedInUser.setUserId(object.getString(LinkedInFields.ID));
                }
                if (object.has(LinkedInFields.PICTURE_URL)) {
                    linkedInUser.setPhotoUrl(object.getString(LinkedInFields.PICTURE_URL));
                }
                if (object.has(LinkedInFields.NAME))
                    linkedInUser.setDisplayName(object.getString(LinkedInFields.NAME));
                if (object.has(LinkedInFields.FIRST_NAME))
                    linkedInUser.setFirstName(object.getString(LinkedInFields.FIRST_NAME));
                if (object.has(LinkedInFields.LAST_NAME))
                    linkedInUser.setLastName(object.getString(LinkedInFields.LAST_NAME));

            } catch (JSONException e) {
                Log.e(getClass().getSimpleName(), e.getMessage());
                linkedInUser = null;
            }
            return linkedInUser;
        }
    }


}
