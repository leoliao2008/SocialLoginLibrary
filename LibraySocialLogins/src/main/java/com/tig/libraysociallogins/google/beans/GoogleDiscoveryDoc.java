package com.tig.libraysociallogins.google.beans;

import com.tig.libraysociallogins.google.beans.GoogleBaseResponse;

import java.util.List;

/**
 * The OpenID Connect protocol requires the use of multiple endpoints for authenticating users,
 * and for requesting resources including tokens, user information, and public keys.
 * To simplify implementations and increase flexibility, OpenID Connect allows the use of a "Discovery document,"
 * a JSON document found at a well-known location containing key-value pairs which provide details about the
 * OpenID Connect provider's configuration, including the URIs of the authorization, token, userinfo,
 * and public-keys endpoints.
 * <a href="https://developers.google.com/identity/protocols/OpenIDConnect#discovery">Reference Page<a/>
 */
public class GoogleDiscoveryDoc extends GoogleBaseResponse {

    /**
     * issuer : https://accounts.google.com
     * authorization_endpoint : https://accounts.google.com/o/oauth2/v2/auth
     * token_endpoint : https://www.googleapis.com/oauth2/v4/token
     * userinfo_endpoint : https://www.googleapis.com/oauth2/v3/userinfo
     * revocation_endpoint : https://accounts.google.com/o/oauth2/revoke
     * jwks_uri : https://www.googleapis.com/oauth2/v3/certs
     * response_types_supported : ["code","token","id_token","code token","code id_token","token id_token","code token id_token","none"]
     * subject_types_supported : ["public"]
     * id_token_signing_alg_values_supported : ["RS256"]
     * scopes_supported : ["openid","email","profile"]
     * token_endpoint_auth_methods_supported : ["client_secret_post","client_secret_basic"]
     * claims_supported : ["aud","email","email_verified","exp","family_name","given_name","iat","iss","locale","name","picture","sub"]
     * code_challenge_methods_supported : ["plain","S256"]
     */

    private String issuer;
    private String authorization_endpoint;
    private String token_endpoint;
    private String userinfo_endpoint;
    private String revocation_endpoint;
    private String jwks_uri;
    private List<String> response_types_supported;
    private List<String> subject_types_supported;
    private List<String> id_token_signing_alg_values_supported;
    private List<String> scopes_supported;
    private List<String> token_endpoint_auth_methods_supported;
    private List<String> claims_supported;
    private List<String> code_challenge_methods_supported;

    public String getIssuer() {
        return issuer;
    }

    public void setIssuer(String issuer) {
        this.issuer = issuer;
    }

    public String getAuthorization_endpoint() {
        return authorization_endpoint;
    }

    public void setAuthorization_endpoint(String authorization_endpoint) {
        this.authorization_endpoint = authorization_endpoint;
    }

    public String getToken_endpoint() {
        return token_endpoint;
    }

    public void setToken_endpoint(String token_endpoint) {
        this.token_endpoint = token_endpoint;
    }

    public String getUserinfo_endpoint() {
        return userinfo_endpoint;
    }

    public void setUserinfo_endpoint(String userinfo_endpoint) {
        this.userinfo_endpoint = userinfo_endpoint;
    }

    public String getRevocation_endpoint() {
        return revocation_endpoint;
    }

    public void setRevocation_endpoint(String revocation_endpoint) {
        this.revocation_endpoint = revocation_endpoint;
    }

    public String getJwks_uri() {
        return jwks_uri;
    }

    public void setJwks_uri(String jwks_uri) {
        this.jwks_uri = jwks_uri;
    }

    public List<String> getResponse_types_supported() {
        return response_types_supported;
    }

    public void setResponse_types_supported(List<String> response_types_supported) {
        this.response_types_supported = response_types_supported;
    }

    public List<String> getSubject_types_supported() {
        return subject_types_supported;
    }

    public void setSubject_types_supported(List<String> subject_types_supported) {
        this.subject_types_supported = subject_types_supported;
    }

    public List<String> getId_token_signing_alg_values_supported() {
        return id_token_signing_alg_values_supported;
    }

    public void setId_token_signing_alg_values_supported(List<String> id_token_signing_alg_values_supported) {
        this.id_token_signing_alg_values_supported = id_token_signing_alg_values_supported;
    }

    public List<String> getScopes_supported() {
        return scopes_supported;
    }

    public void setScopes_supported(List<String> scopes_supported) {
        this.scopes_supported = scopes_supported;
    }

    public List<String> getToken_endpoint_auth_methods_supported() {
        return token_endpoint_auth_methods_supported;
    }

    public void setToken_endpoint_auth_methods_supported(List<String> token_endpoint_auth_methods_supported) {
        this.token_endpoint_auth_methods_supported = token_endpoint_auth_methods_supported;
    }

    public List<String> getClaims_supported() {
        return claims_supported;
    }

    public void setClaims_supported(List<String> claims_supported) {
        this.claims_supported = claims_supported;
    }

    public List<String> getCode_challenge_methods_supported() {
        return code_challenge_methods_supported;
    }

    public void setCode_challenge_methods_supported(List<String> code_challenge_methods_supported) {
        this.code_challenge_methods_supported = code_challenge_methods_supported;
    }

    @Override
    public String toString() {
        return "GoogleDiscoveryDoc{" +
                "issuer='" + issuer + '\'' +
                ", authorization_endpoint='" + authorization_endpoint + '\'' +
                ", token_endpoint='" + token_endpoint + '\'' +
                ", userinfo_endpoint='" + userinfo_endpoint + '\'' +
                ", revocation_endpoint='" + revocation_endpoint + '\'' +
                ", jwks_uri='" + jwks_uri + '\'' +
                ", response_types_supported=" + response_types_supported +
                ", subject_types_supported=" + subject_types_supported +
                ", id_token_signing_alg_values_supported=" + id_token_signing_alg_values_supported +
                ", scopes_supported=" + scopes_supported +
                ", token_endpoint_auth_methods_supported=" + token_endpoint_auth_methods_supported +
                ", claims_supported=" + claims_supported +
                ", code_challenge_methods_supported=" + code_challenge_methods_supported +
                "} " + super.toString();
    }
}
