package com.diffrenz.assignment.service;

import com.diffrenz.assignment.repository.StatementRepository;
import com.diffrenz.assignment.service.DTO.StatementDTO;
import com.diffrenz.assignment.service.VM.StatementVM;
import org.springframework.stereotype.Service;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static com.diffrenz.assignment.service.AuthService.getCurrentUserRoles;

@Service
public class StatementService {

    private final StatementRepository statementRepository;

    private final AuthService authService;

    public StatementService(StatementRepository statementRepository, AuthService authService) {
        this.statementRepository = statementRepository;
        this.authService = authService;
    }

    public static String calculateSHA256(String input) {
        try {
            // Create MessageDigest instance for SHA-256
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            md.update(input.getBytes());
            byte[] bytes = md.digest();
            StringBuilder sb = new StringBuilder();
            for (byte b : bytes) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Returns a list of statements for the specified account, filtered by the
     * specified amount range and date range.
     *
     * @param accountNumber the account number
     * @param amountFrom    the minimum amount
     * @param amountTo      the maximum amount
     * @param fromDate      the start date
     * @param toDate        the end date
     * @return the list of statements
     */

    public List<StatementVM> getStatements(Long accountNumber, Double amountFrom, Double amountTo, LocalDate fromDate,
            LocalDate toDate) {
        toDate = (toDate != null) ? toDate : LocalDate.now();
        fromDate = (fromDate != null) ? fromDate : toDate.minusMonths(3);
        Long userId = null;
        boolean isAdmin = getCurrentUserRoles().contains("ADMIN");
        if (!isAdmin) {
            userId = authService.getCurrentUser().getId();
            amountFrom = null;
            amountTo = null;
            accountNumber = null;
        }

        List<StatementDTO> statementDTOS = statementRepository.findAllStatement(fromDate, toDate, accountNumber,
                amountFrom, amountTo, userId);

        List<StatementVM> statementsVM = new ArrayList<>(statementDTOS.size());

        for (StatementDTO statement : statementDTOS) {
            StatementVM statementVM = new StatementVM();
            statementVM.setTransactionDate(statement.getTransactionDate());
            statementVM.setAmount(statement.getAmount());

            if (!isAdmin && statement.getAccountNumber() != null) {
                statementVM.setAccountNumber(calculateSHA256(statement.getAccountNumber()));
            } else {
                statementVM.setAccountNumber(statement.getAccountNumber());
            }

            statementsVM.add(statementVM);
        }
        return statementsVM;
    }

}
