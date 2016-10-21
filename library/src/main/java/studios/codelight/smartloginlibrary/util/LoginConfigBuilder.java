package studios.codelight.smartloginlibrary.util;

import android.app.Notification;
import android.content.Context;
import android.content.Intent;

import java.util.ArrayList;

import studios.codelight.smartloginlibrary.LoginActivity;
import studios.codelight.smartloginlibrary.providers.FacebookLoginProvider;
import studios.codelight.smartloginlibrary.providers.LoginProvider;
import studios.codelight.smartloginlibrary.providers.LoginProviderFactory;
import studios.codelight.smartloginlibrary.providers.LoginProviderId;

import static studios.codelight.smartloginlibrary.LoginActivity.EXTRA_LOGIN_CONFIG;

/**
 * Created by Kalyan on 9/9/2015.
 */
public class LoginConfigBuilder {

    private Context context;
    private LoginConfig config;


    public LoginConfigBuilder() {
        config = new LoginConfig();
        config.setAppLogo(0);
    }

    public LoginConfigBuilder withContext(Context context) {
        this.context = context;
        return this;
    }

    public LoginConfigBuilder setAppLogo(int logo) {
        config.setAppLogo(logo);
        return this;
    }


    public LoginConfigBuilder addLoginHelper(LoginProviderId loginProvider) {
        config.addLoginProvider(loginProvider);
        return this;
    }

    public LoginConfigBuilder enableFacebook(String appId) {
        return enableFacebook(appId, FacebookLoginProvider.getDefaultFacebookPermissions());
    }

    public LoginConfigBuilder enableFacebook(String appId, ArrayList<String> facebookPermissions) {
        LoginProvider loginProvider = LoginProviderFactory.getInstanceFor(LoginProviderId.FACEBOOK);
        loginProvider.setAppId(appId);
        loginProvider.setAppPermissions(facebookPermissions);
        //Must be the last call
        loginProvider.sdkInitializer(context);
        config.addLoginProvider(LoginProviderId.FACEBOOK);
        return this;
    }

    public LoginConfigBuilder enableGoogle() {
        LoginProvider loginProvider = LoginProviderFactory.getInstanceFor(LoginProviderId.GOOGLE);
        //Must be the last call
        loginProvider.sdkInitializer(context);
        config.addLoginProvider(LoginProviderId.GOOGLE);
        return this;
    }


    public LoginConfigBuilder enableLinkedIn(ArrayList<String> linkedInPermissions) {
        LoginProvider loginProvider = LoginProviderFactory.getInstanceFor(LoginProviderId.LINKEDIN);
        loginProvider.setAppPermissions(linkedInPermissions);
        //Must be the last call
        loginProvider.sdkInitializer(context);

        config.addLoginProvider(LoginProviderId.LINKEDIN);
        return this;
    }

    public LoginConfig build(){
        return config;
    }


}
