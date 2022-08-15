package com.example.ewalletdemo.user.request;

import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class WithdrawMoneyRequest {

    private String sender;
    private String receiver;
    private BigDecimal amount;
    private String purpose;
}
