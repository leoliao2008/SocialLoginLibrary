package com.tig.libraysociallogins.google.beans;

/**
 * https://developers.google.com/identity/sign-in/devices
 */
public class GoogleTokens extends GoogleBaseResponse {

    /**
     * error : authorization_pending
     * access_token : ya29.AHES6ZSuY8f6WFLswSv0HZLP2J4cCvFSj-8GiZM0Pr6cgXU
     * token_type : Bearer
     * expires_in : 3600
     * refresh_token : 1/551G1yXUqgkDGnkfFk6ZbjMMMDIMxo3JFc8lY8CAR-Q
     * id_token : eyJhbGciOiJSUzI...
     */

    private String access_token;
    private String token_type;
    private int expires_in;
    private String refresh_token;
    private String id_token;


    public String getAccess_token() {
        return access_token;
    }

    public void setAccess_token(String access_token) {
        this.access_token = access_token;
    }

    public String getToken_type() {
        return token_type;
    }

    public void setToken_type(String token_type) {
        this.token_type = token_type;
    }

    public int getExpires_in() {
        return expires_in;
    }

    public void setExpires_in(int expires_in) {
        this.expires_in = expires_in;
    }

    public String getRefresh_token() {
        return refresh_token;
    }

    public void setRefresh_token(String refresh_token) {
        this.refresh_token = refresh_token;
    }

    public String getId_token() {
        return id_token;
    }

    public void setId_token(String id_token) {
        this.id_token = id_token;
    }

    @Override
    public String toString() {
        return "TokenResponse{" +
                "access_token='" + access_token + '\'' +
                ", token_type='" + token_type + '\'' +
                ", expires_in=" + expires_in +
                ", refresh_token='" + refresh_token + '\'' +
                ", id_token='" + id_token + '\'' +
                "} " + super.toString();
    }
}
