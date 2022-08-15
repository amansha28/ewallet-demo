package com.example.ewalletdemo.user.controller;


import com.example.ewalletdemo.transaction.model.TransactionStatus;
import com.example.ewalletdemo.transaction.request.DepositMoneyRequest;
import com.example.ewalletdemo.transaction.response.DepositMoneyResponse;
import com.example.ewalletdemo.transaction.response.WithdrawMoneyResponse;
import com.example.ewalletdemo.user.request.UserRequest;
import com.example.ewalletdemo.user.request.WithdrawMoneyRequest;
import com.example.ewalletdemo.user.response.UserResponse;
import com.example.ewalletdemo.user.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.Date;
import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = UserController.class)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UserResponse userResponse;

    @MockBean
    private UserRequest userRequest;

    @MockBean
    private DepositMoneyRequest depositMoneyRequest;

    @MockBean
    private DepositMoneyResponse depositMoneyResponse;

    @MockBean
    private WithdrawMoneyRequest withdrawMoneyRequest;

    @MockBean
    private WithdrawMoneyResponse withdrawMoneyResponse;

    @Test
    void getUserWalletDetailsTest() throws Exception {
        String userId = "aman123";
        userResponse = new UserResponse(userId, "aman@gmail.com", "Aman", 33, new BigDecimal(0.0));

        Mockito.when(userService.getUserWithWalletBalance(userId)).thenReturn(userResponse);

        String url = "/ewallet/balance";
        String expectedResult = "{\"code\":200,\"message\":\"Ok\",\"data\":{\"userId\":\"aman123\",\"emailId\":\"aman@gmail.com\",\"name\":\"Aman\",\"age\":33,\"walletBalance\":0}}";
        mockMvc.perform(get(url).param("userId", userId)).andExpect(status().isOk()).andExpect(content().string(expectedResult));
    }

    @Test
    void createUserWalletTest() throws Exception {
        String userId = "aman123";
        userRequest = new UserRequest(userId, "aman@gmail.com", "Aman", 33);
        userResponse = new UserResponse(userId, "aman@gmail.com", "Aman", 33, new BigDecimal(0.0));
        Mockito.when(userService.createUserWallet(Mockito.any(UserRequest.class))).thenReturn(userResponse);
        String url = "/ewallet/register";
        String obj = objectMapper.writeValueAsString(userRequest);
        String expectedResult = "{\"code\":200,\"message\":\"Ok\",\"data\":{\"userId\":\"aman123\",\"emailId\":\"aman@gmail.com\",\"name\":\"Aman\",\"age\":33,\"walletBalance\":0}}";
        mockMvc.perform(post(url)
                        .contentType("application/json")
                        .content(obj))
                .andExpect(status().isCreated())
                .andExpect(content().string(expectedResult));
    }

    @Test
    void depositMoneyToUserWalletTest() throws Exception {
        String receiverUserId = "aman123";
        depositMoneyRequest = new DepositMoneyRequest(receiverUserId, new BigDecimal(10.0));
        depositMoneyResponse = new DepositMoneyResponse(UUID.randomUUID().toString(), receiverUserId, TransactionStatus.SUCCESS.toString(), new BigDecimal(10.0));
        Mockito.when(userService.depositMoneyToUserWallet(Mockito.any(DepositMoneyRequest.class))).thenReturn(depositMoneyResponse);
        String url = "/ewallet/credit";
        String obj = objectMapper.writeValueAsString(depositMoneyRequest);
        mockMvc.perform(put(url)
                        .contentType("application/json")
                        .content(obj))
                .andExpect(status().isOk());
    }

    @Test
    void withdrawMoneyToUserWalletTest() throws Exception {
        String senderUserId = "john345";
        String receiverUserId = "aman123";
        String purpose = "rent";
        String transactionId = "f800a08d-44e0-4c89-867f-4f6e8f2fb7f9";
        withdrawMoneyRequest = new WithdrawMoneyRequest(senderUserId, receiverUserId, new BigDecimal(10.0), purpose);
        withdrawMoneyResponse = new WithdrawMoneyResponse(transactionId, senderUserId, receiverUserId, purpose, TransactionStatus.SUCCESS.toString(), new BigDecimal(10.0), new Date());
        Mockito.when(userService.withdrawMoneyToUserWallet(Mockito.any(WithdrawMoneyRequest.class))).thenReturn(withdrawMoneyResponse);
        String url = "/ewallet/transfer/money";
        String obj = objectMapper.writeValueAsString(withdrawMoneyRequest);
        mockMvc.perform(put(url)
                        .contentType("application/json")
                        .content(obj))
                .andExpect(status().isOk());
    }


    @Test
    void withdrawMoneyFromSelfWalletTest() throws Exception {
        String senderUserId = "";
        String receiverUserId = "aman123";
        String purpose = "rent";
        String transactionId = "f800a08d-44e0-4c89-867f-4f6e8f2fb7f9";
        withdrawMoneyRequest = new WithdrawMoneyRequest(senderUserId, receiverUserId, new BigDecimal(10.0), purpose);
        withdrawMoneyResponse = new WithdrawMoneyResponse(transactionId, senderUserId, receiverUserId, purpose, TransactionStatus.SUCCESS.toString(), new BigDecimal(10.0), new Date());
        Mockito.when(userService.withdrawMoneyFromSelfWallet(Mockito.any(WithdrawMoneyRequest.class))).thenReturn(withdrawMoneyResponse);
        String url = "/ewallet/withdraw/self";
        String obj = objectMapper.writeValueAsString(withdrawMoneyRequest);
        mockMvc.perform(put(url)
                        .contentType("application/json")
                        .content(obj))
                .andExpect(status().isOk());
    }
}
