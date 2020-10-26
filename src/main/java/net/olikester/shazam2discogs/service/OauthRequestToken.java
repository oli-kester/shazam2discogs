package net.olikester.shazam2discogs.service;

import java.text.ParseException;

public class OauthRequestToken {

    private final String TOKEN_REQUEST_LABEL = "oauth_token=";
    private final String SECRET_REQUEST_LABEL = "oauth_token_secret=";

    // TODO put an ID here & start thinking about sessions
    private String token;
    private String secret;

    /**
     * Create a Request Token by parsing the values out of a response body string.
     * 
     * @param responseBody The full response body string.
     * @throws ParseException 
     */
    public OauthRequestToken(String responseBody) throws ParseException {
	int tokenIndex = responseBody.indexOf(TOKEN_REQUEST_LABEL);
	int secretIndex = responseBody.indexOf(SECRET_REQUEST_LABEL);

	if (tokenIndex < 0 || secretIndex < 0) {
	    throw new ParseException("Token Request Response - Invalid format ", 0);
	}

	this.token = responseBody.substring(tokenIndex + TOKEN_REQUEST_LABEL.length(), secretIndex - 1);
	this.secret = responseBody.substring(secretIndex + SECRET_REQUEST_LABEL.length());
    }

    /**
     * Create a Request Token from token & secret strings.
     * 
     * @param token  - The request token string (no ampersand).
     * @param secret - The request token secret string.
     */
    public OauthRequestToken(String token, String secret) {
	super();
	this.token = token;
	this.secret = secret;
    }

    /**
     * @return the token
     */
    public String getToken() {
	return token;
    }

    /**
     * @param token the token to set
     */
    public void setToken(String token) {
	this.token = token;
    }

    /**
     * @return the secret
     */
    public String getSecret() {
	return secret;
    }

    /**
     * @param secret the secret to set
     */
    public void setSecret(String secret) {
	this.secret = secret;
    }

    @Override
    public String toString() {
	return "OauthRequestToken [token=" + token + ", secret=" + secret + "]";
    }

}
