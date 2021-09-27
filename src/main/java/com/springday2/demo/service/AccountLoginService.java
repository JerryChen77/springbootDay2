package com.springday2.demo.service;
import com.springday2.demo.entity.ResultVO;

/**
 * @author Cjl
 * @date 2021/7/13 20:29
 */
public interface AccountLoginService {
    ResultVO login(String username, String password);
}
