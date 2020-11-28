package net.olikester.shazam2discogs.model;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;

@Entity
public class Tag {
    @Id
    private String id;
    private String trackTitle;
    private String artist;
    private String album;
    private String label;
    private int releaseYear;
    private String imageUrl;
    @OneToMany(mappedBy = "tag")
    private Set<TagReleaseMatch> matches;

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
}
