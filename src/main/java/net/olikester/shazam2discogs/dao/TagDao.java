package net.olikester.shazam2discogs.dao;

import net.olikester.shazam2discogs.model.Tag;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TagDao extends JpaRepository<Tag, String> {
}
