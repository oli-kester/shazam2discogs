package net.olikester.shazam2discogs.model;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

@SpringBootTest(classes=Tag.class)
class TagAndReleaseTest {

    private static String oneTagJson1;

    @BeforeAll
    static void setup () throws IOException {
        oneTagJson1 = Files.readString(Path.of("src/test/resources/one-tag-test.json"));
    }

    @Test
    public void oneTagParseTest () {
        Tag expectedTag = new Tag("524476162", "Model Village", "IDLES");

        System.out.println(oneTagJson1);
    }

    @Test
    public void oneTagParseTestWithRelease () {
        Tag expectedTag = new Tag("524476162", "Model Village", "IDLES", new Release("Ultra Mono", "Partisan Records", "2020"));


    }

}