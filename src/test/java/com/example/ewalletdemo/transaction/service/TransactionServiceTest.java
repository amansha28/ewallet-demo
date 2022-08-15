package com.example.ewalletdemo.transaction.service;

import com.example.ewalletdemo.transaction.constants.TransactionConstants;
import com.example.ewalletdemo.transaction.model.Transaction;
import com.example.ewalletdemo.transaction.model.TransactionStatus;
import com.example.ewalletdemo.transaction.repository.TransactionRepository;
import com.example.ewalletdemo.transaction.request.DepositMoneyRequest;
import com.example.ewalletdemo.transaction.response.DepositMoneyResponse;
import com.example.ewalletdemo.transaction.response.WithdrawMoneyResponse;
import com.example.ewalletdemo.transaction.util.TransactionValidator;
import com.example.ewalletdemo.user.constants.ResponseCodes;
import com.example.ewalletdemo.user.exception.FinalException;
import com.example.ewalletdemo.user.request.WithdrawMoneyRequest;
import com.example.ewalletdemo.wallet.model.Wallet;
import com.example.ewalletdemo.wallet.service.WalletService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
@ExtendWith(SpringExtension.class)
class TransactionServiceTest {

    @MockBean
    private TransactionRepository transactionRepository;

    @MockBean
    private WalletService walletService;

    @MockBean
    private TransactionValidator transactionValidator;

    @InjectMocks
    private TransactionService transactionService;

    @Test
    void shouldPassSuccessfully_depositMoneyToUserWallet() throws FinalException {
        String receiverId = "aman123";
        BigDecimal amount = new BigDecimal(10.0);
        String transactionId = UUID.randomUUID().toString();
        DepositMoneyRequest depositMoneyRequest = new DepositMoneyRequest(receiverId, amount);
        Timestamp currentTimeStamp = new Timestamp(System.currentTimeMillis());
        Transaction expectedTransaction = Transaction.builder()
                .transactionId(transactionId)
                .sender(TransactionConstants.DEFAULT_SENDER)
                .receiver(depositMoneyRequest.getReceiver())
                .purpose(TransactionConstants.DEFAULT_DEPOSIT_PURPOSE)
                .status(TransactionStatus.PENDING.toString())
                .amount(depositMoneyRequest.getAmount())
                .transactionTime(String.valueOf(currentTimeStamp.getTime()))
                .build();

        Wallet receiverWallet = new Wallet(1, receiverId, new BigDecimal(3.0), currentTimeStamp.toString());

        Mockito.when(transactionRepository.save(expectedTransaction)).thenReturn(expectedTransaction);
        Mockito.when(walletService.fetchWalletForUser(receiverId)).thenReturn(receiverWallet);
        Mockito.when(walletService.updateWallet(receiverWallet)).thenReturn(receiverWallet);
        Mockito.when(transactionRepository.findTransactionByTransactionId(Mockito.any(String.class)))
                .thenReturn(expectedTransaction);
        expectedTransaction.setStatus(TransactionStatus.SUCCESS.toString());
        Mockito.when(transactionRepository.save(expectedTransaction)).thenReturn(expectedTransaction);

        DepositMoneyResponse expectedResponse = expectedTransaction.buildResponseForDeposit();
        DepositMoneyResponse actualResponse = transactionService.depositMoneyToUserWallet(depositMoneyRequest);

        assertEquals(expectedResponse.getTransactionId(), actualResponse.getTransactionId());
        assertEquals(expectedResponse.getStatus(), actualResponse.getStatus());
        assertEquals(expectedResponse.getReceiver(), actualResponse.getReceiver());
        assertEquals(expectedResponse.getAmount(), actualResponse.getAmount());
    }

    @Test
    void shouldPassSuccessfully_withdrawMoneyToUserWallet() throws FinalException {
        String senderUserId = "john345";
        String receiverUserId = "aman123";
        BigDecimal amount = new BigDecimal(10.0);
        String purpose = "rent";
        Timestamp currentTimestamp = new Timestamp(System.currentTimeMillis());

        WithdrawMoneyRequest withdrawMoneyRequest = new WithdrawMoneyRequest(senderUserId, receiverUserId, amount, purpose);
        Wallet senderWallet = new Wallet(1, senderUserId, new BigDecimal(15.0), String.valueOf(currentTimestamp.getTime()));
        Transaction expectedTransaction = Transaction.builder()
                .transactionId(UUID.randomUUID().toString())
                .sender(withdrawMoneyRequest.getSender())
                .receiver(withdrawMoneyRequest.getReceiver())
                .purpose(withdrawMoneyRequest.getPurpose())
                .status(TransactionStatus.SUCCESS.toString())
                .amount(withdrawMoneyRequest.getAmount())
                .transactionTime(String.valueOf(currentTimestamp.getTime()))
                .build();
        Wallet receiverWallet = new Wallet(2, receiverUserId, new BigDecimal(17.0), String.valueOf(currentTimestamp.getTime()));
        WithdrawMoneyResponse expectedResponse = expectedTransaction.buildResponseForWithdraw();

        Mockito.when(walletService.fetchWalletForUser(senderUserId)).thenReturn(senderWallet);
        Mockito.when(transactionRepository.save(expectedTransaction)).thenReturn(expectedTransaction);
        Mockito.when(walletService.fetchWalletForUser(withdrawMoneyRequest.getReceiver())).thenReturn(receiverWallet);
        Mockito.when(walletService.updateWallet(receiverWallet)).thenReturn(receiverWallet);
        Mockito.when(walletService.updateWallet(senderWallet)).thenReturn(senderWallet);
        Mockito.when(transactionRepository.findTransactionByTransactionId(Mockito.any(String.class))).thenReturn(expectedTransaction);

        WithdrawMoneyResponse actualResponse = transactionService.withdrawMoneyToUserWallet(withdrawMoneyRequest);
        assertEquals(expectedResponse.getTransactionId(), actualResponse.getTransactionId());
        assertEquals(expectedResponse.getSender(), actualResponse.getSender());
        assertEquals(expectedResponse.getReceiver(), actualResponse.getReceiver());
        assertEquals(expectedResponse.getStatus(), actualResponse.getStatus());
        assertEquals(expectedResponse.getPurpose(), actualResponse.getPurpose());
        assertEquals(expectedResponse.getAmount(), actualResponse.getAmount());
    }

    @Test
    void shouldThrowExceptionWhenWithdrawingMoreThanAvailable_withdrawMoneyToUserWallet() throws FinalException {

        String senderUserId = "john345";
        String receiverUserId = "aman123";
        BigDecimal amount = new BigDecimal(30.0);
        String purpose = "rent";
        Timestamp currentTimestamp = new Timestamp(System.currentTimeMillis());

        WithdrawMoneyRequest withdrawMoneyRequest = new WithdrawMoneyRequest(senderUserId, receiverUserId, amount, purpose);
        Wallet senderWallet = new Wallet(1, senderUserId, new BigDecimal(15.0), String.valueOf(currentTimestamp.getTime()));
        Mockito.when(walletService.fetchWalletForUser(senderUserId)).thenReturn(senderWallet);
        FinalException finalException = assertThrows(FinalException.class, () -> transactionService.withdrawMoneyToUserWallet(withdrawMoneyRequest));

        int expectedErrorCode = ResponseCodes.GENERIC_ERROR;
        String expectedErrorMessage = "Insufficient Balance to perform transaction";
        assertEquals(expectedErrorCode, finalException.getCode());
        assertEquals(expectedErrorMessage, finalException.getMessage());
    }

    @Test
    void shouldThrowExceptionWhenAmountZeroOrLess_withdrawMoneyFromSelfWallet() {
        String senderUserId = "john345";
        String receiverUserId = "aman123";
        BigDecimal amount = new BigDecimal(0.0);
        String purpose = "rent";

        WithdrawMoneyRequest withdrawMoneyRequest = new WithdrawMoneyRequest(senderUserId, receiverUserId, amount, purpose);

        FinalException finalException = assertThrows(FinalException.class, () -> transactionService.withdrawMoneyFromSelfWallet(withdrawMoneyRequest));

        int expectedErrorCode = HttpStatus.BAD_REQUEST.value();
        String expectedErrorMessage = "Amount should be greater than 0";
        assertEquals(expectedErrorCode, finalException.getCode());
        assertEquals(expectedErrorMessage, finalException.getMessage());
    }

    @Test
    void shouldThrowExceptionWhenAmountGreaterThanBalance_withdrawMoneyFromSelfWallet() throws FinalException {
        String receiverUserId = "aman123";
        String senderUserId = receiverUserId;
        BigDecimal amount = new BigDecimal(30.0);
        String purpose = "rent";
        Timestamp currentTimestamp = new Timestamp(System.currentTimeMillis());
        String transactionId = UUID.randomUUID().toString();
        WithdrawMoneyRequest withdrawMoneyRequest = new WithdrawMoneyRequest(senderUserId, receiverUserId, amount, purpose);
        Transaction expectedTransaction = Transaction.builder()
                .transactionId(transactionId)
                .receiver(withdrawMoneyRequest.getReceiver())
                .purpose(withdrawMoneyRequest.getPurpose() == null || withdrawMoneyRequest.getPurpose().isEmpty() ? TransactionConstants.SELF_WITHDRAWAL_MONEY_PURPOSE : withdrawMoneyRequest.getPurpose())
                .status(TransactionStatus.SUCCESS.toString())
                .amount(withdrawMoneyRequest.getAmount())
                .transactionTime(String.valueOf(currentTimestamp.getTime()))
                .build();

        Wallet receiverWallet = new Wallet(1, receiverUserId, new BigDecimal(10.0), String.valueOf(currentTimestamp.getTime()));
        WithdrawMoneyResponse expectedResponse = expectedTransaction.buildResponseForWithdraw();

        Mockito.when(transactionRepository.save(expectedTransaction)).thenReturn(expectedTransaction);
        Mockito.when(walletService.fetchWalletForUser(withdrawMoneyRequest.getReceiver())).thenReturn(receiverWallet);
        Mockito.when(walletService.updateWallet(receiverWallet)).thenReturn(receiverWallet);
        Mockito.when(transactionRepository.findTransactionByTransactionId(Mockito.any(String.class))).thenReturn(expectedTransaction);

        FinalException finalException = assertThrows(FinalException.class, () -> transactionService.withdrawMoneyFromSelfWallet(withdrawMoneyRequest));

        int expectedErrorCode = ResponseCodes.GENERIC_ERROR;
        String expectedErrorMessage = "Insufficient Balance to perform transaction";

        assertEquals(expectedErrorCode, finalException.getCode());
        assertEquals(expectedErrorMessage, finalException.getMessage());
    }

    @Test
    public void shouldPassSuccessfully_withdrawMoneyFromSelfWallet() throws FinalException {
        String receiverUserId = "aman123";
        String senderUserId = receiverUserId;
        BigDecimal amount = new BigDecimal(30.0);
        String purpose = "rent";
        Timestamp currentTimestamp = new Timestamp(System.currentTimeMillis());
        String transactionId = UUID.randomUUID().toString();
        WithdrawMoneyRequest withdrawMoneyRequest = new WithdrawMoneyRequest(senderUserId, receiverUserId, amount, purpose);
        Transaction expectedTransaction = Transaction.builder()
                .transactionId(transactionId)
                .receiver(withdrawMoneyRequest.getReceiver())
                .purpose(withdrawMoneyRequest.getPurpose() == null || withdrawMoneyRequest.getPurpose().isEmpty() ? TransactionConstants.SELF_WITHDRAWAL_MONEY_PURPOSE : withdrawMoneyRequest.getPurpose())
                .status(TransactionStatus.SUCCESS.toString())
                .amount(withdrawMoneyRequest.getAmount())
                .transactionTime(String.valueOf(currentTimestamp.getTime()))
                .build();

        Wallet receiverWallet = new Wallet(1, receiverUserId, new BigDecimal(40.0), String.valueOf(currentTimestamp.getTime()));
        WithdrawMoneyResponse expectedResponse = expectedTransaction.buildResponseForWithdraw();

        Mockito.when(transactionRepository.save(expectedTransaction)).thenReturn(expectedTransaction);
        Mockito.when(walletService.fetchWalletForUser(withdrawMoneyRequest.getReceiver())).thenReturn(receiverWallet);
        Mockito.when(walletService.updateWallet(receiverWallet)).thenReturn(receiverWallet);
        Mockito.when(transactionRepository.findTransactionByTransactionId(Mockito.any(String.class))).thenReturn(expectedTransaction);

        WithdrawMoneyResponse actualResponse = transactionService.withdrawMoneyFromSelfWallet(withdrawMoneyRequest);
        assertEquals(expectedResponse.getTransactionId(), actualResponse.getTransactionId());
        assertEquals(expectedResponse.getSender(), actualResponse.getSender());
        assertEquals(expectedResponse.getReceiver(), actualResponse.getReceiver());
        assertEquals(expectedResponse.getStatus(), actualResponse.getStatus());
        assertEquals(expectedResponse.getPurpose(), actualResponse.getPurpose());
        assertEquals(expectedResponse.getAmount(), actualResponse.getAmount());
    }

    @Test
    void shouldThrowExceptionReceiverWalletNull_withdrawMoneyFromSelfWallet() throws FinalException {
        String senderUserId = null;
        String receiverUserId = "aman123";
        BigDecimal amount = new BigDecimal(30.0);
        String purpose = "rent";
        Timestamp currentTimestamp = new Timestamp(System.currentTimeMillis());
        String transactionId = UUID.randomUUID().toString();
        WithdrawMoneyRequest withdrawMoneyRequest = new WithdrawMoneyRequest(senderUserId, receiverUserId, amount, purpose);
        Transaction expectedTransaction = Transaction.builder()
                .transactionId(transactionId)
                .receiver(withdrawMoneyRequest.getReceiver())
                .purpose(withdrawMoneyRequest.getPurpose() == null || withdrawMoneyRequest.getPurpose().isEmpty() ? TransactionConstants.SELF_WITHDRAWAL_MONEY_PURPOSE : withdrawMoneyRequest.getPurpose())
                .status(TransactionStatus.SUCCESS.toString())
                .amount(withdrawMoneyRequest.getAmount())
                .transactionTime(String.valueOf(currentTimestamp.getTime()))
                .build();

        Wallet receiverWallet = new Wallet(1, receiverUserId, new BigDecimal(10.0), String.valueOf(currentTimestamp.getTime()));
        WithdrawMoneyResponse expectedResponse = expectedTransaction.buildResponseForWithdraw();

        Mockito.when(transactionRepository.save(expectedTransaction)).thenReturn(expectedTransaction);
        Mockito.when(walletService.fetchWalletForUser(withdrawMoneyRequest.getReceiver())).thenReturn(null);

        FinalException finalException = assertThrows(FinalException.class, () -> transactionService.withdrawMoneyFromSelfWallet(withdrawMoneyRequest));

        int expectedErrorCode = HttpStatus.INTERNAL_SERVER_ERROR.value();
        String expectedErrorMessage = "invalid receiver id";
        assertEquals(expectedErrorCode, finalException.getCode());
        assertEquals(expectedErrorMessage, finalException.getMessage());

    }
}
