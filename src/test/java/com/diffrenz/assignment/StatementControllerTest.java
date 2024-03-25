package com.diffrenz.assignment;

import com.diffrenz.assignment.controller.StatementController;
import com.diffrenz.assignment.service.StatementService;
import com.diffrenz.assignment.service.VM.StatementVM;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@AutoConfigureMockMvc
@SpringBootTest
public class StatementControllerTest {

    @Test
    void testGetStatements() {

        StatementService statementService = Mockito.mock(StatementService.class);

        StatementController statementController = new StatementController(statementService);

        LocalDate fromDate = LocalDate.of(2023, 1, 1);
        LocalDate toDate = LocalDate.of(2024, 12, 31);
        Long accountNumber = 123456L;
        Double amountFrom = 100.0;
        Double amountTo = 1000.0;
        
        List<StatementVM> expectedStatements = Collections.singletonList(new StatementVM());
        Mockito.when(statementService.getStatements(accountNumber, amountFrom, amountTo, fromDate, toDate))
                .thenReturn(expectedStatements);

        List<StatementVM> actualStatements = statementController.getStatements(fromDate, toDate, accountNumber,
                amountFrom, amountTo);

        assertEquals(expectedStatements, actualStatements);
    }
}
