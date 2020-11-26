package net.olikester.shazam2discogs.model;

import java.util.Objects;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

@Entity
public class TagReleaseMatch {
    @Id
    private int id;
    private String sessionId;
    @ManyToOne(cascade = CascadeType.ALL)
    private Tag tag;
    @ManyToOne(cascade = CascadeType.ALL)
    private Release release;

    /**
     * @return the id
     */
    public int getId() {
	return id;
    }

    /**
     * @param Generate ID using session & tag IDs.
     */
    public void generateId() {
	this.id = sessionId.hashCode() + tag.getId().hashCode();
    }

    /**
     * @param id the id to set
     */
    public void setId(int id) {
	this.id = id;
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
     * @return the tag
     */
    public Tag getTag() {
	return tag;
    }

    /**
     * @param tag the tag to set
     */
    public void setTag(Tag tag) {
	this.tag = tag;
    }

    /**
     * @return the release
     */
    public Release getRelease() {
	return release;
    }

    /**
     * @param release the release to set
     */
    public void setRelease(Release release) {
	this.release = release;
    }

    @Override
    public String toString() {
	return "TagReleaseMatch [sessionId=" + sessionId + ", tag=" + tag + ", release=" + release + "]";
    }

    @Override
    public int hashCode() {
	return Objects.hash(release, sessionId, tag);
    }

    @Override
    public boolean equals(Object obj) {
	if (this == obj)
	    return true;
	if (!(obj instanceof TagReleaseMatch))
	    return false;
	TagReleaseMatch other = (TagReleaseMatch) obj;
	return Objects.equals(release, other.release) && Objects.equals(sessionId, other.sessionId)
		&& Objects.equals(tag, other.tag);
    }

}
