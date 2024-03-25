package com.diffrenz.assignment.controller;

import com.diffrenz.assignment.domain.Account;
import com.diffrenz.assignment.domain.Role;
import com.diffrenz.assignment.repository.AccountRepository;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/accounts")
@PreAuthorize("hasAuthority('ADMIN')")
public class AccountController {

  private static final String ENTITY_NAME = "account";
  private final Logger log = LoggerFactory.getLogger(AccountController.class);

  private final AccountRepository accountRepository;

  public AccountController(AccountRepository accountRepository) {
    this.accountRepository = accountRepository;
  }

  /**
   * Returns all {@link Account} entities.
   *
   * @return a list of all accounts
   */
  @GetMapping("")
  public List<Account> getAllAccounts() {
    log.debug("Rest api to  get All Accounts");
    return accountRepository.findAll();
  }

  /**
   * Returns an {@link Account} entity based on the specified ID.
   *
   * @param id the ID of the account to retrieve
   * @return the account with the specified ID, or null if no account with the specified ID exists
   */
  @GetMapping("/{id}")
  public Account getAccountById(@PathVariable Long id) {
    log.debug(" Rest API call to get account");
    return accountRepository.findById(id).orElse(null);
  }
}
