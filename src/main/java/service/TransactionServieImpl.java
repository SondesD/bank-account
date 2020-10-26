package service;

import exception.BankAccountException;
import model.Account;
import model.AccountTransaction;
import model.TransactionType;
import org.springframework.beans.factory.annotation.Autowired;
import repository.AccountRepository;
import repository.TransactionRepository;

import java.time.LocalDate;
import java.time.Period;
import java.util.List;
import java.util.stream.Collectors;

public class TransactionServieImpl implements TransactionService{

    private static final double MAX_WITHDRAWAL_PER_TRANSACTION  = 1500;

    @Autowired
    AccountRepository accountRepository;

    @Autowired
    TransactionRepository transactionRepository;

    @Override
    public void makeDeposit(long idAccount, double amount) {
     Account bankAccount = accountRepository.findById(idAccount).orElseThrow(
             () ->new BankAccountException("Technical problem: Bank account not initialized or does not exist. Please contact bank directly"));

     saveTransaction(bankAccount.getAccountNumber(), amount, TransactionType.DEPOSIT.getType());
     double balance = bankAccount.getBalance() + amount;
     bankAccount.setBalance(balance);
     accountRepository.save(bankAccount);
    }

    private void saveTransaction(String accountNumber, double amount, String transactionType) {
        AccountTransaction accountTransaction = AccountTransaction.builder()
                .accountNumber(accountNumber)
                .amount(amount)
                .type(transactionType)
                .date(LocalDate.now())
                .build();
        transactionRepository.save(accountTransaction);
    }

    @Override
    public void makeWithdrawal(long idAccount, double amount) {
        Account bankAccount = accountRepository.findById(idAccount).orElseThrow(
                () ->new BankAccountException("Technical problem: Bank account not initialized or does not exist. Please contact bank directly"));

        if(amount > bankAccount.getBalance()){
            throw new BankAccountException("Error: You have insufficient funds");
        }

        if(amount > MAX_WITHDRAWAL_PER_TRANSACTION ){
            throw new BankAccountException("Error: Exceeded Maximum Withdrawal Per Transaction");
        }
        double balance = bankAccount.getBalance() - amount;
        bankAccount.setBalance(balance);
        accountRepository.save(bankAccount);
        saveTransaction(bankAccount.getAccountNumber(), amount, TransactionType.WITHDRAWAL.getType());
    }

    @Override
    public List<AccountTransaction> getTransactionsHistory(long idAccount, LocalDate startDate, LocalDate endDate) {
        Account bankAccount = accountRepository.findById(idAccount).orElseThrow(
                () ->new BankAccountException("Technical problem: Bank account not initialized or does not exist. Please contact bank directly"));

        List<AccountTransaction> transactions = transactionRepository.getAccountTransactionByAccountNumber(bankAccount.getAccountNumber())
                .stream()
                .filter(tx-> (tx.getDate().isAfter(startDate) || startDate.equals(tx.getDate()))
                        && (tx.getDate().isBefore(endDate) || endDate.equals(tx.getDate())))
                .collect(Collectors.toList());
        return transactions;
    }
}
