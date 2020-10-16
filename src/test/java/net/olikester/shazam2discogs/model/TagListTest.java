package net.olikester.shazam2discogs.model;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import net.olikester.shazam2discogs.json.ShazamTagsDeserializer;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

@SpringBootTest (classes=TagList.class)
class TagListTest {

    private static String oneTagJson1 = "";

    @BeforeAll
    static void setup () throws IOException {
        oneTagJson1 = Files.readString(Path.of("src/test/resources/one-tag-test.json"));
    }

    @Test
    public void oneTagParseTest () throws JsonProcessingException {
        Tag expectedTag = new Tag(524476162, "Model Village", "IDLES");

        ObjectMapper mapper = new ObjectMapper();
        SimpleModule module =
                new SimpleModule("ShazamTagsDeserializer", new Version(1, 0, 0, null, null, null));
        module.addDeserializer(TagList.class, new ShazamTagsDeserializer());
        mapper.registerModule(module);
        TagList tags = mapper.readValue(oneTagJson1, TagList.class);

        assertThat(expectedTag).isEqualToComparingFieldByField(tags.toArrayList().get(0));
    }

    @Test
    public void oneTagParseTestWithRelease () {
        Tag expectedTag = new Tag(524476162, "Model Village", "IDLES", new Release("Ultra Mono", "Partisan Records", "2020"));
    }

}