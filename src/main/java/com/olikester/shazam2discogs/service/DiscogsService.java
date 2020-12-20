package com.olikester.shazam2discogs.service;

import java.util.ArrayList;

import org.springframework.security.oauth.consumer.OAuthConsumerToken;

import com.olikester.shazam2discogs.model.JpaOAuthConsumerToken;
import com.olikester.shazam2discogs.model.Release;
import com.olikester.shazam2discogs.model.Tag;

@SuppressWarnings("deprecation")
public interface DiscogsService {

    public final String APP_ID = "discogs";
    public final String REQUEST_TOKEN_URL = "https://api.discogs.com/oauth/request_token";
    public final String AUTHORIZATION_URL = "https://www.discogs.com/oauth/authorize";
    public final String ACCESS_TOKEN_URL = "https://api.discogs.com/oauth/access_token";
    public final String IDENTITY_CHECK_URL = "https://api.discogs.com/oauth/identity";
    public final String USER_AGENT = "Shazam2Discogs/1.2.2 +http://oli-kester.net";
    public final String DISCOGS_SEARCH_URL = "https://api.discogs.com/database/search";
    public final String DISCOGS_USERS_URL = "https://api.discogs.com/users";

    OAuthConsumerToken fetchRequestToken(String callbackURL);

    OAuthConsumerToken fetchAccessToken(OAuthConsumerToken oAuthConsumerToken, String oAuthVerifier);

    /**
     * Returns a list of Releases matching the given search parameters.
     * 
     * @param currTag
     * @param accessToken
     * @return
     */
    ArrayList<Release> getReleaseList(Tag currTag, JpaOAuthConsumerToken accessToken);

    /**
     * A less precise search for use when we don't get any results with
     * getReleaseList(Tag currTag, JpaOAuthConsumerToken accessToken);
     * 
     * @param currTag
     * @param accessToken
     * @return
     */
    ArrayList<Release> getReleaseList(String searchString, JpaOAuthConsumerToken accessToken);

    /**
     * Adds the given release to the authenticated user's wantlist. Returns
     * success/failure status.
     * 
     * @param release
     * @param accessToken
     * @return
     */
    boolean addReleaseToUserWantlist(Release release, JpaOAuthConsumerToken accessToken);

    /**
     * For test purposes - create an access token using the keys in the hidden
     * properties file.
     * 
     * @param sessionId - The session ID to bind the access token to
     * @return - A JPA-compliant access token.
     */
    JpaOAuthConsumerToken createTestAccessToken(String sessionId);

    /**
     * Strips illegal characters from the query string and returns the corrected
     * version.
     * 
     * @param uriString
     * @return
     */
    public static String stripIllegalQueryChars(String uriString) {
	int queryStartIndex = uriString.indexOf('?') + 1;
	String params = uriString.substring(queryStartIndex);
	return uriString.substring(0, queryStartIndex) + params.replace("?", "%3F").replace(";", "%3B");
    }

    /**
     * Get the Discogs username that the given access token relates to. 
     * @param accessToken
     * @return
     */
    String getUserName(JpaOAuthConsumerToken accessToken);

}
