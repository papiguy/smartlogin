package studios.codelight.smartloginlibrary.session;

import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;

import studios.codelight.smartloginlibrary.providers.LoginProviderId;

/**
 * Copyright (c) 2016 Codelight Studios
 * Created by Kalyan on 9/23/2015.
 */
public class SmartUser implements Parcelable {

    public static final Creator<SmartUser> CREATOR = new Creator<SmartUser>() {
        @Override
        public SmartUser createFromParcel(Parcel in) {
            return new SmartUser(in);
        }

        @Override
        public SmartUser[] newArray(int size) {
            return new SmartUser[size];
        }
    };
    private LoginProviderId providerId;
    private String userId;
    private String username;
    private String password;
    private String firstName;
    private String middleName;
    private String lastName;
    private String email;
    private String birthday;
    private int gender;
    private String profileLink;
    private String displayName;
    private String photoUrl;

    public SmartUser() {
    }

    protected SmartUser(Parcel in) {
        providerId = LoginProviderId.from(in.readInt());
        userId = in.readString();
        username = in.readString();
        password = in.readString();
        firstName = in.readString();
        middleName = in.readString();
        lastName = in.readString();
        email = in.readString();
        birthday = in.readString();
        gender = in.readInt();
        profileLink = in.readString();
        displayName = in.readString();
        photoUrl = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(providerId.toInt());
        dest.writeString(userId);
        dest.writeString(username);
        dest.writeString(password);
        dest.writeString(firstName);
        dest.writeString(middleName);
        dest.writeString(lastName);
        dest.writeString(email);
        dest.writeString(birthday);
        dest.writeInt(gender);
        dest.writeString(profileLink);
        dest.writeString(displayName);
        dest.writeString(photoUrl);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getMiddleName() {
        return middleName;
    }

    public void setMiddleName(String middleName) {
        this.middleName = middleName;
    }

    public String getBirthday() {
        return birthday;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    public int getGender() {
        return gender;
    }

    public void setGender(int gender) {
        this.gender = gender;
    }

    public String getProfileLink() {
        return profileLink;
    }

    public void setProfileLink(String profileLink) {
        this.profileLink = profileLink;
    }

    public LoginProviderId getProviderId() {
        return providerId;
    }

    public void setProviderId(LoginProviderId providerId) {
        this.providerId = providerId;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }

    public enum Gender {
        male, female
    }
}
