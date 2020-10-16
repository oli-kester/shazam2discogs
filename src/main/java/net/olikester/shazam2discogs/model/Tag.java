package net.olikester.shazam2discogs.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class Tag {
    @Id
    private int key;
    @JsonProperty ( "title" )
    private String trackTitle;
    @JsonProperty ( "subtitle" )
    private String artist;
    private Release release;

    /**
     * Default constructor to allow Jackson to use the class
     */
    public Tag () {
    }

    public Tag (int key, String trackTitle, String artist) {
        this.key = key;
        this.trackTitle = trackTitle;
        this.artist = artist;
    }

    public Tag (int key, String trackTitle, String artist, Release release) {
        this.key = key;
        this.trackTitle = trackTitle;
        this.artist = artist;
        this.release = release;
    }

    public int getKey () {
        return key;
    }

    public void setKey (int key) {
        this.key = key;
    }

    public String getTrackTitle () {
        return trackTitle;
    }

    public void setTrackTitle (String trackTitle) {
        this.trackTitle = trackTitle;
    }

    public String getArtist () {
        return artist;
    }

    public void setArtist (String artist) {
        this.artist = artist;
    }

    public Release getRelease () {
        return release;
    }

    public void setRelease (Release release) {
        this.release = release;
    }

    @Override
    public String toString () {
        return "Tag{" +
                "key='" + key + '\'' +
                ", title='" + trackTitle + '\'' +
                ", artist='" + artist + '\'' +
                '}';
    }
}
