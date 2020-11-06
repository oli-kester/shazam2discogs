package net.olikester.shazam2discogs.service;

import java.util.ArrayList;

import org.springframework.security.oauth.consumer.OAuthConsumerToken;

import net.olikester.shazam2discogs.model.JpaOAuthConsumerToken;
import net.olikester.shazam2discogs.model.Release;
import net.olikester.shazam2discogs.model.Tag;

@SuppressWarnings("deprecation")
public interface DiscogsService {

    public final String APP_ID = "discogs";
    public final String REQUEST_TOKEN_URL = "https://api.discogs.com/oauth/request_token";
    public final String AUTHORIZATION_URL = "https://www.discogs.com/oauth/authorize";
    public final String ACCESS_TOKEN_URL = "https://api.discogs.com/oauth/access_token";
    public final String USER_AGENT = "Shazam2Discogs/0.1 +http://oli-kester.net";
    public final String DISCOGS_SEARCH_URL = "https://api.discogs.com/database/search";

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
	return uriString.substring(0, queryStartIndex) + params.replace("?", "%3F");
    }

}
