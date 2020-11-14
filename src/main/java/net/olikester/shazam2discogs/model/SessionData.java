package net.olikester.shazam2discogs.model;

import java.util.List;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;

@Entity
public class SessionData {

    @Id
    private String sessionId;
    @OneToMany(mappedBy = "session")
    private List<Tag> tags;

    /**
     * Default constructor to allow Jackson to use the class
     */
    public SessionData() {

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
     * @return the tags
     */
    public List<Tag> getTags() {
	return tags;
    }

    /**
     * @param tags the tags to set
     */
    public void setTags(List<Tag> tags) {
	this.tags = tags;
    }

    @Override
    public String toString() {
	return "SessionData [sessionId=" + sessionId + ", tags=" + tags + "]";
    }

}
