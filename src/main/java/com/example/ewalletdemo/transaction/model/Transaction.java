package com.example.ewalletdemo.transaction.model;

import com.example.ewalletdemo.transaction.response.DepositMoneyResponse;
import com.example.ewalletdemo.transaction.response.WithdrawMoneyResponse;
import lombok.*;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.math.BigDecimal;
import java.util.Date;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String transactionId;
    private String sender;
    private String receiver;
    private String purpose;
    private String status;
    private BigDecimal amount;

    private String transactionTime;

    public DepositMoneyResponse buildResponseForDeposit() {
        return DepositMoneyResponse.builder()
                .transactionId(this.transactionId)
                .amount(this.amount)
                .receiver(this.receiver)
                .status(this.status)
                .build();
    }

    public WithdrawMoneyResponse buildResponseForWithdraw() {
        return WithdrawMoneyResponse.builder()
                .transactionId(this.transactionId)
                .sender(this.sender)
                .receiver(this.receiver)
                .purpose(this.purpose)
                .status(this.status)
                .amount(this.amount)
                .transactionTime(new Date())
                .build();

    }
}
