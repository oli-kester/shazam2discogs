package net.olikester.shazam2discogs.model;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import net.olikester.shazam2discogs.json.ShazamTagsDeserializer;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest ( classes = TagList.class )
class TagListTest {

    private static String oneTagJson1 = "";
    private static ObjectMapper mapper;

    @BeforeAll
    public static void setup () throws IOException {
        oneTagJson1 = Files.readString(Path.of("src/test/resources/one-tag-test.json"));
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
    public void oneTagParseTestWithRelease () throws JsonProcessingException {
        TagList tags = mapper.readValue(oneTagJson1, TagList.class);
        Tag tag = tags.toArrayList().get(0);

        assertThat(tag).isNotNull();
        assertThat(tag.getKey()).isEqualTo(524476162);
        assertThat(tag.getTrackTitle()).isEqualTo("Model Village");
        assertThat(tag.getArtist()).isEqualTo("IDLES");
        assertThat(tag.getRelease()).isEqualToComparingFieldByField(
                new Release("Ultra Mono", "Partisan Records", "2020")
        );
    }
}