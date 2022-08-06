package com.tiangong.dao;

import com.tiangong.domain.auth.AuthRoleElementOperation;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Set;

/**
 * @BelongsProject: bilibili
 * @BelongsPackage: com.tiangong.dao
 * @Author: ChenLipeng
 * @CreateTime: 2022-06-13  13:46
 * @Description: t_auth_role_element_operation表中的mapper映射
 * @Version: 1.0
 */
@Mapper
public interface AuthRoleElementOperationDao {

    /**
    * @description: 通过角色组查询该角色组拥有的所有页面元素权限
    * @author: ChenLipeng
    * @date: 2022/7/15 17:28
    * @param: roleIdSet
    * @return: java.util.List<com.tiangong.domain.auth.AuthRoleElementOperation>
    **/
    List<AuthRoleElementOperation> getRoleElementOperationsByRoleIds(@Param("roleIdSet") Set<Long> roleIdSet);
}
