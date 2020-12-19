package com.olikester.shazam2discogs.service;

import com.olikester.shazam2discogs.model.Tag;

public interface ShazamService {
    public final String shazamTagDataRequestUrl = "https://www.shazam.com/discovery/v5/en/GB/web/-/track/";

    /**
     * Fill out missing fields of the Tag data by sending requests to the Shazam
     * service.
     * 
     * @param tag The tag with missing data.
     * @return The updated Tag.
     */
    Tag fetchExtraTagData(Tag tag);
}
