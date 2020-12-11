package com.olikester.shazam2discogs.model;

import java.util.Objects;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

@Entity
public class TagReleaseMatch {
    @Id
    private int id;
    private String sessionId;
    @ManyToOne(cascade = { CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH })
    private Tag tag;
    @ManyToOne(cascade = { CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH })
    private Release release;
    @Enumerated(EnumType.STRING)
    private DiscogsAdditionStatus discogsAdditionStatus = DiscogsAdditionStatus.PENDING;

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

    public DiscogsAdditionStatus getDiscogsAdditionStatus() {
	return discogsAdditionStatus;
    }

    public void setDiscogsAdditionStatus(DiscogsAdditionStatus discogsAdditionStatus) {
	this.discogsAdditionStatus = discogsAdditionStatus;
    }

    @Override
    public int hashCode() {
	return Objects.hash(discogsAdditionStatus, id, release, sessionId, tag);
    }

    @Override
    public boolean equals(Object obj) {
	if (this == obj)
	    return true;
	if (!(obj instanceof TagReleaseMatch))
	    return false;
	TagReleaseMatch other = (TagReleaseMatch) obj;
	return discogsAdditionStatus == other.discogsAdditionStatus && id == other.id
		&& Objects.equals(release, other.release) && Objects.equals(sessionId, other.sessionId)
		&& Objects.equals(tag, other.tag);
    }

    @Override
    public String toString() {
	return "TagReleaseMatch [id=" + id + ", sessionId=" + sessionId + ", tag=" + tag + ", release=" + release
		+ ", discogsAdditionStatus=" + discogsAdditionStatus + "]";
    }

}
