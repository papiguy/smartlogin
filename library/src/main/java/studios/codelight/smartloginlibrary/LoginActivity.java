package studios.codelight.smartloginlibrary;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.Toolbar;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;

import studios.codelight.smartloginlibrary.providers.LoginProvider;
import studios.codelight.smartloginlibrary.providers.LoginProviderFactory;
import studios.codelight.smartloginlibrary.providers.LoginProviderId;
import studios.codelight.smartloginlibrary.session.SmartUser;
import studios.codelight.smartloginlibrary.session.UserSessionManager;
import studios.codelight.smartloginlibrary.util.DialogUtil;
import studios.codelight.smartloginlibrary.util.LoginConfig;

import static studios.codelight.smartloginlibrary.session.UserSessionManager.USER_SESSION;

public class LoginActivity extends AppCompatActivity implements
        LoginProvider.LoginStatusListener {

    public static final int SIGNIN_SUCCESSFUL = 1;
    public static final int SIGNIN_FAILED = 0;
    public static String EXTRA_LOGIN_CONFIG = "login_config";

    protected EditText usernameEditText, passwordEditText, usernameSignup, emailSignup, passwordSignup, repeatPasswordSignup;
    protected ViewGroup mContainer;
    protected LinearLayout signinContainer, signupContainer;
    protected ImageView appLogo;


    protected LoginProviderId mProcessingLoginFor = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //get the config object from the intent and unpack it
        Bundle bundle = getIntent().getExtras();
        LoginConfig config = bundle.getParcelable(EXTRA_LOGIN_CONFIG);

        setContentView(R.layout.activity_smart_login);

        //Set the title and back button on the Action bar
        Toolbar toolbar = (Toolbar) findViewById(R.id.smart_login_toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Login");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        //Get the containers required to inject the views
        mContainer = (ViewGroup) findViewById(R.id.main_container);
        signinContainer = (LinearLayout) findViewById(R.id.signin_container);
        signupContainer = (LinearLayout) findViewById(R.id.signup_container);

        //Inject the views in the respective containers
        LayoutInflater layoutInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        //include views based on user settings
        if (config.isCustomLoginEnabled()) {
            signinContainer.addView(layoutInflater.inflate(R.layout.fragment_custom_login, mContainer, false));
            if (config.isFacebookEnabled() || config.isGoogleEnabled()) {
                signinContainer.addView(layoutInflater.inflate(R.layout.fragment_divider, mContainer, false));
            }
            signupContainer.addView(layoutInflater.inflate(R.layout.fragment_signup, mContainer, false));

            //listeners
            findViewById(R.id.custom_signin_button).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    doCustomSignin();
                }
            });
            findViewById(R.id.custom_signup_button).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showSignUpPane();
                }
            });
            findViewById(R.id.user_signup_button).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    doCustomSignup();
                }
            });

            //Hide necessary views
            signupContainer.setVisibility(View.GONE);
        }

        if (config.isFacebookEnabled()) {
            addLoginButtonForProvider(LoginProviderId.FACEBOOK);
        }

        if (config.isGoogleEnabled()) {
            addLoginButtonForProvider(LoginProviderId.GOOGLE);
        }

        if (config.isLinkedInEnabled()){
            addLoginButtonForProvider(LoginProviderId.LINKEDIN);
        }

        //bind the views
        appLogo = (ImageView) findViewById(R.id.applogo_imageView);
        usernameEditText = (EditText) findViewById(R.id.userNameEditText);
        passwordEditText = (EditText) findViewById(R.id.passwordEditText);
        usernameSignup = (EditText) findViewById(R.id.userNameSignUp);
        passwordSignup = (EditText) findViewById(R.id.passwordSignUp);
        repeatPasswordSignup = (EditText) findViewById(R.id.repeatPasswordSignUp);
        emailSignup = (EditText) findViewById(R.id.emailSignUp);

        //Set app logo
        if (config.getAppLogo() != 0) {
            appLogo.setImageResource(config.getAppLogo());
        } else {
            appLogo.setVisibility(View.GONE);
        }
    }

    /**
     * Take care of popping the fragment back stack or finishing the activity
     * as appropriate.
     */
    @Override
    public void onBackPressed() {
        if (signupContainer.getVisibility() == View.VISIBLE) {
            signupContainer.setVisibility(View.GONE);
            signinContainer.setVisibility(View.VISIBLE);
        } else {
            finish();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == android.R.id.home) {
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (mProcessingLoginFor != null){
            LoginProvider loginProvider = LoginProviderFactory.getInstanceFor(mProcessingLoginFor);
            loginProvider.onActivityResult(this, resultCode, data, requestCode);
        }

    }

    protected void addLoginButtonForProvider(final LoginProviderId providerId){
        View vg = LayoutInflater.from(this).inflate(R.layout.fragment_social_login, mContainer, false);
        AppCompatButton socialLoginButton = (AppCompatButton) vg.findViewById(R.id.social_login_button);

        LoginProvider loginProvider = LoginProviderFactory.getInstanceFor(providerId);
        socialLoginButton.setCompoundDrawablesWithIntrinsicBounds(loginProvider.providerLogo(), 0, 0, 0);
        socialLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doLoginForProvider(providerId);
            }
        });
        socialLoginButton.setText(loginProvider.providerSignInText());
        signinContainer.addView(vg);
    }

    private void doLoginForProvider(LoginProviderId providerId) {
        LoginProvider loginProvider = LoginProviderFactory.getInstanceFor(providerId);
        loginProvider.setListener(this);
        if (loginProvider.signIn(null, this)) {
            mProcessingLoginFor = providerId;
        } else {
            loginProvider.setListener(null);
        }
    }

    private void showSignUpPane(){
        signinContainer.setVisibility(View.GONE);
        signupContainer.setVisibility(View.VISIBLE);
        findViewById(R.id.userNameSignUp).requestFocus();
    }

    private void doCustomSignup() {
        String username = usernameSignup.getText().toString();
        String password = passwordSignup.getText().toString();
        String repeatPassword = repeatPasswordSignup.getText().toString();
        String email = emailSignup.getText().toString();
        if (username.equals("")) {
            //DialogUtil.getErrorDialog(R.string.username_error, this).show();
            usernameSignup.setError(getResources().getText(R.string.username_error));
            usernameSignup.requestFocus();
        } else if (password.equals("")) {
            //DialogUtil.getErrorDialog(R.string.password_error, this).show();
            passwordSignup.setError(getResources().getText(R.string.password_error));
            passwordSignup.requestFocus();
        } else if (email.equals("")) {
            //DialogUtil.getErrorDialog(R.string.no_email_error, this).show();
            emailSignup.setError(getResources().getText(R.string.no_email_error));
            emailSignup.requestFocus();
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            //DialogUtil.getErrorDialog(R.string.invalid_email_error, this).show();
            emailSignup.setError(getResources().getText(R.string.invalid_email_error));
            emailSignup.requestFocus();
        } else if (!password.equals(repeatPassword)) {
            //DialogUtil.getErrorDialog(R.string.password_mismatch, this).show();
            repeatPasswordSignup.setError(getResources().getText(R.string.password_mismatch));
            repeatPasswordSignup.requestFocus();
        } else {
            /*
            LoginProvider provider = LoginProviderFactory.getInstanceFor(LoginProviderId.CUSTOM);
            SmartUser newUser = populateCustomUserWithUserName(username, email, password);
            if (provider.signUp(newUser, this)){
                mProcessingLoginFor = LoginProviderId.GOOGLE;
            } else {

            }
            */

        }

    }

    private void doCustomSignin() {
        String username = usernameEditText.getText().toString();
        String password = passwordEditText.getText().toString();
        LoginProvider provider = LoginProviderFactory.getInstanceFor(LoginProviderId.CUSTOM);
        if (username.equals("")) {

            if (provider.loginType() == LoginProvider.LoginType.Username) {
                usernameEditText.setError(getResources().getText(R.string.username_error));
            } else {
                usernameEditText.setError(getResources().getText(R.string.email_error));
            }
            usernameEditText.requestFocus();
        } else if (password.equals("")) {
            passwordEditText.setError(getResources().getText(R.string.password_error));
            passwordEditText.requestFocus();
        } else {
            /*
            SmartUser user;
            if (provider.loginType() == LoginProvider.LoginType.Username) {
                user = populateCustomUserWithUserName(username, null, password);
            } else {
                user = populateCustomUserWithEmail(null, username, password);
            }


            provider.signIn(user, this);
            */
        }
    }


    @Override
    public void loginSuccessful(SmartUser user) {
        if (user != null) {
            UserSessionManager sessionManager = new UserSessionManager();
            if (sessionManager.updateUserSession(this, user)) {
                Intent intent = new Intent();
                intent.putExtra(USER_SESSION, user);
                setResult(SIGNIN_SUCCESSFUL, intent);
                finish();
            } else {
                DialogUtil.getErrorDialog(R.string.network_error, this);
            }
        } else {
            DialogUtil.getErrorDialog(R.string.login_failed, this);
        }
        mProcessingLoginFor = null;
    }

    @Override
    public void loginFailed(){
        mProcessingLoginFor = null;
        DialogUtil.getErrorDialog(R.string.login_failed, this);
    }

    @Override
    public void loggedOut() {
        mProcessingLoginFor = null;
        UserSessionManager.logout(this);
    }
}
