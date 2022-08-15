package com.example.ewalletdemo.wallet.dao;

import com.example.ewalletdemo.wallet.model.Wallet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface WalletRepository extends JpaRepository<Wallet, Integer> {

    Wallet findByUserId(String userId);
}
