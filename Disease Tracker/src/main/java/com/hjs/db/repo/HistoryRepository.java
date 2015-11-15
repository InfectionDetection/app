package com.hjs.db.repo;

import com.hjs.db.obj.History;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

/**
 * Created by Saswat on 11/14/2015.
 */
@Repository
public interface HistoryRepository extends CrudRepository<History, Long> {

}
