package service;

import model.AccountTransaction;

import java.time.LocalDate;
import java.util.List;

public interface TransactionService {

    void makeDeposit(long idAccount, double amount);

    void makeWithdrawal(long idAccount, double amount);

    List<AccountTransaction>  getTransactionsHistory(long idAccount, LocalDate startDate, LocalDate endDate);
}
