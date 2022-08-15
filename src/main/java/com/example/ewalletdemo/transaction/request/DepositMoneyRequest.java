package com.example.ewalletdemo.transaction.request;

import lombok.*;

import java.math.BigDecimal;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class DepositMoneyRequest {
    private String receiver;
    private BigDecimal amount;
}
