package net.olikester.shazam2discogs.model;


/**
 * Reads footnote section of Shazam tag
 */
public class Release {
    private int id; //TODO make this work
    private String album;
    private String label;
    private String releaseYear;

    /**
     * Default constructor to allow Jackson to use the class
     */
    public Release () {
    }

    public Release (String album, String label, String releaseYear) {
        this.album = album;
        this.label = label;
        this.releaseYear = releaseYear;
    }

    public int getId () {
        return id;
    }

    public void setId (int id) {
        this.id = id;
    }

    public String getAlbum () {
        return album;
    }

    public void setAlbum (String album) {
        this.album = album;
    }

    public String getLabel () {
        return label;
    }

    public void setLabel (String label) {
        this.label = label;
    }

    public String getReleaseYear () {
        return releaseYear;
    }

    public void setReleaseYear (String releaseYear) {
        this.releaseYear = releaseYear;
    }

    @Override
    public String toString () {
        return "Release{" +
                "id=" + id +
                ", album='" + album + '\'' +
                ", label='" + label + '\'' +
                ", releaseYear='" + releaseYear + '\'' +
                '}';
    }
}
