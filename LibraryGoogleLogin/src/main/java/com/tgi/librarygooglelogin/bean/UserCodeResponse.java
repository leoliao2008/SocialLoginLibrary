package com.tgi.librarygooglelogin.bean;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * https://developers.google.com/identity/sign-in/devices
 */
public class UserCodeResponse implements Parcelable {
    /**
     * device_code : 4/4-GMMhmHCXhWEzkobqIHGG_EnNYYsAkukHspeYUk9E8
     * user_code : GQVQ-JKEC
     * verification_url : https://www.google.com/device
     * expires_in : 1800
     * interval : 5
     * error : invalid_client
     * error_description : Invalid client type.
     */

    private String device_code;
    private String user_code;
    private String verification_url;
    private int expires_in;
    private int interval;
    private String error;
    private String error_description;

    public UserCodeResponse() {
    }

    protected UserCodeResponse(Parcel in) {
        device_code = in.readString();
        user_code = in.readString();
        verification_url = in.readString();
        expires_in = in.readInt();
        interval = in.readInt();
        error = in.readString();
        error_description = in.readString();
    }

    public static final Creator<UserCodeResponse> CREATOR = new Creator<UserCodeResponse>() {
        @Override
        public UserCodeResponse createFromParcel(Parcel in) {
            return new UserCodeResponse(in);
        }

        @Override
        public UserCodeResponse[] newArray(int size) {
            return new UserCodeResponse[size];
        }
    };

    public String getDevice_code() {
        return device_code;
    }

    public void setDevice_code(String device_code) {
        this.device_code = device_code;
    }

    public String getUser_code() {
        return user_code;
    }

    public void setUser_code(String user_code) {
        this.user_code = user_code;
    }

    public String getVerification_url() {
        return verification_url;
    }

    public void setVerification_url(String verification_url) {
        this.verification_url = verification_url;
    }

    public int getExpires_in() {
        return expires_in;
    }

    public void setExpires_in(int expires_in) {
        this.expires_in = expires_in;
    }

    public int getInterval() {
        return interval;
    }

    public void setInterval(int interval) {
        this.interval = interval;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public String getError_description() {
        return error_description;
    }

    public void setError_description(String error_description) {
        this.error_description = error_description;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(device_code);
        dest.writeString(user_code);
        dest.writeString(verification_url);
        dest.writeInt(expires_in);
        dest.writeInt(interval);
        dest.writeString(error);
        dest.writeString(error_description);
    }
}
