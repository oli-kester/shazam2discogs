package net.olikester.shazam2discogs.json;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import net.olikester.shazam2discogs.model.Tag;
import net.olikester.shazam2discogs.model.TagList;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;


@SpringBootTest ( classes = TagList.class )
class DeserializationTest {

    private static String oneTagJson1 = "";
    private static String oneTagJson2 = "";
    private static String largeShazamDoc = "";
    private static final String emptyDoc = "";
    private static String oneTagInvalidTrailingComma = "";
    private static ObjectMapper mapper;

    @BeforeAll
    public static void setup () throws IOException {
        oneTagJson1 = Files.readString(Path.of("src/test/resources/one-tag-test.json"));
        oneTagJson2 = Files.readString(Path.of("src/test/resources/one-tag-test2.json"));
        largeShazamDoc = Files.readString(Path.of("src/test/resources/personal-shazams-oct-2020.json"));
        oneTagInvalidTrailingComma = Files.readString(Path.of("src/test/resources/one-tag-invalid-trailing-comma.json"));
    }

    @BeforeEach
    public void setupEach () {
        mapper = new ObjectMapper();
        SimpleModule module =
                new SimpleModule("ShazamTagsDeserializer", new Version(1, 0, 0, null, null, null));
        module.addDeserializer(TagList.class, new ShazamTagsDeserializer());
        mapper.registerModule(module);
    }

    /**
     * Check we can parse a single Shazam tag correctly.
     */
    @Test
    public void oneTagParseTestWithRelease1 () throws JsonProcessingException {
        TagList tags = mapper.readValue(oneTagJson1, TagList.class);
        Tag tag = tags.toArrayList().get(0);

        assertNotNull(tag);
        assertEquals(524476162, tag.getKey());
        assertEquals("Model Village", tag.getTrackTitle());
        assertEquals("IDLES", tag.getArtist());
        assertEquals("Ultra Mono", tag.getRelease().getAlbum());
        assertEquals("Partisan Records", tag.getRelease().getLabel());
        assertEquals("2020", tag.getRelease().getReleaseYear());
    }

    /**
     * Check we can parse a single Shazam tag correctly.
     */
    @Test
    public void oneTagParseTestWithRelease2 () throws JsonProcessingException {
        TagList tags = mapper.readValue(oneTagJson2, TagList.class);
        Tag tag = tags.toArrayList().get(0);

        assertNotNull(tag);
        assertEquals(5996069, tag.getKey());
        assertEquals("Lazy", tag.getTrackTitle());
        assertEquals("X-Press 2 Feat. David Byrne", tag.getArtist());
        assertEquals("Lazy (feat. David Byrne) - Single", tag.getRelease().getAlbum());
        assertEquals("Skint Records", tag.getRelease().getLabel());
        assertEquals("2002", tag.getRelease().getReleaseYear());
    }

    /**
     * Just check there are no errors thrown with a real, large Shazam JSON file.
     */
    @Test
    public void largeFileSimpleTest () {
        TagList tags = null;
        try {
            tags = mapper.readValue(largeShazamDoc, TagList.class);
        } catch (Exception e) {
            e.printStackTrace();
            fail("Check there are no errors thrown with a real, large Shazam JSON file. Exception thrown. ");
        }
        if (tags.toArrayList().size() < 1) {
            fail("Check there are no errors thrown with a real, large Shazam JSON file. No tags scanned. ");
        }
    }

    /**
     * Test with empty file
     */
    @Test
    public void emptyStringTest () {
        try {
            mapper.readValue(emptyDoc, TagList.class);
            fail("Test with empty file. Program did not throw exception. ");
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }

    /**
     * Test with invalid file with trailing comma.
     */
    @Test
    public void invalidFileTest () {
        try {
            mapper.readValue(oneTagInvalidTrailingComma, TagList.class);
            fail("Test with invalid file with trailing comma. Exception not thrown. ");
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }

    // TODO throw exception on really large file.
}