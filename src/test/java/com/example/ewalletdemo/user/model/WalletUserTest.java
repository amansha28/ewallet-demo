package com.example.ewalletdemo.user.model;

import com.example.ewalletdemo.user.response.UserResponse;
import com.example.ewalletdemo.wallet.model.Wallet;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
class WalletUserTest {

    @InjectMocks
    private WalletUser walletUser;

    @Test
    void shouldPassSuccessfully_buildUserResponseFromUserWithWallet() {
        String userId = "aman123";
        String name = "Aman";
        String emailId = "aman123@gmail.com";
        int age = 33;

        Wallet wallet = new Wallet(1, userId, new BigDecimal(10.0), String.valueOf(new Date()));
        WalletUser user = new WalletUser(1, userId, name, emailId, age, wallet);
        walletUser.setUserId(userId);
        walletUser.setName(name);
        walletUser.setEmailId(emailId);
        walletUser.setAge(age);

        UserResponse actualResponse = user.buildUserResponseFromUserWithWallet();

        assertEquals(walletUser.getUserId(), actualResponse.getUserId());
        assertEquals(walletUser.getName(), actualResponse.getName());
        assertEquals(walletUser.getEmailId(), actualResponse.getEmailId());
        assertEquals(walletUser.getAge(), actualResponse.getAge());
    }
}
