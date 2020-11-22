package net.olikester.shazam2discogs.model;

import javax.persistence.Entity;
import javax.persistence.Id;

/**
 * Holds the percentage-based discogs search progress for each sessionID.
 * 
 * @author Oliver Reynolds
 *
 */
@Entity
public class TaskProgress {
    @Id
    private String sessionId;
    private int searchProgress;

    public TaskProgress() {
    }

    public TaskProgress(String sessionId, int searchProgress) {
	super();
	this.sessionId = sessionId;
	this.searchProgress = searchProgress;
    }

    /**
     * @return the sessionId
     */
    public String getSessionId() {
	return sessionId;
    }

    /**
     * @param sessionId the sessionId to set
     */
    public void setSessionId(String sessionId) {
	this.sessionId = sessionId;
    }

    /**
     * @return the searchProgress
     */
    public int getSearchProgress() {
	return searchProgress;
    }

    /**
     * @param searchProgress the searchProgress to set
     */
    public void setSearchProgress(int searchProgress) {
	this.searchProgress = searchProgress;
    }

    @Override
    public String toString() {
	return "DiscogsSearchProgress [sessionId=" + sessionId + ", searchProgress=" + searchProgress + "]";
    }

}
