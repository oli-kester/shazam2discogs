package com.olikester.shazam2discogs.model;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.OneToMany;

@Entity
public class Tag {

    // shared CSV / JSON input data
    @Id
    private String id;
    private String trackTitle;
    private String artist;
    @OneToMany(mappedBy = "tag", fetch = FetchType.EAGER) // TODO remove EAGER for efficiency & solve subsequent
							  // Hibernate errors
    private Set<TagReleaseMatch> matches;

    // JSON-only data
    private String album;
    private String label;
    private int releaseYear;
    private String imageUrl;

    // CSV-only data
    private Date tagTime;
    private String shazamInfoUrl;

    /**
     * Default constructor to allow Jackson to use the class
     */
    public Tag() {
	matches = new HashSet<>();
    }

    public Tag(String id, String trackTitle, String artist) {
	this.id = id;
	this.trackTitle = trackTitle;
	this.artist = artist;
    }

    public Tag(String id, String trackTitle, String artist, String album, String label, int releaseYear,
	    String imageUrl) {
	this.id = id;
	this.trackTitle = trackTitle;
	this.artist = artist;
	this.album = album;
	this.label = label;
	this.releaseYear = releaseYear;
	this.imageUrl = imageUrl;
    }

    /**
     * Creates a simple search string for this tag.
     * 
     * @return
     */
    public String getSimpleSearchTerm() {
	return trackTitle + " " + artist;
    }

    public String getId() {
	return id;
    }

    public void setId(String id) {
	this.id = id;
    }

    public String getTrackTitle() {
	return trackTitle;
    }

    public void setTrackTitle(String trackTitle) {
	this.trackTitle = trackTitle;
    }

    public String getArtist() {
	return artist;
    }

    public void setArtist(String artist) {
	this.artist = artist;
    }

    /**
     * @return the album
     */
    public String getAlbum() {
	return album;
    }

    /**
     * @param album the album to set
     */
    public void setAlbum(String album) {
	this.album = album;
    }

    /**
     * @return the label
     */
    public String getLabel() {
	return label;
    }

    /**
     * @param label the label to set
     */
    public void setLabel(String label) {
	this.label = label;
    }

    /**
     * @return the releaseYear
     */
    public int getReleaseYear() {
	return releaseYear;
    }

    /**
     * @param releaseYear the releaseYear to set
     */
    public void setReleaseYear(int releaseYear) {
	this.releaseYear = releaseYear;
    }

    /**
     * @return the imageUrl
     */
    public String getImageUrl() {
	return imageUrl;
    }

    /**
     * @param imageUrl the imageUrl to set
     */
    public void setImageUrl(String imageUrl) {
	this.imageUrl = imageUrl;
    }

    public void addMatch(TagReleaseMatch newMatch) {
	matches.add(newMatch);
    }

    /**
     * @return the matches
     */
    public Set<TagReleaseMatch> getMatches() {
	return matches;
    }

    /**
     * @param matches the matches to set
     */
    public void setMatches(Set<TagReleaseMatch> matches) {
	this.matches = matches;
    }

    /**
     * @return the tagTime
     */
    public Date getTagTime() {
	return tagTime;
    }

    /**
     * @param tagTime the tagTime to set
     */
    public void setTagTime(Date tagTime) {
	this.tagTime = tagTime;
    }

    /**
     * @return the shazamInfoUrl
     */
    public String getShazamInfoUrl() {
	return shazamInfoUrl;
    }

    /**
     * @param shazamInfoUrl the shazamInfoUrl to set
     */
    public void setShazamInfoUrl(String shazamInfoUrl) {
	this.shazamInfoUrl = shazamInfoUrl;
    }

    @Override
    public String toString() {
	return "Tag [id=" + id + ", trackTitle=" + trackTitle + ", artist=" + artist + ", matches=" + matches
		+ ", album=" + album + ", label=" + label + ", releaseYear=" + releaseYear + ", imageUrl=" + imageUrl
		+ ", tagTime=" + tagTime + ", shazamInfoUrl=" + shazamInfoUrl + "]";
    }

}
