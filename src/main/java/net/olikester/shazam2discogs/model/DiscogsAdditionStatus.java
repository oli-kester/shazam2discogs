package net.olikester.shazam2discogs.model;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.Id;

/**
 * Represents the statistics of releases added / failed to add to Discogs for
 * the given user ID.
 * 
 * @author Oliver Reynolds
 *
 */
@Entity
public class DiscogsAdditionStatus {
    @Id
    private String sessionId;
    private int numReleasesProcessed;
    private int numReleasesAdded;
    private int numFailedReleases;
    @ElementCollection
    private List<Release> failedReleases;
    
    public DiscogsAdditionStatus() {
	failedReleases = new ArrayList<>();
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
     * @return the numReleasesProcessed
     */
    public int getNumReleasesProcessed() {
	return numReleasesProcessed;
    }

    /**
     * @param numReleasesProcessed the numReleasesProcessed to set
     */
    public void setNumReleasesProcessed(int numReleasesProcessed) {
	this.numReleasesProcessed = numReleasesProcessed;
    }

    /**
     * @return the numReleasesAdded
     */
    public int getNumReleasesAdded() {
	return numReleasesAdded;
    }

    /**
     * @param numReleasesAdded the numReleasesAdded to set
     */
    public void setNumReleasesAdded(int numReleasesAdded) {
	this.numReleasesAdded = numReleasesAdded;
    }

    /**
     * @return the numFailedReleases
     */
    public int getNumFailedReleases() {
	return numFailedReleases;
    }

    /**
     * @param numFailedReleases the numFailedReleases to set
     */
    public void setNumFailedReleases(int numFailedReleases) {
	this.numFailedReleases = numFailedReleases;
    }

    /**
     * @return the failedReleases
     */
    public List<Release> getFailedReleases() {
	return failedReleases;
    }

    /**
     * @param failedReleases the failedReleases to set
     */
    public void setFailedReleases(List<Release> failedReleases) {
	this.failedReleases = failedReleases;
    }

    @Override
    public String toString() {
	return "DiscogsAdditionStatus [sessionId=" + sessionId + ", numReleasesProcessed=" + numReleasesProcessed
		+ ", numReleasesAdded=" + numReleasesAdded + ", numFailedReleases=" + numFailedReleases
		+ ", failedReleases=" + failedReleases + "]";
    }
}
