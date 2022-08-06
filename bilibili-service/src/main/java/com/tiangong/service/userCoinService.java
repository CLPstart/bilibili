package com.tiangong.service;

import com.tiangong.dao.UserCoinDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
 * @BelongsProject: bilibili
 * @BelongsPackage: com.tiangong.service
 * @Author: ChenLipeng
 * @CreateTime: 2022-07-18  10:58
 * @Description: 用户硬币相关业务层
 * @Version: 1.0
 */
@Service
public class userCoinService {

    @Autowired
    private UserCoinDao userCoinDao;

    /**
    * @description: 获取用户拥有硬币数量的业务层方法
    * @author: ChenLipeng
    * @date: 2022/7/18 11:07
    * @param: userId
    * @return: java.lang.Integer
    **/
    public Integer getUserCoinsAmount(Long userId) {
        return userCoinDao.getUserCoinsAmount(userId);
    }

    /**
    * @description: 更新用户的硬币数量
    * @author: ChenLipeng
    * @date: 2022/7/18 11:38
    * @param: userId
    * @param: i
    **/
    public void updateUserCoinsAmount(Long userId, int amount) {
        userCoinDao.updateUserCoinsAmount(userId, amount, new Date());
    }
}
