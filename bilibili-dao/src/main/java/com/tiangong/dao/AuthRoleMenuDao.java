package com.tiangong.dao;

import com.tiangong.domain.auth.AuthRoleMenu;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Set;

/**
 * @BelongsProject: bilibili
 * @BelongsPackage: com.tiangong.dao
 * @Author: ChenLipeng
 * @CreateTime: 2022-06-13  13:46
 * @Description: t_auth_menu表中的mapper映射
 * @Version: 1.0
 */
@Mapper
public interface AuthRoleMenuDao {

    /**
    * @description: 通过角色组查询该角色组所拥有的所有页面权限
    * @author: ChenLipeng
    * @date: 2022/7/15 17:31
    * @param: roleIdSet
    * @return: java.util.List<com.tiangong.domain.auth.AuthRoleMenu>
    **/
    List<AuthRoleMenu> getAuthRoleMeansByRoleIds(@Param("roleIdSet") Set<Long> roleIdSet);

}
