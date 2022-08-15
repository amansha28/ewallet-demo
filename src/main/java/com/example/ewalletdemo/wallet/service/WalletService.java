package com.example.ewalletdemo.wallet.service;


import com.example.ewalletdemo.wallet.dao.WalletRepository;
import com.example.ewalletdemo.wallet.model.Wallet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.sql.Timestamp;

@Service
public class WalletService {

    public final BigDecimal defaultBalance = BigDecimal.valueOf(0.0);
    private final Logger logger = LoggerFactory.getLogger(WalletService.class);
    @Autowired
    private WalletRepository walletRepository;

    /*
     * Create default wallet for given userId.
     * */
    public Wallet createDefaultWallet(String userId) {
        logger.debug("Started WalletService :: createDefaultWallet ");
        Timestamp currentTimestamp = new Timestamp(System.currentTimeMillis());
        Wallet wallet = Wallet.builder()
                .userId(userId)
                .balance(defaultBalance)
                .walletCreationTime(String.valueOf(currentTimestamp.getTime()))
                .build();

        logger.debug("Completed WalletService :: createDefaultWallet ");
        return walletRepository.save(wallet);
    }

    public Wallet fetchWalletForUser(String userId) {
        return walletRepository.findByUserId(userId);
    }

    // to update the wallet Balance during credits/widthdrawals.
    public Wallet updateWallet(Wallet wallet) {
        return walletRepository.save(wallet);
    }
}
