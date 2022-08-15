package com.example.ewalletdemo.user.service;

import com.example.ewalletdemo.transaction.model.TransactionStatus;
import com.example.ewalletdemo.transaction.request.DepositMoneyRequest;
import com.example.ewalletdemo.transaction.response.DepositMoneyResponse;
import com.example.ewalletdemo.transaction.response.WithdrawMoneyResponse;
import com.example.ewalletdemo.transaction.service.TransactionService;
import com.example.ewalletdemo.user.constants.ResponseCodes;
import com.example.ewalletdemo.user.exception.FinalException;
import com.example.ewalletdemo.user.model.WalletUser;
import com.example.ewalletdemo.user.repository.UserRepository;
import com.example.ewalletdemo.user.request.UserRequest;
import com.example.ewalletdemo.user.request.WithdrawMoneyRequest;
import com.example.ewalletdemo.user.response.UserResponse;
import com.example.ewalletdemo.user.util.UserValidator;
import com.example.ewalletdemo.wallet.model.Wallet;
import com.example.ewalletdemo.wallet.service.WalletService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Date;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
@ExtendWith(SpringExtension.class)
class UserServiceTest {

    @MockBean
    private UserRepository userRepository;

    @MockBean
    private UserValidator userValidator;

    @MockBean
    private UserRequest userRequest;
    @MockBean
    private WalletService walletService;

    @MockBean
    private DepositMoneyRequest depositMoneyRequest;

    @MockBean
    private DepositMoneyResponse expectedDepositMoneyResponse;

    @MockBean
    private TransactionService transactionService;

    @MockBean
    private WithdrawMoneyRequest withdrawMoneyRequest;

    @MockBean
    private WithdrawMoneyResponse withdrawMoneyResponse;

    @InjectMocks
    private UserService userService;

    @Test
    void shouldSuccessfullyCreateUserWallet() throws FinalException {
        String userId = "aman123";
        String name = "Aman";
        String emailId = "aman@gmail.com";
        int age = 33;
        Timestamp currentTimestamp = new Timestamp(System.currentTimeMillis());
        userRequest = new UserRequest(userId, emailId, name, age);
        Wallet wallet = new Wallet(1, userId, new BigDecimal(10.0), String.valueOf(currentTimestamp.getTime()));
        WalletUser walletUser = new WalletUser(1, userId, name, emailId, age, wallet);

        UserResponse expectedUserResponse = new UserResponse(userId, emailId, name, age, wallet.getBalance());
        Mockito.when(userRepository.save(Mockito.any(WalletUser.class))).thenReturn(walletUser);
        Mockito.when(walletService.createDefaultWallet(walletUser.getUserId())).thenReturn(wallet);

        UserResponse actualUserResponse = userService.createUserWallet(userRequest);
        assertEquals(expectedUserResponse.getUserId(), actualUserResponse.getUserId(), () -> "Expected is not same as Actual user response.");
        assertEquals(expectedUserResponse.getAge(), actualUserResponse.getAge(), () -> "Expected is not same as Actual user response.");
        assertEquals(expectedUserResponse.getName(), actualUserResponse.getName(), () -> "Expected is not same as Actual user response.");
        assertEquals(expectedUserResponse.getEmailId(), actualUserResponse.getEmailId(), () -> "Expected is not same as Actual user response.");
        assertEquals(expectedUserResponse.getWalletBalance(), actualUserResponse.getWalletBalance(), () -> "Expected is not same as Actual user response.");
    }

    @Test
    void shouldGiveEmptyWalletWhileCreatingUserWallet() throws FinalException {
        String userId = "aman123";
        String name = "Aman";
        String emailId = "aman@gmail.com";
        int age = 33;
        userRequest = new UserRequest(userId, emailId, name, age);
        Wallet wallet = null;
        WalletUser walletUser = new WalletUser(1, userId, name, emailId, age, wallet);
        Mockito.when(userRepository.save(Mockito.any(WalletUser.class))).thenReturn(walletUser);
        Mockito.when(walletService.createDefaultWallet(walletUser.getUserId())).thenReturn(wallet);
//        UserResponse actualUserResponse = userService.createUserWallet(userRequest);
        System.out.println();
        assertThrows(FinalException.class, () -> userService.createUserWallet(userRequest));

    }

    @Test
    void shouldGetUserWithWalletBalance() throws FinalException {
        String userId = "aman123";
        String name = "Aman";
        String emailId = "aman@gmail.com";
        int age = 33;
        Timestamp currentTimestamp = new Timestamp(System.currentTimeMillis());
        Wallet wallet = new Wallet(1, userId, new BigDecimal(10.0), String.valueOf(currentTimestamp.getTime()));
        WalletUser walletUser = new WalletUser(1, userId, name, emailId, age, wallet);
        UserResponse expectedUserResponse = new UserResponse(userId, emailId, name, age, wallet.getBalance());

        Mockito.when(walletService.fetchWalletForUser(userId)).thenReturn(wallet);
        Mockito.when(userRepository.findUserByUserId(userId)).thenReturn(walletUser);

        UserResponse actualUserResponse = userService.getUserWithWalletBalance(userId);
        assertEquals(expectedUserResponse.getUserId(), actualUserResponse.getUserId(), () -> "Expected is not same as Actual user response.");
        assertEquals(expectedUserResponse.getAge(), actualUserResponse.getAge(), () -> "Expected is not same as Actual user response.");
        assertEquals(expectedUserResponse.getName(), actualUserResponse.getName(), () -> "Expected is not same as Actual user response.");
        assertEquals(expectedUserResponse.getEmailId(), actualUserResponse.getEmailId(), () -> "Expected is not same as Actual user response.");
        assertEquals(expectedUserResponse.getWalletBalance(), actualUserResponse.getWalletBalance(), () -> "Expected is not same as Actual user response.");
    }


    @Test
    void shouldThrowExceptionWhenGetUserWithWalletBalance() throws FinalException {
        String userId = "aman123";
        Wallet wallet = null;
        Mockito.when(walletService.fetchWalletForUser(userId)).thenReturn(wallet);

        FinalException finalException = assertThrows(FinalException.class, () -> userService.getUserWithWalletBalance(userId));

        int expectedErrorCode = ResponseCodes.GENERIC_ERROR;
        String expectedErrorMessage = ResponseCodes.GENERIC_ERROR_MESSAGE;
        assertEquals(expectedErrorCode, finalException.getCode());
        assertEquals(expectedErrorMessage, finalException.getMessage());
    }


    @Test
    void shouldReturnEmptyUserWhenGetUserWithWalletBalance() throws FinalException {
        String userId = "aman123";
        WalletUser walletUser = null;
        Timestamp currentTimestamp = new Timestamp(System.currentTimeMillis());
        Wallet wallet = new Wallet(1, userId, new BigDecimal(10.0), String.valueOf(currentTimestamp.getTime()));

        Mockito.when(walletService.fetchWalletForUser(userId)).thenReturn(wallet);
        Mockito.when(userRepository.findUserByUserId(userId)).thenReturn(walletUser);

        assertThrows(FinalException.class, () -> userService.getUserWithWalletBalance(userId));

    }


    @Test
    void shouldDepositMoneyToUserWallet() throws FinalException {
        String receiverUserId = "aman123";
        String name = "Aman";
        String emailId = "aman@gmail.com";
        int age = 33;
        Timestamp currentTimestamp = new Timestamp(System.currentTimeMillis());
        Wallet wallet = new Wallet(1, receiverUserId, new BigDecimal(10.0), String.valueOf(currentTimestamp.getTime()));
        WalletUser walletUser = new WalletUser(1, receiverUserId, name, emailId, age, wallet);

        depositMoneyRequest = new DepositMoneyRequest(receiverUserId, new BigDecimal(10.0));
        expectedDepositMoneyResponse = new DepositMoneyResponse(UUID.randomUUID().toString(), receiverUserId, TransactionStatus.SUCCESS.toString(), new BigDecimal(10.0));

        Mockito.when(userRepository.findUserByUserId(depositMoneyRequest.getReceiver())).thenReturn(walletUser);
        Mockito.when(transactionService.depositMoneyToUserWallet(depositMoneyRequest)).thenReturn(expectedDepositMoneyResponse);

        DepositMoneyResponse actualDepositMoneyResponse = userService.depositMoneyToUserWallet(depositMoneyRequest);
        assertEquals(expectedDepositMoneyResponse.getReceiver(), actualDepositMoneyResponse.getReceiver(), () -> "Expected is not same as Actual user response.");
        assertEquals(expectedDepositMoneyResponse.getAmount(), actualDepositMoneyResponse.getAmount(), () -> "Expected is not same as Actual user response.");
        assertEquals(expectedDepositMoneyResponse.getStatus(), actualDepositMoneyResponse.getStatus(), () -> "Expected is not same as Actual user response.");
    }

    @Test
    void shouldReturnEmptyUserWhenDepositMoneyToUserWallet() {
        String receiverUserId = "aman123";
        depositMoneyRequest = new DepositMoneyRequest(receiverUserId, new BigDecimal(10.0));
        WalletUser walletUser = null;

        Mockito.when(userRepository.findUserByUserId(depositMoneyRequest.getReceiver())).thenReturn(walletUser);

        assertThrows(FinalException.class, () -> userService.depositMoneyToUserWallet(depositMoneyRequest));
    }

    @Test
    void shouldWithdrawMoneyToUserWallet() throws FinalException {
        String senderUserId = "john345";
        String senderEmailId = "john@yahoo.com";
        String senderName = "John Doe";
        int senderAge = 21;
        String receiverUserId = "aman123";
        String receiverEmailId = "aman123@gmail.com";
        String receiverName = "Aman";
        int receiverAge = 33;

        BigDecimal amount = new BigDecimal(10.0);
        String purpose = "rent";
        Timestamp currentTimestamp = new Timestamp(System.currentTimeMillis());

        Wallet senderWallet = new Wallet(1, senderUserId, new BigDecimal(15.0), String.valueOf(currentTimestamp.getTime()));
        WalletUser senderWalletUser = new WalletUser(1, senderUserId, senderName, senderEmailId, senderAge, senderWallet);

        currentTimestamp = new Timestamp(System.currentTimeMillis());
        Wallet receiverWallet = new Wallet(2, receiverUserId, new BigDecimal(17.0), String.valueOf(currentTimestamp.getTime()));
        WalletUser receiverWalletUser = new WalletUser(2, receiverUserId, receiverName, receiverEmailId, receiverAge, receiverWallet);

        withdrawMoneyRequest = new WithdrawMoneyRequest(senderUserId, receiverUserId, amount, purpose);
        withdrawMoneyResponse = new WithdrawMoneyResponse(UUID.randomUUID().toString(), senderUserId, receiverUserId, purpose, TransactionStatus.SUCCESS.toString(), amount, new Date());
        Mockito.when(userRepository.findUserByUserId(withdrawMoneyRequest.getSender())).thenReturn(senderWalletUser);
        Mockito.when(userRepository.findUserByUserId(withdrawMoneyRequest.getReceiver())).thenReturn(receiverWalletUser);

        Mockito.when(transactionService.withdrawMoneyToUserWallet(withdrawMoneyRequest)).thenReturn(withdrawMoneyResponse);
        WithdrawMoneyResponse actualResponse = userService.withdrawMoneyToUserWallet(withdrawMoneyRequest);

        assertEquals(withdrawMoneyResponse.getReceiver(), actualResponse.getReceiver(), () -> "Expected is not same as Actual user response.");
        assertEquals(withdrawMoneyResponse.getSender(), actualResponse.getSender(), () -> "Expected is not same as Actual user response.");
        assertEquals(withdrawMoneyResponse.getAmount(), actualResponse.getAmount(), () -> "Expected is not same as Actual user response.");
        assertEquals(withdrawMoneyResponse.getStatus(), actualResponse.getStatus(), () -> "Expected is not same as Actual user response.");
        assertEquals(withdrawMoneyResponse.getPurpose(), actualResponse.getPurpose(), () -> "Expected is not same as Actual user response.");
    }

    @Test
    void shouldReturnExceptionWhenSenderReceiverNullForWithdrawMoneyToUserWallet() {
        String senderUserId = "john345";
        String receiverUserId = "aman123";
        BigDecimal amount = new BigDecimal(10.0);
        String purpose = "rent";

        withdrawMoneyRequest = new WithdrawMoneyRequest(senderUserId, receiverUserId, amount, purpose);
        WalletUser senderWalletUser = null;
        WalletUser receiverWalletUser = null;

        Mockito.when(userRepository.findUserByUserId(withdrawMoneyRequest.getSender())).thenReturn(senderWalletUser);
        Mockito.when(userRepository.findUserByUserId(withdrawMoneyRequest.getReceiver())).thenReturn(receiverWalletUser);

        assertThrows(FinalException.class, () -> userService.withdrawMoneyToUserWallet(withdrawMoneyRequest));
    }

    @Test
    void shouldWithdrawMoneyFromSelfWallet() throws FinalException {
        String receiverUserId = "aman123";
        String receiverEmailId = "aman123@gmail.com";
        String receiverName = "Aman";
        int receiverAge = 33;
        BigDecimal amount = new BigDecimal(10.0);
        String purpose = "rent";
        Timestamp currentTimestamp = new Timestamp(System.currentTimeMillis());

        Wallet receiverWallet = new Wallet(2, receiverUserId, new BigDecimal(17.0), String.valueOf(currentTimestamp.getTime()));
        WalletUser receiverWalletUser = new WalletUser(2, receiverUserId, receiverName, receiverEmailId, receiverAge, receiverWallet);

        withdrawMoneyRequest = new WithdrawMoneyRequest(null, receiverUserId, amount, purpose);
        withdrawMoneyResponse = new WithdrawMoneyResponse(UUID.randomUUID().toString(), null, receiverUserId, purpose, TransactionStatus.SUCCESS.toString(), amount, new Date());

        Mockito.when(userRepository.findUserByUserId(withdrawMoneyRequest.getReceiver())).thenReturn(receiverWalletUser);
        Mockito.when(transactionService.withdrawMoneyFromSelfWallet(withdrawMoneyRequest)).thenReturn(withdrawMoneyResponse);

        WithdrawMoneyResponse actualResponse = userService.withdrawMoneyFromSelfWallet(withdrawMoneyRequest);

        assertEquals(withdrawMoneyResponse.getReceiver(), actualResponse.getReceiver(), () -> "Expected is not same as Actual user response.");
        assertEquals(withdrawMoneyResponse.getSender(), actualResponse.getSender(), () -> "Expected is not same as Actual user response.");
        assertEquals(withdrawMoneyResponse.getAmount(), actualResponse.getAmount(), () -> "Expected is not same as Actual user response.");
        assertEquals(withdrawMoneyResponse.getStatus(), actualResponse.getStatus(), () -> "Expected is not same as Actual user response.");
        assertEquals(withdrawMoneyResponse.getPurpose(), actualResponse.getPurpose(), () -> "Expected is not same as Actual user response.");
    }

}
