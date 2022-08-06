package com.tiangong.dao;

import com.tiangong.domain.UserFollowing;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @BelongsProject: bilibili
 * @BelongsPackage: com.tiangong.dao
 * @Author: ChenLipeng
 * @CreateTime: 2022-06-13  13:46
 * @Description: t_user_following表中的mapper映射
 * @Version: 1.0
 */
@Mapper
public interface UserFollowingDao {
    /**
    * @description: 根据UserId和followingId删除t_user_following表中的一条数据
    * @author: ChenLipeng 
    * @date: 2022/6/15 14:18
    * @param: userId
    * @param: followingId
    * @return: java.lang.Integer
    **/
    Integer deleteUserFollowing(@Param("userId") Long userId, @Param("followingId") Long followingId);

    /**
    * @description: 在t_user_following表中添加一条数据
    * @author: ChenLipeng 
    * @date: 2022/6/15 14:19
    * @param: userFollowing
    **/
    void addUserFollowing(UserFollowing userFollowing);

    /**
    * @description: 根据用户Id获得他关注名单
    * @author: ChenLipeng
    * @date: 2022/6/16 10:51
    * @param: userId
    * @return: java.util.List<com.tiangong.domain.UserFollowing>
    **/
    List<UserFollowing> getUserFollowings(Long userId);

    /**
    * @description: 获取用户的粉丝
    * @author: ChenLipeng
    * @date: 2022/6/16 11:34
    * @param: userId
    * @return: java.util.List<com.tiangong.domain.UserFollowing>
    **/
    List<UserFollowing> getUserFans(Long userId);
}
