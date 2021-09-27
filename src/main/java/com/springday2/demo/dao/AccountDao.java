package com.springday2.demo.dao;

import com.springday2.demo.entity.Account;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author Cjl
 * @date 2021/7/21 12:28
 */
public interface AccountDao {
    Integer insert(Account account);

    Integer deleteByCardId(Integer cardId);

    Integer updateBalance(@Param("cardId") Integer cardId, @Param("balance") double balance);

    Integer updatePersonalInfo(Account account);

    Integer updateAllInfos(Account account);

    Account findByCardId(Integer cardId);

    List<Account> findAll();

    Account findByUsername(String username);
}
