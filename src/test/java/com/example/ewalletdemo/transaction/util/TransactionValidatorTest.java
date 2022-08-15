package com.example.ewalletdemo.transaction.util;

import com.example.ewalletdemo.transaction.request.DepositMoneyRequest;
import com.example.ewalletdemo.user.constants.ResponseCodes;
import com.example.ewalletdemo.user.exception.FinalException;
import com.example.ewalletdemo.user.request.WithdrawMoneyRequest;
import com.example.ewalletdemo.wallet.model.Wallet;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
class TransactionValidatorTest {

    @InjectMocks
    private TransactionValidator transactionValidator;

    @Test
    void shouldThrowExceptionWhenReceiverNull_depositMoneyAllFieldsValidator() {
        String receiver = null;
        BigDecimal amount = new BigDecimal(10.0);
        DepositMoneyRequest depositMoneyRequest = new DepositMoneyRequest(receiver, amount);

        FinalException finalException = assertThrows(FinalException.class, () -> transactionValidator.depositMoneyAllFieldsValidator(depositMoneyRequest));

        int expectedErrorCode = HttpStatus.BAD_REQUEST.value();
        String expectedErrorMessage = "receiver cannot be null or empty";
        assertEquals(expectedErrorCode, finalException.getCode());
        assertEquals(expectedErrorMessage, finalException.getMessage());
    }


    @Test
    void shouldThrowExceptionWhenAmountZero_depositMoneyAllFieldsValidator() {
        String receiver = "aman123";
        BigDecimal amount = new BigDecimal(0.0);
        DepositMoneyRequest depositMoneyRequest = new DepositMoneyRequest(receiver, amount);

        FinalException finalException = assertThrows(FinalException.class, () -> transactionValidator.depositMoneyAllFieldsValidator(depositMoneyRequest));

        int expectedErrorCode = HttpStatus.BAD_REQUEST.value();
        String expectedErrorMessage = "amount should be greater than zero.";
        assertEquals(expectedErrorCode, finalException.getCode());
        assertEquals(expectedErrorMessage, finalException.getMessage());
    }

    @Test
    void shouldThrowExceptionWhenSenderNull_withdrawMoneyRequestAllFieldsValidator() {
        String sender = null;
        String receiver = "aman123";
        BigDecimal amount = new BigDecimal(10.0);
        String purpose = "rent";
        WithdrawMoneyRequest withdrawMoneyRequest = new WithdrawMoneyRequest(sender, receiver, amount, purpose);
        FinalException finalException = assertThrows(FinalException.class, () -> transactionValidator.withdrawMoneyRequestAllFieldsValidator(withdrawMoneyRequest));

        int expectedErrorCode = HttpStatus.BAD_REQUEST.value();
        String expectedErrorMessage = "sender cannot be null or empty";
        assertEquals(expectedErrorCode, finalException.getCode());
        assertEquals(expectedErrorMessage, finalException.getMessage());

    }

    @Test
    void shouldThrowExceptionWhenReceiverNull_withdrawMoneyRequestAllFieldsValidator() {
        String sender = "john456";
        String receiver = null;
        BigDecimal amount = new BigDecimal(10.0);
        String purpose = "rent";
        WithdrawMoneyRequest withdrawMoneyRequest = new WithdrawMoneyRequest(sender, receiver, amount, purpose);
        FinalException finalException = assertThrows(FinalException.class, () -> transactionValidator.withdrawMoneyRequestAllFieldsValidator(withdrawMoneyRequest));

        int expectedErrorCode = HttpStatus.BAD_REQUEST.value();
        String expectedErrorMessage = "receiver cannot be null or empty";
        assertEquals(expectedErrorCode, finalException.getCode());
        assertEquals(expectedErrorMessage, finalException.getMessage());
    }

    @Test
    void shouldThrowExceptionWhenAmountZero_amountValidator() {
        Double amount = 0.0;
        FinalException finalException = assertThrows(FinalException.class, () -> transactionValidator.amountValidator(amount));

        int expectedErrorCode = HttpStatus.BAD_REQUEST.value();
        String expectedErrorMessage = "amount should be greater than zero.";
        assertEquals(expectedErrorCode, finalException.getCode());
        assertEquals(expectedErrorMessage, finalException.getMessage());
    }

    @Test
    void shouldThrowExceptionWhenWalletNull_isValidWallet() {
        Wallet wallet = null;
        String userId = "aman123";
        FinalException finalException = assertThrows(FinalException.class, () -> transactionValidator.isValidWallet(wallet, userId));

        int expectedErrorCode = ResponseCodes.GENERIC_ERROR;
        String expectedErrorMessage = ResponseCodes.GENERIC_ERROR_MESSAGE;
        assertEquals(expectedErrorCode, finalException.getCode());
        assertEquals(expectedErrorMessage, finalException.getMessage());
    }
}
