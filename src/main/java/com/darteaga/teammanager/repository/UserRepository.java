package com.darteaga.teammanager.repository;

import com.darteaga.teammanager.domain.User;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * Spring Data JPA repository for the User entity.
 */
public interface UserRepository extends JpaRepository<User,Long> {

    @Query("select distinct user from User user left join fetch user.manytomanys")
    List<User> findAllWithEagerRelationships();

    @Query("select user from User user left join fetch user.manytomanys where user.id =:id")
    User findOneWithEagerRelationships(@Param("id") Long id);

}
