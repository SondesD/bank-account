package controller;


import model.AccountTransaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import service.TransactionService;

import java.time.LocalDate;
import java.util.List;

@RestController
public class TransactionController {

    @Autowired
    TransactionService transactionService;

    @PostMapping("/deposit")
    void makeDeposit(@PathVariable long accoundId, @PathVariable double amount){
        transactionService.makeDeposit(accoundId, amount);
    }

    @PostMapping("/withdrawal")
    void makeWithdrawal(@PathVariable long accoundId, @PathVariable double amount){
        transactionService.makeWithdrawal(accoundId, amount);
    }

    @GetMapping("/statement")
    List<AccountTransaction> getTransactionsStatement(@PathVariable long accoundId, @PathVariable LocalDate startDate, @PathVariable LocalDate endDate){
       return transactionService.getTransactionsHistory(accoundId, startDate, endDate);
    }
}
