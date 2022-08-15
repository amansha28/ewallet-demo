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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.sql.Timestamp;
import java.util.UUID;

@Service
public class TransactionService {

    private final Logger logger = LoggerFactory.getLogger(TransactionService.class);
    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private WalletService walletService;

    @Autowired
    private TransactionValidator transactionValidator;

    /*
     * Assuming the caller will check for the request Object before calling depositMoneyToUserWallet()
     * Deposit money to the corresponding user wallet.
     * */
    @Transactional
    public DepositMoneyResponse depositMoneyToUserWallet(DepositMoneyRequest depositMoneyRequest) throws FinalException {
        logger.debug("Started TransactionService :: depositMoneyToUserWallet");
        transactionValidator.depositMoneyAllFieldsValidator(depositMoneyRequest);

        Timestamp currentTimeStamp = new Timestamp(System.currentTimeMillis());
        // create initial transaction with pending state.
        Transaction transaction = Transaction.builder()
                .transactionId(UUID.randomUUID().toString())
                .sender(TransactionConstants.DEFAULT_SENDER)
                .receiver(depositMoneyRequest.getReceiver())
                .purpose(TransactionConstants.DEFAULT_DEPOSIT_PURPOSE)
                .status(TransactionStatus.PENDING.toString())
                .amount(depositMoneyRequest.getAmount())
                .transactionTime(String.valueOf(currentTimeStamp.getTime()))
                .build();
        transactionRepository.save(transaction);

        // retrieve wallet + add money to wallet + save in db.
        Wallet receiverWallet = walletService.fetchWalletForUser(depositMoneyRequest.getReceiver());
        receiverWallet.setBalance(receiverWallet.getBalance().add(depositMoneyRequest.getAmount()));
        walletService.updateWallet(receiverWallet);

        // update the status of transaction.
        Transaction updateSavedTransaction = transactionRepository.findTransactionByTransactionId(transaction.getTransactionId());
        updateSavedTransaction.setStatus(TransactionStatus.SUCCESS.toString());
        Transaction updateTransaction = transactionRepository.save(updateSavedTransaction);
        logger.debug("Completed TransactionService :: depositMoneyToUserWallet");
        return updateTransaction.buildResponseForDeposit();
    }


    /*
     * Assuming the caller will check for the request Object before calling depositMoneyToUserWallet()
     * Transaction method for transferring money from sender to receiver's wallet.
     * */
    @Transactional
    public WithdrawMoneyResponse withdrawMoneyToUserWallet(WithdrawMoneyRequest withdrawMoneyRequest) throws FinalException {
        logger.debug("Started TransactionService :: withdrawMoneyToUserWallet");
        transactionValidator.withdrawMoneyRequestAllFieldsValidator(withdrawMoneyRequest);

        // sender should have enough balance in the account to withdraw
        String senderUserId = withdrawMoneyRequest.getSender();
        Wallet senderWallet = walletService.fetchWalletForUser(senderUserId);

        transactionValidator.isValidWallet(senderWallet, senderUserId);

        if (senderWallet.getBalance().subtract(withdrawMoneyRequest.getAmount()).doubleValue() < 0) {
            //throw exception if Insufficient funds to transfer.
            logger.error("Insufficient Balance in wallet of user-id : {} to perform transaction of amount : {}", senderUserId, withdrawMoneyRequest.getAmount().doubleValue());
            throw new FinalException(ResponseCodes.GENERIC_ERROR, "Insufficient Balance to perform transaction");
        }
        Timestamp currentTimeStamp = new Timestamp(System.currentTimeMillis());
        // create initial transaction with pending state.
        Transaction transaction = Transaction.builder()
                .transactionId(UUID.randomUUID().toString())
                .sender(withdrawMoneyRequest.getSender())
                .receiver(withdrawMoneyRequest.getReceiver())
                .purpose(withdrawMoneyRequest.getPurpose() == null || withdrawMoneyRequest.getPurpose().isEmpty() ? TransactionConstants.DEFAULT_TRANSFER_PURPOSE : withdrawMoneyRequest.getPurpose())
                .status(TransactionStatus.PENDING.toString())
                .amount(withdrawMoneyRequest.getAmount())
                .transactionTime(String.valueOf(currentTimeStamp.getTime()))
                .build();
        transactionRepository.save(transaction);

        // retrieve wallet of sender and receiver + credit/debit money to wallets + save in back in db.
        Wallet receiverWallet = walletService.fetchWalletForUser(withdrawMoneyRequest.getReceiver());

        transactionValidator.isValidWallet(receiverWallet, withdrawMoneyRequest.getReceiver());

        senderWallet.setBalance(senderWallet.getBalance().subtract(withdrawMoneyRequest.getAmount()));
        receiverWallet.setBalance(receiverWallet.getBalance().add(withdrawMoneyRequest.getAmount()));
        walletService.updateWallet(receiverWallet);
        walletService.updateWallet(senderWallet);

        logger.debug("Completed TransactionService :: withdrawMoneyToUserWallet");
        // update the status of transaction.
        return updateSavedTransaction(transaction.getTransactionId());
    }


    /*
     * To self withdraw money from wallet.
     * */
    @Transactional
    public WithdrawMoneyResponse withdrawMoneyFromSelfWallet(WithdrawMoneyRequest withdrawMoneyRequest) throws FinalException {
        logger.debug("Started TransactionService :: withdrawMoneyFromSelfWallet");
        if (withdrawMoneyRequest.getAmount().doubleValue() > 0) {

            Timestamp currentTimeStamp = new Timestamp(System.currentTimeMillis());
            // create initial transaction with pending state.
            Transaction transaction = Transaction.builder()
                    .transactionId(UUID.randomUUID().toString())
                    .sender(withdrawMoneyRequest.getReceiver())
                    .receiver(withdrawMoneyRequest.getReceiver())
                    .purpose(withdrawMoneyRequest.getPurpose() == null || withdrawMoneyRequest.getPurpose().isEmpty() ? TransactionConstants.SELF_WITHDRAWAL_MONEY_PURPOSE : withdrawMoneyRequest.getPurpose())
                    .status(TransactionStatus.PENDING.toString())
                    .amount(withdrawMoneyRequest.getAmount())
                    .transactionTime(String.valueOf(currentTimeStamp.getTime()))
                    .build();
            transactionRepository.save(transaction);

            // retrieve wallet of receiver + debit money to wallets + save back in db.
            Wallet receiverWallet = walletService.fetchWalletForUser(withdrawMoneyRequest.getReceiver());
            if (receiverWallet == null) {
                throw new FinalException(HttpStatus.INTERNAL_SERVER_ERROR.value(), "invalid receiver id");
            }

            if (receiverWallet.getBalance().subtract(withdrawMoneyRequest.getAmount()).doubleValue() < 0) {
                //throw exception if Insufficient funds to transfer.
                logger.error("Insufficient Balance in wallet of user-id : {} to perform transaction of amount : {}", withdrawMoneyRequest.getReceiver(), withdrawMoneyRequest.getAmount().doubleValue());
                throw new FinalException(ResponseCodes.GENERIC_ERROR, "Insufficient Balance to perform transaction");
            } else {
                receiverWallet.setBalance(receiverWallet.getBalance().subtract(withdrawMoneyRequest.getAmount()));
                walletService.updateWallet(receiverWallet);

                logger.debug("Completed TransactionService :: withdrawMoneyFromSelfWallet");
                // update the status of initially created transaction.
                return updateSavedTransaction(transaction.getTransactionId());
            }
        }
        throw new FinalException(HttpStatus.BAD_REQUEST.value(), "Amount should be greater than 0");
    }

    public WithdrawMoneyResponse updateSavedTransaction(String transactionId) {
        logger.debug("Started TransactionService :: updateSavedTransaction");
        Transaction updateSavedTransaction = transactionRepository.findTransactionByTransactionId(transactionId);
        updateSavedTransaction.setStatus(TransactionStatus.SUCCESS.toString());
        Transaction updateTransaction = transactionRepository.save(updateSavedTransaction);
        logger.debug("Completed TransactionService :: updateSavedTransaction");
        return updateTransaction.buildResponseForWithdraw();
    }
}
