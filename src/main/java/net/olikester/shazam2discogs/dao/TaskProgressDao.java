package net.olikester.shazam2discogs.dao;

import org.springframework.data.jpa.repository.JpaRepository;

import net.olikester.shazam2discogs.model.TaskProgress;

public interface TaskProgressDao extends JpaRepository<TaskProgress, String> {

}
