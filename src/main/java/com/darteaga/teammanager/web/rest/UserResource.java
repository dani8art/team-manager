package com.darteaga.teammanager.web.rest;

import com.codahale.metrics.annotation.Timed;
import com.darteaga.teammanager.domain.User;
import com.darteaga.teammanager.repository.UserRepository;
import com.darteaga.teammanager.web.rest.util.HeaderUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import javax.validation.Valid;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

/**
 * REST controller for managing User.
 */
@RestController
@RequestMapping("/api")
public class UserResource {

    private final Logger log = LoggerFactory.getLogger(UserResource.class);

    @Inject
    private UserRepository userRepository;

    /**
     * POST  /users -> Create a new user.
     */
    @RequestMapping(value = "/users",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<User> createUser(@Valid @RequestBody User user) throws URISyntaxException {
        log.debug("REST request to save User : {}", user);
        if (user.getId() != null) {
            return ResponseEntity.badRequest().header("Failure", "A new user cannot already have an ID").body(null);
        }
        User result = userRepository.save(user);
        return ResponseEntity.created(new URI("/api/users/" + result.getId()))
                .headers(HeaderUtil.createEntityCreationAlert("user", result.getId().toString()))
                .body(result);
    }

    /**
     * PUT  /users -> Updates an existing user.
     */
    @RequestMapping(value = "/users",
        method = RequestMethod.PUT,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<User> updateUser(@Valid @RequestBody User user) throws URISyntaxException {
        log.debug("REST request to update User : {}", user);
        if (user.getId() == null) {
            return createUser(user);
        }
        User result = userRepository.save(user);
        return ResponseEntity.ok()
                .headers(HeaderUtil.createEntityUpdateAlert("user", user.getId().toString()))
                .body(result);
    }

    /**
     * GET  /users -> get all the users.
     */
    @RequestMapping(value = "/users",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public List<User> getAllUsers(@RequestParam(required = false) String filter) {
        if ("onetoone-is-null".equals(filter)) {
            log.debug("REST request to get all Users where onetoone is null");
            return StreamSupport
                .stream(userRepository.findAll().spliterator(), false)
                .filter(user -> user.getOnetoone() == null)
                .collect(Collectors.toList());
        }

        log.debug("REST request to get all Users");
        return userRepository.findAllWithEagerRelationships();
    }

    /**
     * GET  /users/:id -> get the "id" user.
     */
    @RequestMapping(value = "/users/{id}",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<User> getUser(@PathVariable Long id) {
        log.debug("REST request to get User : {}", id);
        return Optional.ofNullable(userRepository.findOneWithEagerRelationships(id))
            .map(user -> new ResponseEntity<>(
                user,
                HttpStatus.OK))
            .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    /**
     * DELETE  /users/:id -> delete the "id" user.
     */
    @RequestMapping(value = "/users/{id}",
            method = RequestMethod.DELETE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        log.debug("REST request to delete User : {}", id);
        userRepository.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert("user", id.toString())).build();
    }
}
