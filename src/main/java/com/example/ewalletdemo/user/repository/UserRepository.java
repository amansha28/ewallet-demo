package com.example.ewalletdemo.user.repository;

import com.example.ewalletdemo.user.model.WalletUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<WalletUser, Integer> {

    WalletUser findUserByUserId(String userId);
}
