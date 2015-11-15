package com.hjs.db.repo;

import com.hjs.db.obj.GCM;
import com.hjs.db.obj.User;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

/**
 * Created by Saswat on 11/15/2015.
 */
@Repository
public interface GCMRepository extends CrudRepository<GCM, Long> {
    GCM findByUser(User user);
}
