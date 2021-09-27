package com.springday2.demo.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * @author Cjl
 * @date 2021/7/21 11:46
 */

@Data
@NoArgsConstructor
@AllArgsConstructor
@Component
public class Account {
    private Integer cardId;
    private String username;
    private String accountPassword;
    private double accountBalance;
    private Integer userId;
    private String img;
    private Integer isAdmin;


}
