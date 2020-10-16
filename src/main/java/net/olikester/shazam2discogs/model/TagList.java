package net.olikester.shazam2discogs.model;

import java.util.ArrayList;

public class TagList {
    private final ArrayList<Tag> tagsList;

    public TagList () {
        tagsList = new ArrayList<>();
    }

    public ArrayList<Tag> toArrayList () {
        return new ArrayList<>(tagsList);
    }

    public void add (Tag newTag) {
        tagsList.add(newTag);
    }

    @Override
    public String toString () {
        return "TagList{" +
                "tagsList=" + tagsList +
                '}';
    }
}
