package com.example.ewalletdemo.user.model;


import com.example.ewalletdemo.user.response.UserResponse;
import com.example.ewalletdemo.wallet.model.Wallet;
import lombok.*;

import javax.persistence.*;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor // this is need cuz @Entity needs a no-arg constructor. Hibernate requires default constructor.
@Builder // cuz we need to create user from UserObject.
@ToString
public class WalletUser {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(unique = true)
    private String userId;
    private String name;
    private String emailId;
    private int age;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "wallet_id", referencedColumnName = "id")
    private Wallet wallet;

    // to build WalletUser -> userResponse with balance
    public UserResponse buildUserResponseFromUserWithWallet() {
        return UserResponse.builder()
                .userId(this.userId)
                .emailId(this.emailId)
                .name(this.name)
                .age(this.age)
                .walletBalance(wallet.getBalance())
                .build();
    }
}