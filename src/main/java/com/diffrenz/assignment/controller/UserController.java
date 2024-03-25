package com.diffrenz.assignment.controller;

import com.diffrenz.assignment.domain.User;
import com.diffrenz.assignment.repository.UserRepository;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
@PreAuthorize("hasRole('ADMIN')")
public class UserController {

  private final Logger log = LoggerFactory.getLogger(UserController.class);

  private static final String ENTITY_NAME = "auth";

  private final UserRepository userRepository;

  public UserController(UserRepository userRepository) {
    this.userRepository = userRepository;
  }

  /**
   * Returns all {@link User} entities.
   *
   * @return a list of all users
   */

  @GetMapping("")
  public List<User> getAllUsers() {
    log.debug(" Rest api to get All Users ");
    return userRepository.findAll();
  }

}
