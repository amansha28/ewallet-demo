package com.example.ewalletdemo;

import com.example.ewalletdemo.user.controller.UserController;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;


@SpringBootTest
class EwalletDemoApplicationTests {
    @Autowired
    private UserController controller;


    /*
     * Simple Sanity test to check if application context can start.
     * */
    @Test
    void contextLoads() {
        assertThat(controller).isNotNull();
    }

}
