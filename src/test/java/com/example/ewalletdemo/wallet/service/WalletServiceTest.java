package com.example.ewalletdemo.wallet.service;

import com.example.ewalletdemo.user.exception.FinalException;
import com.example.ewalletdemo.wallet.dao.WalletRepository;
import com.example.ewalletdemo.wallet.model.Wallet;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.math.BigDecimal;
import java.sql.Timestamp;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
@ExtendWith(SpringExtension.class)
class WalletServiceTest {

    @MockBean
    private WalletRepository walletRepository;

    @InjectMocks
    private WalletService walletService;


    @Test
    void shouldCreateWallet_createDefaultWallet() throws FinalException {
        String userId = "aman123";
        Timestamp currentTimestamp = new Timestamp(System.currentTimeMillis());

        Wallet expectedWallet = Wallet.builder().userId(userId).walletCreationTime(String.valueOf(currentTimestamp.getTime()))
                .balance(new BigDecimal(0.0)).build();
        Mockito.when(walletRepository.save(Mockito.any(Wallet.class))).thenReturn(expectedWallet);
        assertEquals(expectedWallet, walletService.createDefaultWallet(userId));
    }

    @Test
    void shouldReturnWalletForUser_fetchWalletForUser() throws FinalException {
        String userId = "aman123";
        Timestamp currentTimestamp = new Timestamp(System.currentTimeMillis());

        Wallet expectedWallet = Wallet.builder().userId(userId).walletCreationTime(String.valueOf(currentTimestamp.getTime()))
                .balance(new BigDecimal(0.0)).build();
        Mockito.when(walletRepository.findByUserId(userId)).thenReturn(expectedWallet);
        assertEquals(expectedWallet, walletService.fetchWalletForUser(userId));
    }

    @Test
    void shouldUpdateWallet_updateWallet() {
        String userId = "aman123";
        Timestamp currentTimestamp = new Timestamp(System.currentTimeMillis());

        Wallet expectedWallet = Wallet.builder().userId(userId).walletCreationTime(String.valueOf(currentTimestamp.getTime()))
                .balance(new BigDecimal(0.0)).build();
        Mockito.when(walletRepository.save(Mockito.any(Wallet.class))).thenReturn(expectedWallet);

        assertEquals(expectedWallet, walletService.updateWallet(expectedWallet));
    }
}
