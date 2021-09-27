package com.springday2.demo.service.impl;


import com.springday2.demo.dao.AccountDao;
import com.springday2.demo.entity.Account;
import com.springday2.demo.entity.ResultVO;
import com.springday2.demo.service.AccountLoginService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author Cjl
 * @date 2021/7/13 20:30
 */
@Service
public class AccountLoginServiceImpl implements AccountLoginService {
    @Autowired
    ResultVO resultVO;
    @Autowired
    AccountDao accountDao;
    @Override
    public ResultVO login(String username, String password) {
//        ClassPathXmlApplicationContext ioc = new ClassPathXmlApplicationContext("applicationContext.xml");
//        ResultVO resultVO = ioc.getBean(ResultVO.class);

        Account byUsername = accountDao.findByUsername(username);
        if (byUsername==null){
            resultVO.setSuccess(false);
            resultVO.setMessage("用户不存在，请先注册");
            return resultVO;
        }else if (byUsername.getAccountPassword().equals(password)){
            resultVO.setSuccess(true);
            resultVO.setMessage("用户名密码正确！！！");
            resultVO.setData(byUsername);
            return resultVO;
        }else{
            resultVO.setSuccess(false);
            resultVO.setMessage("用户名密码错误！！！");
            return resultVO;
        }
    }
}
