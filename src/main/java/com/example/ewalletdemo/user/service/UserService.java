package com.example.ewalletdemo.user.service;

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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final Logger logger = LoggerFactory.getLogger(UserService.class);

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private WalletService walletService;

    @Autowired
    private TransactionService transactionService;

    @Autowired
    private UserValidator userValidator;

    /*
     * Create wallet for the User.
     * */
    public UserResponse createUserWallet(UserRequest userRequest) throws FinalException {
        logger.debug("Started UserService :: createUserWallet ");
        try {
            userValidator.userRequestAllFieldsValidator(userRequest);

            // Create user
            WalletUser tempWalletUser = userRequest.buildUserFromUserRequest();

            tempWalletUser = userRepository.save(tempWalletUser);

            // Create a new wallet with default balance.
            Wallet tempWallet = walletService.createDefaultWallet(tempWalletUser.getUserId());

            // assign wallet to user.
            if (tempWallet != null) {
                tempWalletUser.setWallet(tempWallet);
                tempWalletUser = userRepository.save(tempWalletUser);
                logger.debug("Completed UserService :: createUserWallet ");
                return tempWalletUser.buildUserResponseFromUserWithWallet();
            } else {
                logger.debug("Completed UserService :: createUserWallet ");
                throw new FinalException(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Wallet could not be created.");
            }
        } catch (FinalException e) {
            throw new FinalException(e.getCode(), e.getMessage());
        }

    }


    /*
     * Return User info along with Wallet details.
     * */
    public UserResponse getUserWithWalletBalance(String userId) throws FinalException {
        logger.debug("Started UserService :: getUserWithWalletBalance");
        try {
            userValidator.userIdValidator(userId);

            Wallet userWallet = walletService.fetchWalletForUser(userId);

            if (userWallet == null) {
                logger.error("wallet for user-id : {} cannot be found.", userId);
                throw new FinalException(ResponseCodes.GENERIC_ERROR, ResponseCodes.GENERIC_ERROR_MESSAGE);
            }

            WalletUser tempUser = userRepository.findUserByUserId(userId);
            if (tempUser != null) {
                tempUser.setWallet(userWallet);
                logger.debug("Completed UserService :: getUserWithWalletBalance");
                return tempUser.buildUserResponseFromUserWithWallet();
            } else {
                logger.error("user with user-id : {} cannot be found", userId);
                throw new FinalException(ResponseCodes.GENERIC_ERROR, ResponseCodes.GENERIC_ERROR_MESSAGE);
            }

        } catch (FinalException ex) {
            throw new FinalException(ex.getCode(), ex.getMessage());
        }
    }


    /*
     * Deposit money in the user wallet.
     * */
    public DepositMoneyResponse depositMoneyToUserWallet(DepositMoneyRequest depositMoneyRequest) throws FinalException {
        logger.debug("Started UserService :: depositMoneyToUserWallet");
        //checks if the request is valid or not, if yes then call the transactionService.
        userValidator.depositMoneyAllFieldsValidator(depositMoneyRequest);
        WalletUser retrievedUser = userRepository.findUserByUserId(depositMoneyRequest.getReceiver());

        // user is valid
        if (retrievedUser != null) {
            return transactionService.depositMoneyToUserWallet(depositMoneyRequest);
        } else {
            throw new FinalException(HttpStatus.NOT_FOUND.value(), "User not found");
        }

    }

    /*
     * Send money from sender's to receiver's wallet.
     * */
    public WithdrawMoneyResponse withdrawMoneyToUserWallet(WithdrawMoneyRequest withdrawMoneyRequest) throws FinalException {
        logger.debug("Started UserService :: withdrawMoneyToUserWallet");
        //checks if the request is valid or not, if yes then call the transactionService.
        userValidator.withdrawMoneyRequestAllFieldsValidator(withdrawMoneyRequest);

        WalletUser receiver = userRepository.findUserByUserId(withdrawMoneyRequest.getReceiver());
        WalletUser sender = userRepository.findUserByUserId(withdrawMoneyRequest.getSender());
        if (sender != null && receiver != null)
            return transactionService.withdrawMoneyToUserWallet(withdrawMoneyRequest);
        else {
            // throw exception : sender or receiver is not found.
            throw new FinalException(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Sender or Receiver not found.");
        }
    }

    /*
     * Withdraw money from self wallet.
     * */
    public WithdrawMoneyResponse withdrawMoneyFromSelfWallet(WithdrawMoneyRequest withdrawMoneyRequest) throws FinalException {
        logger.debug("Started UserService :: withdrawMoneyFromSelfWallet");
        //checks if the request is valid or not, if yes then call the transactionService.
        userValidator.withdrawMoneyRequestPartialValidator(withdrawMoneyRequest);
        WalletUser receiver = userRepository.findUserByUserId(withdrawMoneyRequest.getReceiver());

        userValidator.userAccountValidator(receiver, withdrawMoneyRequest.getReceiver());
        return transactionService.withdrawMoneyFromSelfWallet(withdrawMoneyRequest);
    }

}
