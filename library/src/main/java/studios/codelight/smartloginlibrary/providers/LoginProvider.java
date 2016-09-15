package studios.codelight.smartloginlibrary.providers;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;

import java.util.ArrayList;

import studios.codelight.smartloginlibrary.session.SmartUser;

/**
 * Created by nitin on 9/9/16.
 */
public abstract class LoginProvider implements Parcelable {

    public enum LoginType {
        Username,
        Email
    }

    public interface LoginStatusListener {
        void loginSuccessful(SmartUser user);

        void loginFailed();

        void loggedOut();
    }

    private String providerName;
    private LoginProviderId identifier;
    private ArrayList<String> appPermissions;
    private int providerLogo;
    private int providerSignInText;
    private String appId;
    private LoginType loginType;
    private LoginStatusListener listener;
    private Context mContext;

    private Handler mUIHandler = new Handler(Looper.myLooper());

    public LoginProvider() {

    }

    protected LoginProvider(Parcel in) {
        providerName = in.readString();
        identifier = LoginProviderId.from(in.readInt());
        int hasPermissions = in.readInt();
        if (hasPermissions == 1) {
            appPermissions = in.createStringArrayList();
        }
        providerLogo = in.readInt();
        providerSignInText = in.readInt();
        int hasAppId = in.readInt();
        if (hasAppId == 1) {
            appId = in.readString();
        }
        int hasLoginType = in.readInt();
        if (hasLoginType == 1) {
            loginType = (LoginType) in.readSerializable();
        }
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(providerName);
        dest.writeInt(identifier.toInt());
        if (appPermissions != null && appPermissions.size() > 0) {
            dest.writeInt(1);
            dest.writeStringList(appPermissions);
        } else {
            dest.writeInt(0);
        }
        dest.writeInt(providerLogo);
        dest.writeInt(providerSignInText);
        if (appId != null && !appId.isEmpty()) {

            dest.writeString(appId);
        } else {
            dest.writeInt(0);
        }
        if (loginType != null) {
            dest.writeInt(1);
            dest.writeSerializable(loginType);
        } else {
            dest.writeInt(0);
        }
    }

    public int providerSignInText() {
        return providerSignInText;
    }

    public void setProviderSignInText(int resId) {
        providerSignInText = resId;
    }

    public String providerName() {
        return providerName;
    }

    public void setProviderName(String providerName) {
        this.providerName = providerName;
    }

    public LoginProviderId identifier() {
        return identifier;
    }

    public void setIdentifier(LoginProviderId identifier) {
        this.identifier = identifier;
    }

    public ArrayList<String> appPermissions() {
        return appPermissions;
    }

    public void setAppPermissions(ArrayList<String> appPermissions) {
        this.appPermissions = appPermissions;
    }

    public int providerLogo() {
        return providerLogo;
    }

    public void setProviderLogo(int providerLogo) {
        this.providerLogo = providerLogo;
    }

    public String appId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

    public LoginType loginType() {
        return loginType;
    }

    public void setLoginType(LoginType loginType) {
        this.loginType = loginType;
    }

    public void setListener(LoginStatusListener listener) {
        this.listener = listener;
    }

    protected LoginStatusListener loginListener() {
        return listener;
    }

    protected String getString(int resId) {
        return mContext.getString(resId);
    }

    protected Context context() {
        return mContext;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public abstract void onActivityResult(Activity callingActivity, int resultCode, Intent data, int requestCode);

    public void sdkInitializer(Context context) {
        mContext = context;
    }

    public abstract boolean isLoggedIn(SmartUser user);

    public abstract boolean signIn(SmartUser user, AppCompatActivity callingActivity);

    public abstract boolean signUp(SmartUser newUser, Activity callingActivity);

    public abstract boolean logout(SmartUser user);

    protected void runOnUiThread(Runnable r){
        mUIHandler.post(r);
    }
}
