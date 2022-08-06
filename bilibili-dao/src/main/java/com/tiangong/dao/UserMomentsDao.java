package com.tiangong.dao;

import com.tiangong.domain.UserMoment;
import org.apache.ibatis.annotations.Mapper;

/**
 * @BelongsProject: bilibili
 * @BelongsPackage: com.tiangong.dao
 * @Author: ChenLipeng
 * @CreateTime: 2022-06-19  11:22
 * @Description: t_user_moment表的mapper映射
 * @Version: 1.0
 */
@Mapper
public interface UserMomentsDao {

    /**
    * @description: 添加用户信息
    * @author: ChenLipeng
    * @date: 2022/6/19 11:53
    * @param: userMoment
    **/
    void addUserMoments(UserMoment userMoment);
}
