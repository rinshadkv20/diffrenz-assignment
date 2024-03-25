package com.diffrenz.assignment.service.DTO;

import java.time.LocalDate;

public interface StatementDTO {

    String getAccountNumber();

    LocalDate getTransactionDate();

    Double getAmount();

}
