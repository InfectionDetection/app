package com.hjs.db.repo;

import com.hjs.db.obj.Track;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

/**
 * Created by Saswat on 11/14/2015.
 */
@Repository
public interface TrackRepository extends CrudRepository<Track, Long> {

    @Query("SELECT t FROM Track t WHERE t.date_created > :bottom AND t.date_created < :top")
    List<Track> findInRange(@Param("bottom") Date bottom, @Param("top") Date top);

}
