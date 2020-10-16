package net.olikester.shazam2discogs.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class Tag {
    @Id
    @JsonProperty ( "key" )
    private String tagKey;
    @JsonProperty ( "title" )
    private String trackTitle;
    @JsonProperty ( "subtitle" )
    private String artist;
    private Release release;

    public Tag () {
    }

    public Tag (String tagKey, String trackTitle, String artist) {
        this.tagKey = tagKey;
        this.trackTitle = trackTitle;
        this.artist = artist;
    }

    public Tag (String tagKey, String trackTitle, String artist, Release release) {
        this.tagKey = tagKey;
        this.trackTitle = trackTitle;
        this.artist = artist;
        this.release = release;
    }

    public String getTagKey () {
        return tagKey;
    }

    public void setTagKey (String tagKey) {
        this.tagKey = tagKey;
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
                "key='" + tagKey + '\'' +
                ", title='" + trackTitle + '\'' +
                ", artist='" + artist + '\'' +
                '}';
    }
}
