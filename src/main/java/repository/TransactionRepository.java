package repository;

import model.AccountTransaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TransactionRepository extends JpaRepository<AccountTransaction, Long> {

    List<AccountTransaction> getAccountTransactionByAccountNumber(String accountNumber);
}
