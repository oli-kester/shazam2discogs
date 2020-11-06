package net.olikester.shazam2discogs.model;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class TagTest {
    
    @DisplayName("Test simple search string creation.")
    @Test
    public void searchStringTest1() {
	Tag tag = new Tag();
	tag.setTrackTitle("Model Village");
	tag.setArtist("IDLES");
	assertEquals("Model Village IDLES", tag.getSimpleSearchTerm());
    }
}
