package model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.time.LocalDate;


@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class AccountTransaction {

        @Id
        @GeneratedValue(strategy= GenerationType.AUTO)
        private Long id;
        private String accountNumber;
        private String type;
        private double amount;
        private LocalDate date;
}
