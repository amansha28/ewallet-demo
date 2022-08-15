package com.example.ewalletdemo.user.response;


import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder // cuz we need to create userResponse from WalletUser.
public class UserResponse {
    private String userId;
    private String emailId;
    private String name;
    private int age;

    // adding to get the balance.
    private BigDecimal walletBalance;
}
