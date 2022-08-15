package com.example.ewalletdemo.transaction.response;

import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class DepositMoneyResponse {

    private String transactionId;
    private String receiver;
    private String status;
    private BigDecimal amount;
}
