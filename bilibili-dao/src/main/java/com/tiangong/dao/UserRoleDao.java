package com.tiangong.dao;

import com.tiangong.domain.auth.UserRole;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * @BelongsProject: bilibili
 * @BelongsPackage: com.tiangong.dao
 * @Author: ChenLipeng
 * @CreateTime: 2022-07-12  19:16
 * @Description: t_user_role表的mapper映射
 * @Version: 1.0
 */
@Mapper
public interface UserRoleDao {

    /**
    * @description: 根据用户id获得用户所属的角色组
    * @author: ChenLipeng
    * @date: 2022/7/15 17:35
    * @param: userId
    * @return: java.util.List<com.tiangong.domain.auth.UserRole>
    **/
    List<UserRole> getUserRoleByUserId(Long userId);

    /**
    * @description: 添加角色
    * @author: ChenLipeng
    * @date: 2022/7/15 17:35
    * @param: userRole
    * @return: java.lang.Integer
    **/
    Integer addUserRole(UserRole userRole);

}
