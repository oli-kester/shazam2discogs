package net.olikester.shazam2discogs.json;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import net.olikester.shazam2discogs.model.Release;
import net.olikester.shazam2discogs.model.Tag;
import net.olikester.shazam2discogs.model.TagList;

import java.io.IOException;

public class ShazamTagsDeserializer extends StdDeserializer<TagList> {

    public ShazamTagsDeserializer () {
        this(null);
    }

    public ShazamTagsDeserializer (Class<?> vc) {
        super(vc);
    }

    @Override
    /**
     * Deserialize a full Shazam JSON export into a list of tags, discarding irrelevant data.
     */
    public TagList deserialize (JsonParser parser, DeserializationContext context) throws IOException {
        TagList tagList = new TagList();
        ObjectCodec codec = parser.getCodec();
        JsonNode rootNode = codec.readTree(parser);

        for (JsonNode currTag : rootNode.get("tags")) {
            Tag newTag = new Tag();
            JsonNode headers = currTag.get("track").get("heading");

            newTag.setKey(currTag.get("key").asInt());
            newTag.setTrackTitle(headers.get("title").asText());
            newTag.setArtist(headers.get("subtitle").asText());

            Release newRelease = new Release();
            for (JsonNode currFootnote : currTag.get("track").get("footnotes")) {
                switch (currFootnote.get("title").asText()) {
                    case "Album":
                        newRelease.setAlbum(currFootnote.get("value").asText());
                        break;
                    case "Label":
                        newRelease.setLabel(currFootnote.get("value").asText());
                        break;
                    case "Released":
                        newRelease.setReleaseYear(currFootnote.get("value").asText());
                        break;
                }
            }
            newTag.setRelease(newRelease);
            tagList.add(newTag);
        }

        return tagList;
    }
}
