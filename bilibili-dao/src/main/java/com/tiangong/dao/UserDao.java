package com.tiangong.dao;

import com.tiangong.domain.RefreshTokenDetail;
import com.tiangong.domain.User;
import com.tiangong.domain.UserInfo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @BelongsProject: bilibili
 * @BelongsPackage: com.tiangong.dao
 * @Author: ChenLipeng
 * @CreateTime: 2022-06-13  13:46
 * @Description: User表的Mapper映射
 * @Version: 1.0
 */
@Mapper
public interface UserDao {

    /**
    * @description: 通过用户手机号从数据库中查询用户对象
    * @author: ChenLipeng
    * @date: 2022/6/14 9:57
    * @param: phone
    * @return: com.tiangong.domain.User
    **/
    User getUserByPhone(String phone);

    /**
    * @description: 向数据库t_user表中添加信息
    * @author: ChenLipeng
    * @date: 2022/6/14 9:57
    * @param: user
    * @return: java.lang.Integer
    **/
    Integer addUser(User user);

    /**
    * @description: 向数据库t_userInfo表中添加信息
    * @author: ChenLipeng 
    * @date: 2022/6/14 9:58
    * @param: userInfo
    * @return: java.lang.Integer
    **/
    Integer addUserInfo(UserInfo userInfo);

    /**
    * @description: 通过id获取用户信息
    * @author: ChenLipeng
    * @date: 2022/6/14 14:43
    * @param: userId
    * @return: com.tiangong.domain.User
    **/
    User getUserById(Long userId);

    /**
    * @description: 通过id获取用户详细信息
    * @author: ChenLipeng
    * @date: 2022/6/14 14:43
    * @param: userId
    * @return: com.tiangong.domain.UserInfo
    **/
    UserInfo getUserInfoById(Long userId);

    /**
    * @description: 更新用户信息
    * @author: ChenLipeng
    * @date: 2022/6/14 19:10
    * @param: user
    * @return: java.lang.Integer
    **/
    Integer updateUsers(User user);

    /**
    * @description: 根据用户邮箱或手机号查询用户信息
    * @author: ChenLipeng
    * @date: 2022/6/14 19:10
    * @param: phone
    * @param: email
    * @return: com.tiangong.domain.User
    **/
    User getUserByPhoneOrEmail(@Param("phone") String phone, @Param("email") String email);

    /**
    * @description: 更新用户详细信息
    * @author: ChenLipeng 
    * @date: 2022/6/14 19:11 
    * @param: userInfo
    **/
    void updateUserInfos(UserInfo userInfo);

    /**
    * @description: 通过用户id的集合获得所有的用户信息集合
    * @author: ChenLipeng
    * @date: 2022/6/16 11:18
    * @param: userIdList
    * @return: java.util.List<com.tiangong.domain.UserInfo>
    **/
    List<UserInfo> getUserInfoByUserIds(Set<Long> userIdList);

    /**
    * @description: 查询数据表中符合条件的数据的总数
    * @author: ChenLipeng
    * @date: 2022/6/18 11:02
    * @param: params
    * @return: java.lang.Integer
    **/
    Integer pageCountUserInfo(Map<String, Object> params);

    /**
    * @description: 分页查询数据
    * @author: ChenLipeng
    * @date: 2022/6/18 11:02
    * @param: params
    * @return: java.util.List<com.tiangong.domain.UserInfo>
    **/
    List<UserInfo> pageListUserInfos(Map<String, Object> params);

    /**
    * @description: 删除刷新token
    * @author: ChenLipeng
    * @date: 2022/7/15 17:32
    * @param: refreshToken
    * @param: userId
    * @return: java.lang.Integer
    **/
    Integer deleteRefreshToken(@Param("refreshToken") String refreshToken,
                               @Param("userId") Long userId);

    /**
    * @description: 添加刷新token
    * @author: ChenLipeng
    * @date: 2022/7/15 17:33
    * @param: refreshToken
    * @param: userId
    * @param: createTime
    * @return: java.lang.Integer
    **/
    Integer addRefreshToken(@Param("refreshToken") String refreshToken,
                            @Param("userId") Long userId,
                            @Param("createTime") Date createTime);

    /**
    * @description: 得到刷新token的细节
    * @author: ChenLipeng
    * @date: 2022/7/15 17:33
    * @param: refreshToken
    * @return: com.tiangong.domain.RefreshTokenDetail
    **/
    RefreshTokenDetail getRefreshTokenDetail(String refreshToken);

    /**
    * @description: 批量查询用户详情
    * @author: ChenLipeng
    * @date: 2022/7/18 13:42
    * @param: userIdList
    * @return: java.util.List<com.tiangong.domain.UserInfo>
    **/
    List<UserInfo> batchGetUserInfoByUserIds(Set<Long> userIdList);

}
