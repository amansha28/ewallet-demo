package com.example.ewalletdemo.transaction.util;

import com.example.ewalletdemo.transaction.request.DepositMoneyRequest;
import com.example.ewalletdemo.user.constants.ResponseCodes;
import com.example.ewalletdemo.user.exception.FinalException;
import com.example.ewalletdemo.user.request.WithdrawMoneyRequest;
import com.example.ewalletdemo.wallet.model.Wallet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;


@Component
public class TransactionValidator {

    private final Logger logger = LoggerFactory.getLogger(TransactionValidator.class);

    public void depositMoneyAllFieldsValidator(DepositMoneyRequest depositMoneyRequest) throws FinalException {
        logger.debug("Started TransactionValidator :: depositMoneyAllFieldsValidator");
        if (depositMoneyRequest.getReceiver() == null || depositMoneyRequest.getReceiver().isEmpty()) {
            throw new FinalException(HttpStatus.BAD_REQUEST.value(), "receiver cannot be null or empty");
        }

        if (depositMoneyRequest.getAmount().doubleValue() <= 0) {
            throw new FinalException(HttpStatus.BAD_REQUEST.value(), "amount should be greater than zero.");
        }
        logger.debug("Completed TransactionValidator :: depositMoneyAllFieldsValidator");
    }

    public void withdrawMoneyRequestAllFieldsValidator(WithdrawMoneyRequest withdrawMoneyRequest) throws FinalException {
        logger.debug("Started TransactionValidator :: withdrawMoneyRequestAllFieldsValidator");
        if (withdrawMoneyRequest.getSender() == null || withdrawMoneyRequest.getSender().isEmpty()) {
            throw new FinalException(HttpStatus.BAD_REQUEST.value(), "sender cannot be null or empty");
        }

        if (withdrawMoneyRequest.getReceiver() == null || withdrawMoneyRequest.getReceiver().isEmpty()) {
            throw new FinalException(HttpStatus.BAD_REQUEST.value(), "receiver cannot be null or empty");
        }
        amountValidator(withdrawMoneyRequest.getAmount().doubleValue());
        logger.debug("Completed TransactionValidator :: withdrawMoneyRequestAllFieldsValidator");
    }


    public void amountValidator(Double amount) throws FinalException {
        logger.debug("Started TransactionValidator :: amountValidator");
        if (amount <= 0) {
            throw new FinalException(HttpStatus.BAD_REQUEST.value(), "amount should be greater than zero.");
        }
        logger.debug("Completed TransactionValidator :: amountValidator");
    }

    public void isValidWallet(Wallet wallet, String userId) throws FinalException {
        logger.debug("Started TransactionValidator :: isValidWallet");
        if (wallet == null) {
            logger.error("wallet for user-id : {} cannot be found.", userId);
            throw new FinalException(ResponseCodes.GENERIC_ERROR, ResponseCodes.GENERIC_ERROR_MESSAGE);
        }
        logger.debug("Completed TransactionValidator :: isValidWallet");
    }
}
