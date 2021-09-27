package com.springday2.demo.service;


import com.springday2.demo.entity.Account;
import java.util.List;


public interface AccountService {
    Integer save(Account account);

    Integer delete(Integer id);

    Integer updateBalance(Integer fromCardId, Integer toCardId, double money);

    Account select(Integer id);

    List<Account> selectAll();

    Integer updatePersonalInfo(Account account);

    Integer updateAllInfos(Account account);
}
