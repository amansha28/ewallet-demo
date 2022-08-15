package com.example.ewalletdemo.wallet.model;

import lombok.*;

import javax.persistence.*;
import java.math.BigDecimal;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder // cuz we want to create a wallet builder.
@ToString
public class Wallet {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(unique = true)
    private String userId;
    private BigDecimal balance;
    private String walletCreationTime;

}
