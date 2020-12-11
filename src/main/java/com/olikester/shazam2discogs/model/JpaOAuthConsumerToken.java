package com.olikester.shazam2discogs.model;

import java.util.Map;

import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.MapKeyColumn;

import org.springframework.security.oauth.consumer.OAuthConsumerToken;

/**
 * Custom implementation of the OAuth1 library Consumer Token class to allow
 * storage as a JPA entity.
 * 
 * @author oliver
 * 
 */
@SuppressWarnings("deprecation")
@Entity
public class JpaOAuthConsumerToken {
    @Id
    private String httpSessionId;
    private String username;
    private String oauthResourceId;
    private String oauthToken;
    private String oauthSecret;
    private boolean isAccessToken;
    @ElementCollection
    @MapKeyColumn(name = "PARAM_KEY")
    @Column(name = "PARAM_VALUE")
    private Map<String, String> additionalParameters;

    @SuppressWarnings("unused")
    private static final long serialVersionUID = -3314099155713269220L;

    public JpaOAuthConsumerToken() {
    }

    /**
     * Turn a plain OAuthConsumerToken into a JpaOAuthConsumerToken
     * 
     * @param httpSessionId
     * @param username
     * @param oauthToken
     */
    public JpaOAuthConsumerToken(String httpSessionId, OAuthConsumerToken oauthToken) {
	this.httpSessionId = httpSessionId;
	this.oauthResourceId = oauthToken.getResourceId();
	this.oauthToken = oauthToken.getValue();
	this.oauthSecret = oauthToken.getSecret();
	this.isAccessToken = oauthToken.isAccessToken();
	this.additionalParameters = oauthToken.getAdditionalParameters();
    }

    public OAuthConsumerToken toOAuthConsumerToken() {
	OAuthConsumerToken returnToken = new OAuthConsumerToken();
	returnToken.setResourceId(getOauthResourceId());
	returnToken.setValue(getOauthToken());
	returnToken.setSecret(getOauthSecret());
	returnToken.setAccessToken(isAccessToken());
	returnToken.setAdditionalParameters(getAdditionalParameters());
	return returnToken;
    }

    /**
     * @return the httpSessionId
     */
    public String getHttpSessionId() {
	return httpSessionId;
    }

    /**
     * @param httpSessionId the httpSessionId to set
     */
    public void setHttpSessionId(String httpSessionId) {
	this.httpSessionId = httpSessionId;
    }

    /**
     * @return the oauthResourceId
     */
    public String getOauthResourceId() {
	return oauthResourceId;
    }

    /**
     * @param oauthResourceId the oauthResourceId to set
     */
    public void setOauthResourceId(String oauthResourceId) {
	this.oauthResourceId = oauthResourceId;
    }

    /**
     * @return the oauthToken
     */
    public String getOauthToken() {
	return oauthToken;
    }

    /**
     * @param oauthToken the oauthToken to set
     */
    public void setOauthToken(String oauthToken) {
	this.oauthToken = oauthToken;
    }

    /**
     * @return the oauthSecret
     */
    public String getOauthSecret() {
	return oauthSecret;
    }

    /**
     * @param oauthSecret the oauthSecret to set
     */
    public void setOauthSecret(String oauthSecret) {
	this.oauthSecret = oauthSecret;
    }

    /**
     * @return the isAccessToken
     */
    public boolean isAccessToken() {
	return isAccessToken;
    }

    /**
     * @param isAccessToken the isAccessToken to set
     */
    public void setAccessToken(boolean isAccessToken) {
	this.isAccessToken = isAccessToken;
    }

    /**
     * @return the additionalParameters
     */
    public Map<String, String> getAdditionalParameters() {
	return additionalParameters;
    }

    /**
     * @param additionalParameters the additionalParameters to set
     */
    public void setAdditionalParameters(Map<String, String> additionalParameters) {
	this.additionalParameters = additionalParameters;
    }

    /**
     * @return the username
     */
    public String getUsername() {
	return username;
    }

    /**
     * @param username the username to set
     */
    public void setUsername(String username) {
	this.username = username;
    }

    @Override
    public String toString() {
	return "JpaOAuthConsumerToken [httpSessionId=" + httpSessionId + ", username=" + username + ", oauthResourceId="
		+ oauthResourceId + ", oauthToken=" + oauthToken + ", oauthSecret=" + oauthSecret + ", isAccessToken="
		+ isAccessToken + ", additionalParameters=" + additionalParameters + "]";
    }

}
