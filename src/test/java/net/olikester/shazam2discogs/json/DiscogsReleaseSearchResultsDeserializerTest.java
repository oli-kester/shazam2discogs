package net.olikester.shazam2discogs.json;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;

import net.olikester.shazam2discogs.model.Release;

@SpringBootTest
public class DiscogsReleaseSearchResultsDeserializerTest {

    private static String exampleResults1 = "";
    private static String exampleRelease1 = "";
    private static ObjectMapper mapper;

    @BeforeAll
    public static void setup() throws IOException {
	exampleResults1 = Files.readString(Path.of("src/test/resources/release-search-1.json"));
	exampleRelease1 = Files.readString(Path.of("src/test/resources/one-release-test.json"));
    }

    @BeforeEach
    public void setupEach() {
	mapper = new ObjectMapper();
	SimpleModule module = new SimpleModule("DiscogsReleaseSearchResultsDeserializer",
		new Version(1, 0, 0, null, null, null));
	module.addDeserializer(ArrayList.class, new DiscogsReleaseSearchResultsDeserializer());
	mapper.registerModule(module);
    }

    @DisplayName("Check a single search result gets parsed correctly.")
    @Test
    public void oneReleaseParseTest() throws JsonProcessingException {
	ArrayList<Release> releases = mapper.readValue(exampleRelease1, new TypeReference<ArrayList<Release>>() {
	});

	Release release = releases.get(0);

	assertNotNull(release);
	assertEquals("UK", release.getCountry());
	assertEquals(2020, release.getReleaseYear());
	assertEquals("FLAC", release.getFormatDesc());
	assertEquals("File", release.getFormatType());
	assertEquals("16093568", release.getId());
	assertEquals("Warp Records", release.getLabel());
	assertEquals(
		"https://img.discogs.com/S5sirpn8_XJ7uD0LFkipF4XaDI4=/fit-in/150x150/filters:strip_icc():format(jpeg):mode_rgb():quality(40)/discogs-images/R-16093568-1603306296-6325.jpeg.jpg",
		release.getThumbnailPath());
	assertEquals("Autechre - SIGN", release.getTitle());
	assertEquals(8, release.getPopularity());
    }

    @DisplayName("Check multiple results get parsed without exception")
    @Test
    public void multipleReleaseParseSimpleTest() throws JsonMappingException, JsonProcessingException {
	ArrayList<Release> releases = mapper.readValue(exampleResults1, new TypeReference<ArrayList<Release>>() {
	});

	assertEquals(6, releases.size());

	for (Release release : releases) {
	    assertEquals("Autechre - SIGN", release.getTitle());
	}
    }

}
