package net.olikester.shazam2discogs.model;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

@Entity
public class Tag {
    @Id
    private int key;
    private String trackTitle;
    private String artist;
    private String album;
    private String label;
    private int releaseYear;
    @ManyToOne
    @JoinColumn(name = "sessionId", nullable = false)
    private SessionData session;

    /**
     * Default constructor to allow Jackson to use the class
     */
    public Tag() {
    }

    public Tag(int key, String trackTitle, String artist) {
	this.key = key;
	this.trackTitle = trackTitle;
	this.artist = artist;
    }

    public Tag(int key, String trackTitle, String artist, String album, String label, int releaseYear) {
	this.key = key;
	this.trackTitle = trackTitle;
	this.artist = artist;
	this.album = album;
	this.label = label;
	this.releaseYear = releaseYear;
    }

    public int getKey() {
	return key;
    }

    public void setKey(int key) {
	this.key = key;
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
     * @return the session
     */
    public SessionData getSession() {
	return session;
    }

    /**
     * @param session the session to set
     */
    public void setSession(SessionData session) {
	this.session = session;
    }

    @Override
    public String toString() {
	return "Tag [key=" + key + ", trackTitle=" + trackTitle + ", artist=" + artist + ", album=" + album + ", label="
		+ label + ", releaseYear=" + releaseYear + ", session=" + session + "]";
    }

}
