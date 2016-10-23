package studios.codelight.smartloginlibrary.providers;

/**
 * Created by nitin on 9/9/16.
 */
public enum LoginProviderId {
    FACEBOOK(1),
    LINKEDIN(2),
    GOOGLE(3),
    CUSTOM(4);

    int ordinal;

    LoginProviderId(int intVal) {
        this.ordinal = intVal;
    }

    public int toInt(){
        return ordinal;
    }

    public static LoginProviderId from(int intVal){
        switch (intVal){
            case 1:
                return FACEBOOK;
            case 2:
                return LINKEDIN;
            case 3:
                return GOOGLE;
        }
        return CUSTOM;
    }
}
