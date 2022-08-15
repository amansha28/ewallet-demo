package com.example.ewalletdemo.transaction.model;

import com.example.ewalletdemo.transaction.response.DepositMoneyResponse;
import com.example.ewalletdemo.transaction.response.WithdrawMoneyResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Date;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
class TransactionTest {

    @InjectMocks
    Transaction transaction;

    @Test
    void shouldPassSuccessfully_buildResponseForDeposit() {
        String transactionId = UUID.randomUUID().toString();
        BigDecimal amount = new BigDecimal(10.0);
        String receiverId = "aman123";
        String status = TransactionStatus.SUCCESS.toString();
        DepositMoneyResponse expectedResponse = new DepositMoneyResponse(transactionId, receiverId, status, amount);
        transaction.setTransactionId(transactionId);
        transaction.setAmount(amount);
        transaction.setStatus(TransactionStatus.SUCCESS.toString());
        transaction.setReceiver(receiverId);
        DepositMoneyResponse actualResponse = transaction.buildResponseForDeposit();

        assertEquals(expectedResponse.getTransactionId(), actualResponse.getTransactionId());
        assertEquals(expectedResponse.getStatus(), actualResponse.getStatus());
        assertEquals(expectedResponse.getReceiver(), actualResponse.getReceiver());
        assertEquals(expectedResponse.getAmount(), actualResponse.getAmount());
    }

    @Test
    void shouldPassSuccessfully_buildResponseForWithdraw() {
        String transactionId = UUID.randomUUID().toString();
        BigDecimal amount = new BigDecimal(10.0);
        String receiverId = "aman123";
        String senderId = "john456";
        String status = TransactionStatus.SUCCESS.toString();
        String purpose = "credit";
        Date transactionTime = new Date();
        WithdrawMoneyResponse expectedResponse = new WithdrawMoneyResponse(transactionId, senderId, receiverId, purpose, status, amount, transactionTime);

        transaction.setTransactionId(transactionId);
        transaction.setAmount(amount);
        transaction.setStatus(TransactionStatus.SUCCESS.toString());
        transaction.setReceiver(receiverId);
        transaction.setSender(senderId);
        transaction.setPurpose(purpose);

        WithdrawMoneyResponse actualResponse = transaction.buildResponseForWithdraw();

        assertEquals(expectedResponse.getTransactionId(), actualResponse.getTransactionId());
        assertEquals(expectedResponse.getSender(), actualResponse.getSender());
        assertEquals(expectedResponse.getReceiver(), actualResponse.getReceiver());
        assertEquals(expectedResponse.getStatus(), actualResponse.getStatus());
        assertEquals(expectedResponse.getAmount(), actualResponse.getAmount());
        assertEquals(expectedResponse.getPurpose(), actualResponse.getPurpose());
    }
}
