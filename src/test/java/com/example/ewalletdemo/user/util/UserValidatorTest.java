package com.example.ewalletdemo.user.util;

import com.example.ewalletdemo.transaction.request.DepositMoneyRequest;
import com.example.ewalletdemo.user.exception.FinalException;
import com.example.ewalletdemo.user.model.WalletUser;
import com.example.ewalletdemo.user.request.UserRequest;
import com.example.ewalletdemo.user.request.WithdrawMoneyRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
@ExtendWith(SpringExtension.class)
class UserValidatorTest {

    @InjectMocks
    UserValidator userValidator;

    @Test
    void shouldPassUserRequestAllFieldsValidator() throws FinalException {

        UserRequest userRequest = null;
        FinalException finalException = assertThrows(FinalException.class, () -> userValidator.userRequestAllFieldsValidator(userRequest));

        int expectedErrorCode = 400;
        String expectedErrorMessage = "request payload cannot be null or empty";
        assertEquals(expectedErrorCode, finalException.getCode());
        assertEquals(expectedErrorMessage, finalException.getMessage());
    }

    @Test
    void shouldThrowExceptionWhenEmailIdNull_UserRequestAllFieldsValidator() {
        String userId = "aman123";
        String name = "Aman";
        String emailId = null;
        int age = 33;

        UserRequest userRequest = new UserRequest(userId, emailId, name, age);
        FinalException finalException = assertThrows(FinalException.class, () -> userValidator.userRequestAllFieldsValidator(userRequest));

        int expectedErrorCode = 400;
        String expectedErrorMessage = "email-id cannot be null or empty";
        assertEquals(expectedErrorCode, finalException.getCode());
        assertEquals(expectedErrorMessage, finalException.getMessage());
    }

    @Test
    void shouldThrowExceptionWhenInvalidEmail_userRequestAllFieldsValidator() {
        String userId = "aman123";
        String name = "Aman";
        String emailId = "amanxyz@gmail";
        int age = 33;

        userValidator.EMAIL_REGEX = "[a-z0-9._%+-]+@[a-z0-9.-]+\\.[a-z]{2,3}";
        UserRequest userRequest = new UserRequest(userId, emailId, name, age);
        FinalException finalException = assertThrows(FinalException.class, () -> userValidator.userRequestAllFieldsValidator(userRequest));

        int expectedErrorCode = 400;
        String expectedErrorMessage = "invalid email id";
        assertEquals(expectedErrorCode, finalException.getCode());
        assertEquals(expectedErrorMessage, finalException.getMessage());
    }

    @Test
    void shouldThrowExceptionWhenNameEmpty_userRequestAllFieldsValidator() {
        String userId = "aman123";
        String name = null;
        String emailId = "amanxyz@gmail.com";
        int age = 33;

        userValidator.EMAIL_REGEX = "[a-z0-9._%+-]+@[a-z0-9.-]+\\.[a-z]{2,3}";
        UserRequest userRequest = new UserRequest(userId, emailId, name, age);
        FinalException finalException = assertThrows(FinalException.class, () -> userValidator.userRequestAllFieldsValidator(userRequest));

        int expectedErrorCode = 400;
        String expectedErrorMessage = "name cannot be null of empty";
        assertEquals(expectedErrorCode, finalException.getCode());
        assertEquals(expectedErrorMessage, finalException.getMessage());
    }


    @Test
    void shouldThrowExceptionWhenAgeZero_userRequestAllFieldsValidator() {
        String userId = "aman123";
        String name = "Aman";
        String emailId = "amanxyz@gmail.com";
        int age = 0;

        userValidator.EMAIL_REGEX = "[a-z0-9._%+-]+@[a-z0-9.-]+\\.[a-z]{2,3}";
        UserRequest userRequest = new UserRequest(userId, emailId, name, age);
        FinalException finalException = assertThrows(FinalException.class, () -> userValidator.userRequestAllFieldsValidator(userRequest));

        int expectedErrorCode = 400;
        String expectedErrorMessage = "age cannot be null or empty";
        assertEquals(expectedErrorCode, finalException.getCode());
        assertEquals(expectedErrorMessage, finalException.getMessage());
    }

    @Test
    void shouldThrowExceptionWhenUnderAge_userRequestAllFieldsValidator() {
        String userId = "aman123";
        String name = "Aman";
        String emailId = "amanxyz@gmail.com";
        int age = 15;

        userValidator.EMAIL_REGEX = "[a-z0-9._%+-]+@[a-z0-9.-]+\\.[a-z]{2,3}";
        userValidator.LEGAL_AGE = 18;
        UserRequest userRequest = new UserRequest(userId, emailId, name, age);
        FinalException finalException = assertThrows(FinalException.class, () -> userValidator.userRequestAllFieldsValidator(userRequest));

        int expectedErrorCode = 400;
        String expectedErrorMessage = "user should be above legal age for signup";
        assertEquals(expectedErrorCode, finalException.getCode());
        assertEquals(expectedErrorMessage, finalException.getMessage());
    }

    @Test
    void shouldPassWhenUserIdNull_UserIdValidator() throws FinalException {
        String userId = null;
        FinalException finalException = assertThrows(FinalException.class, () -> userValidator.userIdValidator(userId));

        int expectedErrorCode = 400;
        String expectedErrorMessage = "user-id cannot be null or empty";
        assertEquals(expectedErrorCode, finalException.getCode());
        assertEquals(expectedErrorMessage, finalException.getMessage());
    }


    @Test
    void shouldThrowExceptionWhenUserNull_userAccountValidator() {
        WalletUser walletUser = null;
        String userId = "aman123";
        FinalException finalException = assertThrows(FinalException.class, () -> userValidator.userAccountValidator(walletUser, userId));

        int expectedErrorCode = 500;
        String expectedErrorMessage = "user with user-id" + userId + " not found.";
        assertEquals(expectedErrorCode, finalException.getCode());
        assertEquals(expectedErrorMessage, finalException.getMessage());
    }

    @Test
    void shouldThrowExceptionWhenReceiverNull_depositMoneyAllFieldsValidator() {
        String receiverUserId = null;
        DepositMoneyRequest depositMoneyRequest = new DepositMoneyRequest(receiverUserId, new BigDecimal(10.0));

        FinalException finalException = assertThrows(FinalException.class, () -> userValidator.depositMoneyAllFieldsValidator(depositMoneyRequest));

        int expectedErrorCode = 400;
        String expectedErrorMessage = "receiver cannot be null or empty";
        assertEquals(expectedErrorCode, finalException.getCode());
        assertEquals(expectedErrorMessage, finalException.getMessage());
    }

    @Test
    void shouldThrowExceptionWhenSenderNull_withdrawMoneyRequestAllFieldsValidator() {
        String senderUserId = null;
        String receiverUserId = "aman123";
        BigDecimal amount = new BigDecimal(10.0);
        String purpose = "rent";

        WithdrawMoneyRequest withdrawMoneyRequest = new WithdrawMoneyRequest(senderUserId, receiverUserId, amount, purpose);
        FinalException finalException = assertThrows(FinalException.class, () -> userValidator.withdrawMoneyRequestAllFieldsValidator(withdrawMoneyRequest));

        int expectedErrorCode = 400;
        String expectedErrorMessage = "sender cannot be null or empty";
        assertEquals(expectedErrorCode, finalException.getCode());
        assertEquals(expectedErrorMessage, finalException.getMessage());

    }

    @Test
    void shouldThrowExceptionWhenReceiverNull_withdrawMoneyRequestAllFieldsValidator() {
        String senderUserId = "john345";
        String receiverUserId = null;
        BigDecimal amount = new BigDecimal(10.0);
        String purpose = "rent";

        WithdrawMoneyRequest withdrawMoneyRequest = new WithdrawMoneyRequest(senderUserId, receiverUserId, amount, purpose);
        FinalException finalException = assertThrows(FinalException.class, () -> userValidator.withdrawMoneyRequestAllFieldsValidator(withdrawMoneyRequest));

        int expectedErrorCode = 400;
        String expectedErrorMessage = "receiver cannot be null or empty";
        assertEquals(expectedErrorCode, finalException.getCode());
        assertEquals(expectedErrorMessage, finalException.getMessage());
    }

    @Test
    void shouldThrowExceptionWhenReceiverNull_withdrawMoneyRequestPartialValidator() {

        String receiverUserId = null;
        BigDecimal amount = new BigDecimal(10.0);
        String purpose = "rent";

        WithdrawMoneyRequest withdrawMoneyRequest = new WithdrawMoneyRequest(null, receiverUserId, amount, purpose);
        FinalException finalException = assertThrows(FinalException.class, () -> userValidator.withdrawMoneyRequestPartialValidator(withdrawMoneyRequest));

        int expectedErrorCode = 400;
        String expectedErrorMessage = "receiver cannot be null or empty";
        assertEquals(expectedErrorCode, finalException.getCode());
        assertEquals(expectedErrorMessage, finalException.getMessage());
    }

    @Test
    void shouldThrowExceptionWhenAmountZero_amountValidator() {
        Double amount = 0.0;
        FinalException finalException = assertThrows(FinalException.class, () -> userValidator.amountValidator(amount));

        int expectedErrorCode = 400;
        String expectedErrorMessage = "amount should be greater than zero.";
        assertEquals(expectedErrorCode, finalException.getCode());
        assertEquals(expectedErrorMessage, finalException.getMessage());

    }
}
