package com.example.ewalletdemo.user.util;

import com.example.ewalletdemo.transaction.request.DepositMoneyRequest;
import com.example.ewalletdemo.user.exception.FinalException;
import com.example.ewalletdemo.user.model.WalletUser;
import com.example.ewalletdemo.user.request.UserRequest;
import com.example.ewalletdemo.user.request.WithdrawMoneyRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;


@Component
public class UserValidator {

    private static final String RECEIVER_NULL_OR_EMPTY = "receiver cannot be null or empty";
    private final Logger logger = LoggerFactory.getLogger(UserValidator.class);

    @Value("${legal-age}")
    int LEGAL_AGE;
    @Value("${valid-email-regex}")
    String EMAIL_REGEX;

    public void userRequestAllFieldsValidator(UserRequest userRequest) throws FinalException {
        logger.debug("Started UserValidator :: userRequestAllFieldsValidator ");
        if (userRequest == null)
            throw new FinalException(HttpStatus.BAD_REQUEST.value(), "request payload cannot be null or empty");

        WalletUser user = userRequest.buildUserFromUserRequest();

        userIdValidator(user.getUserId());

        if (user.getEmailId() == null || user.getEmailId().isEmpty()) {
            throw new FinalException(HttpStatus.BAD_REQUEST.value(), "email-id cannot be null or empty");
        } else {
            String userEmailIdLowerCase = user.getEmailId().toLowerCase();
            boolean isValidEmail = userEmailIdLowerCase.matches(EMAIL_REGEX);
            if (!isValidEmail)
                throw new FinalException(HttpStatus.BAD_REQUEST.value(), "invalid email id");
        }

        if (user.getName() == null || user.getName().isEmpty()) {
            throw new FinalException(HttpStatus.BAD_REQUEST.value(), "name cannot be null of empty");
        }

        if (user.getAge() == 0) {
            throw new FinalException(HttpStatus.BAD_REQUEST.value(), "age cannot be null or empty");
        } else if (user.getAge() < LEGAL_AGE) {
            throw new FinalException(HttpStatus.BAD_REQUEST.value(), "user should be above legal age for signup");
        }
        logger.debug("Completed UserValidator :: userRequestAllFieldsValidator ");
    }

    public void userIdValidator(String userId) throws FinalException {
        logger.debug("Started UserValidator :: userIdValidator ");
        if (userId == null || userId.isEmpty())
            throw new FinalException(HttpStatus.BAD_REQUEST.value(), "user-id cannot be null or empty");
        logger.debug("Completed UserValidator :: userIdValidator ");
    }

    public void userAccountValidator(WalletUser user, String userId) throws FinalException {
        logger.debug("Started UserValidator :: userAccountValidator ");
        if (user == null) {
            throw new FinalException(HttpStatus.INTERNAL_SERVER_ERROR.value(), "user with user-id" + userId + " not found.");
        }
        logger.debug("Completed UserValidator :: userAccountValidator ");
    }

    public void depositMoneyAllFieldsValidator(DepositMoneyRequest depositMoneyRequest) throws FinalException {
        logger.debug("Started UserValidator :: depositMoneyAllFieldsValidator ");
        if (depositMoneyRequest.getReceiver() == null || depositMoneyRequest.getReceiver().isEmpty()) {
            throw new FinalException(HttpStatus.BAD_REQUEST.value(), RECEIVER_NULL_OR_EMPTY);
        }

        amountValidator(depositMoneyRequest.getAmount().doubleValue());
        logger.debug("Completed UserValidator :: depositMoneyAllFieldsValidator ");
    }

    public void withdrawMoneyRequestAllFieldsValidator(WithdrawMoneyRequest withdrawMoneyRequest) throws FinalException {
        logger.debug("Started UserValidator :: withdrawMoneyRequestAllFieldsValidator ");
        if (withdrawMoneyRequest.getSender() == null || withdrawMoneyRequest.getSender().isEmpty()) {
            throw new FinalException(HttpStatus.BAD_REQUEST.value(), "sender cannot be null or empty");
        }

        if (withdrawMoneyRequest.getReceiver() == null || withdrawMoneyRequest.getReceiver().isEmpty()) {
            throw new FinalException(HttpStatus.BAD_REQUEST.value(), RECEIVER_NULL_OR_EMPTY);
        }
        amountValidator(withdrawMoneyRequest.getAmount().doubleValue());
        logger.debug("Completed UserValidator :: withdrawMoneyRequestAllFieldsValidator ");
    }


    public void withdrawMoneyRequestPartialValidator(WithdrawMoneyRequest withdrawMoneyRequest) throws FinalException {
        logger.debug("Started UserValidator :: withdrawMoneyRequestPartialValidator ");
        if (withdrawMoneyRequest.getReceiver() == null || withdrawMoneyRequest.getReceiver().isEmpty()) {
            throw new FinalException(HttpStatus.BAD_REQUEST.value(), RECEIVER_NULL_OR_EMPTY);
        }
        amountValidator(withdrawMoneyRequest.getAmount().doubleValue());
        logger.debug("Completed UserValidator :: withdrawMoneyRequestPartialValidator ");
    }

    public void amountValidator(Double amount) throws FinalException {
        logger.debug("Started UserValidator :: amountValidator ");
        if (amount <= 0) {
            throw new FinalException(HttpStatus.BAD_REQUEST.value(), "amount should be greater than zero.");
        }
        logger.debug("Completed UserValidator :: amountValidator ");
    }


}
