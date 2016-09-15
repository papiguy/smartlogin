package studios.codelight.smartloginlibrary.util;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

import studios.codelight.smartloginlibrary.providers.LoginProviderId;

public class LoginConfig implements Parcelable {


    private int appLogo;
    private ArrayList<LoginProviderId> loginProviders;


    public LoginConfig() {
        appLogo = 0;
        loginProviders = new ArrayList();
    }

    protected LoginConfig(Parcel in) {
        appLogo = in.readInt();
        loginProviders = new ArrayList();
        for (Object loginProviderId : in.readArray(LoginProviderId.class.getClassLoader())) {
            loginProviders.add((LoginProviderId) loginProviderId);
        }
    }


    public int getAppLogo() {
        return appLogo;
    }

    public void setAppLogo(int appLogo) {
        this.appLogo = appLogo;
    }


    public void addLoginProvider(LoginProviderId providerId) {
        loginProviders.add(providerId);
    }



    public boolean isFacebookEnabled(){
        return isProviderEnabled(LoginProviderId.FACEBOOK);
    }

    public boolean isCustomLoginEnabled(){
        return isProviderEnabled(LoginProviderId.CUSTOM);
    }

    public boolean isGoogleEnabled(){
        return isProviderEnabled(LoginProviderId.GOOGLE);
    }

    public boolean isLinkedInEnabled(){
        return isProviderEnabled(LoginProviderId.LINKEDIN);
    }

    private boolean isProviderEnabled(LoginProviderId providerId){
        for(LoginProviderId loginProviderId  : loginProviders){
            if (loginProviderId == providerId) return true;
        }
        return false;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(appLogo);
        dest.writeArray(loginProviders.toArray());
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<LoginConfig> CREATOR = new Creator<LoginConfig>() {
        @Override
        public LoginConfig createFromParcel(Parcel in) {
            return new LoginConfig(in);
        }

        @Override
        public LoginConfig[] newArray(int size) {
            return new LoginConfig[size];
        }
    };

}
