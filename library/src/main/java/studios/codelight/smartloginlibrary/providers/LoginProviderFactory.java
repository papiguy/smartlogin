package studios.codelight.smartloginlibrary.providers;

import android.app.Activity;
import android.content.Intent;

import java.util.HashMap;

import studios.codelight.smartloginlibrary.LoginActivity;
import studios.codelight.smartloginlibrary.util.LoginConfig;

import static studios.codelight.smartloginlibrary.LoginActivity.EXTRA_LOGIN_CONFIG;


/**
 * Created by nitin on 9/9/16.
 */

public class LoginProviderFactory {


    private static LoginConfig mConfig;
    private static HashMap<LoginProviderId, LoginProvider> providerMap = new HashMap<LoginProviderId, LoginProvider>();

    public static LoginProvider getInstanceFor(LoginProviderId providerId) {
        LoginProvider loginProvider = providerMap.get(providerId);
        if (loginProvider == null && providerId != LoginProviderId.CUSTOM) {
            switch (providerId) {
                case FACEBOOK:
                    loginProvider = new FacebookLoginProvider();
                    break;
                case GOOGLE:
                    loginProvider = new GoogleLoginProvider();
                    break;
                case LINKEDIN:
                    loginProvider = new LinkedInLoginProvider();
                    break;
            }
            providerMap.put(providerId, loginProvider);
        }
        return loginProvider;
    }

    public static void addCustomProvider(LoginProvider loginProvider){
        providerMap.put(LoginProviderId.CUSTOM, loginProvider);
    }

    public static Intent intentForLoginActivity(Activity context) {
        Intent intent = new Intent(context, LoginActivity.class);
        intent.putExtra(EXTRA_LOGIN_CONFIG, mConfig);
        return intent;
    }

    public static void configure(LoginConfig config){
        mConfig = config;
    }
}
