package com.example.ewalletdemo.transaction.response;


import lombok.*;

import java.math.BigDecimal;
import java.util.Date;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class WithdrawMoneyResponse {
    private String transactionId;
    private String sender;
    private String receiver;
    private String purpose;
    private String status;
    private BigDecimal amount;
    private Date transactionTime;

}
