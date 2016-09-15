package studios.codelight.smartloginlibrary.providers;

/**
 * Created by nitin on 9/9/16.
 */
public enum LoginProviderId {
    FACEBOOK(0),
    LINKEDIN(1),
    GOOGLE(2),
    CUSTOM(3);

    int ordinal;

    LoginProviderId(int intVal) {
        this.ordinal = intVal;
    }

    public int toInt(){
        return ordinal;
    }

    public static LoginProviderId from(int intVal){
        switch (intVal){
            case 0:
                return FACEBOOK;
            case 1:
                return LINKEDIN;
            case 2:
                return GOOGLE;
        }
        return CUSTOM;
    }
}
