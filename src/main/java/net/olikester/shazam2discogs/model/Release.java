package net.olikester.shazam2discogs.model;

import javax.persistence.Entity;
import javax.persistence.Id;

/**
 * Represents the required data from a Release in the Discogs Database
 * 
 * @author Oliver Reynolds
 *
 */
@Entity
public class Release {
    @Id
    private String id;
    private String title;
    private String country;
    private int releaseYear;
    private String formatType;
    private String formatDesc;
    private String label;
    private String thumbnailPath;
    private int popularity; // the number of users that have this in their collection.

    /**
     * Default constructor to allow Jackson to use the class
     */
    public Release() {

    }

    /**
     * @return the id
     */
    public String getId() {
	return id;
    }

    /**
     * @param id the id to set
     */
    public void setId(String id) {
	this.id = id;
    }

    /**
     * @return the title
     */
    public String getTitle() {
	return title;
    }

    /**
     * @param title the title to set
     */
    public void setTitle(String title) {
	this.title = title;
    }

    /**
     * @return the country
     */
    public String getCountry() {
	return country;
    }

    /**
     * @param country the country to set
     */
    public void setCountry(String country) {
	this.country = country;
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
     * @return the formatType
     */
    public String getFormatType() {
	return formatType;
    }

    /**
     * @param formatType the formatType to set
     */
    public void setFormatType(String formatType) {
	this.formatType = formatType;
    }

    /**
     * @return the formatDesc
     */
    public String getFormatDesc() {
	return formatDesc;
    }

    /**
     * @param formatDesc the formatDesc to set
     */
    public void setFormatDesc(String formatDesc) {
	this.formatDesc = formatDesc;
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
     * @return the thumbnailPath
     */
    public String getThumbnailPath() {
	return thumbnailPath;
    }

    /**
     * @param thumbnailPath the thumbnailPath to set
     */
    public void setThumbnailPath(String thumbnailPath) {
	this.thumbnailPath = thumbnailPath;
    }

    public int getPopularity() {
	return popularity;
    }

    public void setPopularity(int popularity) {
	this.popularity = popularity;
    }

    @Override
    public String toString() {
	return "Release [id=" + id + ", title=" + title + ", country=" + country + ", releaseYear=" + releaseYear
		+ ", formatType=" + formatType + ", formatDesc=" + formatDesc + ", label=" + label + ", thumbnailPath="
		+ thumbnailPath + ", popularity=" + popularity + "]";
    }

}
