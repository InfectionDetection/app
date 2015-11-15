package com.hjs.db.repo;

import com.hjs.db.obj.User;
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
public interface UserRepository extends CrudRepository<User, Long> {

    User findByUuid(String uuid);

}
