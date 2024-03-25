package com.diffrenz.assignment.controller;

import com.diffrenz.assignment.service.StatementService;
import com.diffrenz.assignment.service.VM.StatementVM;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api")
@PreAuthorize("hasAnyRole(ADMIN,USER)")
public class StatementController {

    private final Logger log = LoggerFactory.getLogger(StatementController.class);

    private final StatementService statementService;

    public StatementController(StatementService statementService) {
        this.statementService = statementService;
    }

    /**
     * Returns a list of Statements based on the specified criteria.
     *
     * @param fromDate      the start date (inclusive)
     * @param toDate        the end date (inclusive)
     * @param accountNumber the account number
     * @param amountFrom    the minimum amount
     * @param amountTo      the maximum amount
     * @return a list of Statements
     */
    @GetMapping("/statements")
    public List<StatementVM> getStatements(
            @RequestParam(required = false) LocalDate fromDate,
            @RequestParam(required = false) LocalDate toDate,
            @RequestParam(required = false) Long accountNumber,
            @RequestParam(required = false) Double amountFrom,
            @RequestParam(required = false) Double amountTo) {
        log.debug("REST request to get a list of Statements");

        return statementService.getStatements(accountNumber, amountFrom, amountTo, fromDate, toDate);

    }

}




