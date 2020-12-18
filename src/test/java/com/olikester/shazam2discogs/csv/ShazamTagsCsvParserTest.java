package com.olikester.shazam2discogs.csv;

import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.GregorianCalendar;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.olikester.shazam2discogs.model.Tag;

@SuppressWarnings("unused")
class ShazamTagsCsvParserTest {

    private static String exampleTag1;
    private static String exampleTag2;
    private static String largeTagsFile;
    private static String escapeQuoteTest1;
    private static String escapeQuoteTest1expected;
    private static String escapeQuoteTest2;

    @BeforeAll
    static void setUpBeforeClass() throws Exception {
	exampleTag1 = Files.readString(Path.of("src/test/resources/single-valid-tag1.csv"));
	exampleTag2 = Files.readString(Path.of("src/test/resources/single-valid-tag2.csv"));
	largeTagsFile = Files.readString(Path.of("src/test/resources/large-shazamlibrary-dec-2020.csv"));
	escapeQuoteTest1 = Files.readString(Path.of("src/test/resources/escape-quote-test1.csv"));
	escapeQuoteTest1expected = Files.readString(Path.of("src/test/resources/escape-quote-test1-expected.csv"));
	escapeQuoteTest2 = Files.readString(Path.of("src/test/resources/escape-quote-test2.csv"));
    }

    @AfterAll
    static void tearDownAfterClass() throws Exception {
    }

    @BeforeEach
    void setUp() throws Exception {
    }

    @AfterEach
    void tearDown() throws Exception {
    }

    @DisplayName("Test with empty string")
    @Test
    void emptyStringTest() throws IOException {
	ArrayList<Tag> tags = ShazamTagsCsvParser.readWithCsvBeanReader("");
	assertEquals(0, tags.size());
    }

    @DisplayName("Test with random junk input")
    @Test
    void smallRandomJunkInput() throws IOException {
	try {
	    ArrayList<Tag> tags = ShazamTagsCsvParser
		    .readWithCsvBeanReader("asdfkjn2983y&&2,,,,29883\n29348u5hhns,,\nwoij329");
	    fail("Should have thrown IllegalArgumentException");
	} catch (IllegalArgumentException e) {
	}
    }

    @DisplayName("Test with one line valid CSV")
    @Test
    void oneLineValidTest() throws IOException {
	ArrayList<Tag> tags = ShazamTagsCsvParser.readWithCsvBeanReader(exampleTag1);
	Tag tag = tags.get(0);

	assertNotNull(tag);
	assertEquals(new GregorianCalendar(2020, 10, 27).getTime(), tag.getTagTime());
	assertEquals("Ignite A War", tag.getTrackTitle());
	assertEquals("Robert Hood", tag.getArtist());
	assertEquals("https://www.shazam.com/track/536840067/ignite-a-war", tag.getShazamInfoUrl());
	assertEquals("536840067", tag.getId());
    }

    @DisplayName("Test with one line valid CSV")
    @Test
    void oneLineValidTest2() throws IOException {
	ArrayList<Tag> tags = ShazamTagsCsvParser.readWithCsvBeanReader(exampleTag2);
	Tag tag = tags.get(0);

	assertNotNull(tag);
	assertEquals(new GregorianCalendar(2018, 03, 30).getTime(), tag.getTagTime());
	assertEquals("Are 'Friends' Electric? (THE PSYCHONAUTS & ONE MAN NAMED MO)", tag.getTrackTitle());
	assertEquals("Tubeway Army", tag.getArtist());
	assertEquals("https://www.shazam.com/track/10481775/are-friends-electric-the-psychonauts-and-one-man-named-mo",
		tag.getShazamInfoUrl());
	assertEquals("10481775", tag.getId());
    }

    @DisplayName("Test with a large, real CSV file")
    @Test
    void largeFileTest() throws IOException {
	ArrayList<Tag> tags = ShazamTagsCsvParser.readWithCsvBeanReader(largeTagsFile);
	assertEquals(512, tags.size());
    }

    @DisplayName("Check we're escaping quote marks")
    @Test
    void escapeQuoteTest() {
	String escaped = ShazamTagsCsvParser.removeUnescapedQuotes(escapeQuoteTest1);
	assertEquals(escapeQuoteTest1expected, escaped);
    }

    @DisplayName("Check we're not wrongly escaping quote at cell ends")
    @Test
    void escapeQuoteTest2() {
	String escaped = ShazamTagsCsvParser.removeUnescapedQuotes(escapeQuoteTest2);
	assertEquals(escapeQuoteTest2, escaped); // should be unchanged
    }

    @DisplayName("Test with one line valid CSV")
    @Test
    void oneLineValidTest3() throws IOException {
	ArrayList<Tag> tags = ShazamTagsCsvParser.readWithCsvBeanReader(escapeQuoteTest1);
	Tag tag = tags.get(0);

	assertNotNull(tag);
	assertEquals(new GregorianCalendar(2019, 9, 25).getTime(), tag.getTagTime());
	assertEquals("Happiness (Frankie Knuckles Club 12\" Mix)", tag.getTrackTitle());
	assertEquals("Nicole", tag.getArtist());
	assertEquals("https://www.shazam.com/track/275343426/happiness-frankie-knuckles-club-12-mix",
		tag.getShazamInfoUrl());
	assertEquals("275343426", tag.getId());
    }

    @DisplayName("Test with one line valid CSV")
    @Test
    void oneLineValidTest4() throws IOException {
	ArrayList<Tag> tags = ShazamTagsCsvParser.readWithCsvBeanReader(escapeQuoteTest2);
	Tag tag = tags.get(0);

	assertNotNull(tag);
	assertEquals(new GregorianCalendar(2020, 0, 21).getTime(), tag.getTagTime());
	assertEquals("Hall Of Mirrors", tag.getTrackTitle());
	assertEquals("B12", tag.getArtist());
	assertEquals("https://www.shazam.com/track/59078628/hall-of-mirrors", tag.getShazamInfoUrl());
	assertEquals("59078628", tag.getId());
    }
    
    @DisplayName("Test Escaping Regex function")
    @Test
    void manualRegexTest1() {
	String input = "\"This is a test \" with lots \" of quote \" marks \"";
	String expected = "\"This is a test \"\" with lots \"\" of quote \"\" marks \"";
	String result = ShazamTagsCsvParser.removeUnescapedQuotes(input);
	assertEquals(expected, result);
    }
}
