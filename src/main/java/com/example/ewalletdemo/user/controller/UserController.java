package com.example.ewalletdemo.user.controller;

import com.example.ewalletdemo.transaction.request.DepositMoneyRequest;
import com.example.ewalletdemo.user.exception.FinalException;
import com.example.ewalletdemo.user.request.UserRequest;
import com.example.ewalletdemo.user.request.WithdrawMoneyRequest;
import com.example.ewalletdemo.user.service.UserService;
import com.example.ewalletdemo.user.util.ResponseBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/ewallet")
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping(value = "/register")
    public ResponseEntity<Object> createUserWallet(@RequestBody UserRequest userRequest) throws FinalException {
        return new ResponseEntity<>(ResponseBuilder.successResponse(userService.createUserWallet(userRequest)), HttpStatus.CREATED);
    }

    @GetMapping(value = "/balance")
    public ResponseEntity<Object> getUserWalletDetails(@RequestParam String userId) throws FinalException {
        return new ResponseEntity<>(ResponseBuilder.successResponse(userService.getUserWithWalletBalance(userId)), HttpStatus.OK);
    }

    @PutMapping(value = "/credit")
    public ResponseEntity<Object> depositMoneyToUserWallet(@RequestBody DepositMoneyRequest depositMoneyRequest) throws FinalException {
        return new ResponseEntity<>(userService.depositMoneyToUserWallet(depositMoneyRequest), HttpStatus.OK);
    }

    @PutMapping(value = "/transfer/money")
    public ResponseEntity<Object> withdrawMoneyToUserWallet(@RequestBody WithdrawMoneyRequest withdrawMoneyRequest) throws FinalException {
        return new ResponseEntity<>(userService.withdrawMoneyToUserWallet(withdrawMoneyRequest), HttpStatus.OK);
    }

    @PutMapping(value = "/withdraw/self")
    public ResponseEntity<Object> withdrawMoneyFromSelfWallet(@RequestBody WithdrawMoneyRequest withdrawMoneyRequest) throws FinalException {
        return new ResponseEntity<>(userService.withdrawMoneyFromSelfWallet(withdrawMoneyRequest), HttpStatus.OK);

    }
}
