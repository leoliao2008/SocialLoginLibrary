package com.tig.libraysociallogins.amazon.bean;

public class AmazonAccessToken extends AmazonBaseResponse {

    /**
     * access_token : Atza|IQEBLjAsAhRmHjNgHpi0U-Dme37rR6CuUpSR...
     * token_type : bearer
     * expires_in : 3600
     * refresh_token : Atzr|IQEBLzAtAhRPpMJxdwVz2Nn6f2y-tpJX2DeX...
     */

    /**
     * The access token for the user account. Maximum size of 2048 bytes.
     */
    private String access_token;
    /**
     * The type of token returned. Should be bearer
     */
    private String token_type;
    /**
     * The number of seconds before the access token becomes invalid.
     */
    private int expires_in;
    /**
     * A refresh token that can be used to request a new access token. Maximum size of 2048 bytes.
     */
    private String refresh_token;


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

    @Override
    public String toString() {
        return "AmazonAccessToken{" +
                "access_token='" + access_token + '\'' +
                ", token_type='" + token_type + '\'' +
                ", expires_in=" + expires_in +
                ", refresh_token='" + refresh_token + '\'' +
                "} " + super.toString();
    }
}
