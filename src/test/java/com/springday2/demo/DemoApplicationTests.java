package com.springday2.demo;

import com.springday2.demo.dao.AccountDao;
import com.springday2.demo.entity.Account;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest
class DemoApplicationTests {

    @Autowired
    private AccountDao accountDao;

    @Test
    void contextLoads() {
        Account byUsername = accountDao.findByUsername("拉克丝");
        System.out.println("byUsername = " + byUsername);

        Account byCardId = accountDao.findByCardId(1);
        System.out.println("byCardId = " + byCardId);

    }

}
