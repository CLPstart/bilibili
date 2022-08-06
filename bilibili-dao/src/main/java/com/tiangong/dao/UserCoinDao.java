package com.tiangong.dao;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Date;

/**
 * @BelongsProject: bilibili
 * @BelongsPackage: com.tiangong.dao
 * @Author: ChenLipeng
 * @CreateTime: 2022-07-18  11:01
 * @Description: t_user_coin表的mapper映射
 * @Version: 1.0
 */
@Mapper
public interface UserCoinDao {

    /**
    * @description: 获取用户拥有的硬币总数
    * @author: ChenLipeng
    * @date: 2022/7/18 11:04
    * @param: userId
    * @return: java.lang.Integer
    **/
    Integer getUserCoinsAmount(Long userId);

    /**
    * @description: 更新用户拥有的硬币数量
    * @author: ChenLipeng
    * @date: 2022/7/18 11:40
    * @param: userId
    * @param: amount
    * @return: java.lang.Integer
    **/
    Integer updateUserCoinsAmount(@Param("userId") Long userId,
                                  @Param("amount") Integer amount,
                                  @Param("updateTime") Date updateTime);
}
