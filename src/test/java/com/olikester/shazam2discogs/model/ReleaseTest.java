package com.olikester.shazam2discogs.model;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.olikester.shazam2discogs.json.DiscogsReleaseSearchResultsDeserializer;
import com.olikester.shazam2discogs.model.MediaFormats;
import com.olikester.shazam2discogs.model.Release;

public class ReleaseTest {

    private static ArrayList<Release> releases;

    @BeforeAll
    public static void setup() throws JsonMappingException, JsonProcessingException, IOException {
	ObjectMapper mapper = new ObjectMapper();
	SimpleModule module = new SimpleModule("DiscogsReleaseSearchResultsDeserializer",
		new Version(1, 0, 0, null, null, null));
	module.addDeserializer(ArrayList.class, new DiscogsReleaseSearchResultsDeserializer());
	mapper.registerModule(module);

	releases = mapper.readValue(Files.readString(Path.of("src/test/resources/release-search-1.json")),
		new TypeReference<ArrayList<Release>>() {
		});
    }

    @DisplayName("Simple check that the most popular hi-res release is selected. ")
    @Test
    public void filterReleasesByFormatHiresDigital1() {
	Release result = Release.selectPreferredReleaseByFormat(releases, MediaFormats.DIGITAL_HI_RES);
	assertEquals("16031874", result.getId());
    }


    @DisplayName("Simple check that the most popular CD release is selected. ")
    @Test
    public void filterReleasesByFormatCD1() {
	Release result = Release.selectPreferredReleaseByFormat(releases, MediaFormats.PHYSICAL_CD);
	assertEquals("16063776", result.getId());
    }

    @DisplayName("Simple check that the most popular Vinyl release is selected. ")
    @Test
    public void filterReleasesByFormatVinyl1() {
	Release result = Release.selectPreferredReleaseByFormat(releases, MediaFormats.PHYSICAL_VINYL);
	assertEquals("16061701", result.getId());
    }

    @DisplayName("Simple check that the most popular MP3 release is selected. ")
    @Test
    public void filterReleasesByFormatMP31() {
	Release result = Release.selectPreferredReleaseByFormat(releases, MediaFormats.DIGITAL_MP3);
	assertEquals("16041221", result.getId());
    }

}
