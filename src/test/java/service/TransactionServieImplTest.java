package service;

import exception.BankAccountException;
import model.Account;
import model.AccountTransaction;
import model.TransactionType;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import repository.AccountRepository;
import repository.TransactionRepository;

import javax.transaction.Transaction;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class TransactionServieImplTest {

    public static final long ACCOUNT_ID = 2L;
    public static final String ACCOUNT_NUMBER = "accountNumber";
    @InjectMocks
    TransactionServieImpl transactionService;

    @Mock
    AccountRepository accountRepository;

    @Mock
    TransactionRepository transactionRepository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    void makeDeposit_should_returnException_when_accountNotExists(){
        when(accountRepository.findById(ACCOUNT_ID)).thenReturn(Optional.empty());
        assertThrows(BankAccountException.class, () -> {
            transactionService.makeDeposit(ACCOUNT_ID, 400);
        });
    }

    @Test
    void makeDeposit_should_saveAccountAndTransaction_when_accountExist(){
        when(accountRepository.findById(ACCOUNT_ID)).thenReturn(Optional.of(buildAccount()));
        transactionService.makeDeposit(ACCOUNT_ID, 400);
        Mockito.verify(transactionRepository).save(Mockito.any(AccountTransaction.class));
        Mockito.verify(accountRepository).save(Mockito.any(Account.class));

    }

    @Test
    void makeWithdrawal_should_returnException_when_accountNotExists(){
        when(accountRepository.findById(ACCOUNT_ID)).thenReturn(Optional.empty());
        Exception exception = assertThrows(BankAccountException.class, () -> {
            transactionService.makeWithdrawal(ACCOUNT_ID, 200);
        });
        assertTrue(exception.getMessage().contains("Bank account not initialized or does not exist"));
    }

    @Test
    void makeWithdrawal_should_returnException_when_insufficientFunds(){
        when(accountRepository.findById(ACCOUNT_ID)).thenReturn(Optional.of(buildAccount()));
        Exception exception = assertThrows(BankAccountException.class, () -> {
            transactionService.makeWithdrawal(ACCOUNT_ID, 1800);
        });
        assertTrue(exception.getMessage().contains("You have insufficient funds"));
    }

    @Test
    void makeWithdrawal_should_returnException_when_amountExceedsTheMax(){
        when(accountRepository.findById(ACCOUNT_ID)).thenReturn(Optional.of(buildAccount()));
        Exception exception = assertThrows(BankAccountException.class, () -> {
            transactionService.makeWithdrawal(ACCOUNT_ID, 1520);
        });
        assertTrue(exception.getMessage().contains("Exceeded Maximum Withdrawal Per Transaction"));
    }

    @Test
    void makeWithdrawal_should_saveAccountAndTransaction_when_everythingIsOk(){
        when(accountRepository.findById(ACCOUNT_ID)).thenReturn(Optional.of(buildAccount()));
        transactionService.makeWithdrawal(ACCOUNT_ID, 200);
        Mockito.verify(transactionRepository).save(Mockito.any(AccountTransaction.class));
        Mockito.verify(accountRepository).save(Mockito.any(Account.class));
    }

    @Test
    void getTransactionsHistory_should_returnException_when_accountNotExists(){
        LocalDate startDate = LocalDate.now().minusDays(30);
        LocalDate endDate = LocalDate.now();
        when(accountRepository.findById(ACCOUNT_ID)).thenReturn(Optional.empty());
        assertThrows(BankAccountException.class, () -> {
            transactionService.getTransactionsHistory(ACCOUNT_ID, startDate, endDate);
        });
    }

    @Test
    void getTransactionsHistory_should_returnTheListOfTransactions(){
        LocalDate startDate = LocalDate.now().minusDays(30);
        LocalDate endDate = LocalDate.now();
        List<AccountTransaction> transactions = new ArrayList<>();
        AccountTransaction accountTransaction1 = buildAccountTransaction(2, 2L, TransactionType.WITHDRAWAL);
        AccountTransaction accountTransaction2 = buildAccountTransaction(0, 1L, TransactionType.DEPOSIT);
        transactions.add(accountTransaction1);
        transactions.add(accountTransaction2);
        when(accountRepository.findById(ACCOUNT_ID)).thenReturn(Optional.of(buildAccount()));
        when(transactionRepository.getAccountTransactionByAccountNumber(ACCOUNT_NUMBER)).thenReturn(transactions);
        List<AccountTransaction> returnedTransactions = transactionService.getTransactionsHistory(ACCOUNT_ID, startDate, endDate);
        Mockito.verify(transactionRepository).getAccountTransactionByAccountNumber(ACCOUNT_NUMBER);
        assertEquals(2, returnedTransactions.size());
    }

    private AccountTransaction buildAccountTransaction(int i, long l, TransactionType withdrawal) {
        return AccountTransaction.builder().accountNumber(ACCOUNT_NUMBER)
                .amount(200).date(LocalDate.now().minusDays(i)).id(l).type(withdrawal.getType())
                .build();
    }

    @Test
    void getTransactionsHistory_should_returnTheFiltredByDateListOfTransactions(){
        LocalDate startDate = LocalDate.now().minusDays(30);
        LocalDate endDate = LocalDate.now();
        List<AccountTransaction> transactions = new ArrayList<>();
        AccountTransaction accountTransaction1 = buildAccountTransaction(2, 2L, TransactionType.WITHDRAWAL);
        AccountTransaction accountTransaction2 = buildAccountTransaction(32, 2L, TransactionType.DEPOSIT);
        transactions.add(accountTransaction1);
        transactions.add(accountTransaction2);
        when(accountRepository.findById(ACCOUNT_ID)).thenReturn(Optional.of(buildAccount()));
        when(transactionRepository.getAccountTransactionByAccountNumber(ACCOUNT_NUMBER)).thenReturn(transactions);
        List<AccountTransaction> returnedTransactions = transactionService.getTransactionsHistory(ACCOUNT_ID, startDate, endDate);
        Mockito.verify(transactionRepository).getAccountTransactionByAccountNumber(ACCOUNT_NUMBER);
        assertEquals(1, returnedTransactions.size());
    }

    private Account buildAccount() {
        Account account = Account.builder().id(ACCOUNT_ID).accountNumber(ACCOUNT_NUMBER).balance(1700).build();
        return account;
    }
}