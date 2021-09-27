package com.springday2.demo.service.impl;


import com.springday2.demo.dao.AccountDao;
import com.springday2.demo.entity.Account;
import com.springday2.demo.service.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


@Service
@Transactional
public class AccountServiceImpl implements AccountService {

    @Autowired
    public AccountDao accountDao;

    @Override
    @Transactional(rollbackFor =Exception.class )
    public Integer save(Account account) {
        System.out.println("Service插入用户信息");
        return  accountDao.insert(account);
    }

    @Override
    @Transactional(rollbackFor =Exception.class )
    public Integer delete(Integer id) {
        Integer integer = accountDao.deleteByCardId(id);
        System.out.println("Service删除用户信息");
        return integer;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Integer updateBalance(Integer fromCardId,Integer toCardId,double money) {
        Account fromCardUser = accountDao.findByCardId(fromCardId);
        Account toCardIdUser = accountDao.findByCardId(toCardId);
            if (fromCardUser!=null&&toCardIdUser!=null&&fromCardUser!=toCardIdUser){
                if (fromCardUser.getAccountBalance()>=money){
                    double fromCardUserBalance=fromCardUser.getAccountBalance()-money;
                    double toCardUserBalance=toCardIdUser.getAccountBalance()+money;
                    accountDao.updateBalance(fromCardId,fromCardUserBalance);
                    accountDao.updateBalance(toCardId,toCardUserBalance);
                    System.out.println("Service更新用户信息");
                    return 1;
                }else{
                    throw new RuntimeException("余额不足");
                }
            }else{
                throw new RuntimeException("账户异常");
            }
    }

    @Override
    public Account select(Integer id) {
        Account account = accountDao.findByCardId(id);
        System.out.println("Service查询用户信息");
        return account;
    }

    @Override
    public List<Account> selectAll() {
        List<Account> all = accountDao.findAll();
        System.out.println("Service查询所有用户信息");
        return all;
    }

    @Override
    public Integer updatePersonalInfo(Account account) {
        return  accountDao.updatePersonalInfo(account);
    }

    @Override
    public Integer updateAllInfos(Account account) {
        return accountDao.updateAllInfos(account);
    }


}
