package com.diffrenz.assignment.repository;

import com.diffrenz.assignment.domain.Statement;
import com.diffrenz.assignment.service.DTO.StatementDTO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface StatementRepository extends JpaRepository<Statement, Long> {

        @Query(value = "SELECT CAST(a.account_number AS CHAR) AS accountNumber, \n" +
                        "       DATE(s.transaction_date) AS transactionDate, \n" +
                        "       s.amount \n" +
                        "FROM `statement` s\n" +
                        "JOIN account a ON s.account_id = a.id\n" +
                        "WHERE DATE(s.transaction_date) BETWEEN DATE(:fromDate) AND DATE(:toDate)\n" +
                        "AND (:accountNumber IS NULL OR CAST(a.account_number AS CHAR) = :accountNumber)\n" +
                        "AND (:amountFrom IS NULL OR s.amount >= :amountFrom)\n" +
                        "AND (:amountTo IS NULL OR s.amount <= :amountTo) \n" +
                        " AND (:userId is null or a.user_id=:userId );\n", nativeQuery = true)
        List<StatementDTO> findAllStatement(@Param("fromDate") LocalDate fromDate,
                        @Param("toDate") LocalDate toDate,
                        @Param("accountNumber") Long accountNumber,
                        @Param("amountFrom") Double amountFrom,
                        @Param("amountTo") Double amountTo,
                        @Param("userId") Long userId);
}
