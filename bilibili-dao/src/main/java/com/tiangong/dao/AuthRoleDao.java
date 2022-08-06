package com.tiangong.dao;

import com.tiangong.domain.auth.AuthRole;
import org.apache.ibatis.annotations.Mapper;

/**
 * @BelongsProject: bilibili
 * @BelongsPackage: com.tiangong.dao
 * @Author: ChenLipeng
 * @CreateTime: 2022-06-13  13:46
 * @Description: t_auth_role表中的mapper映射
 * @Version: 1.0
 */
@Mapper
public interface AuthRoleDao {

    /**
    * @description: 根据角色代码查询角色信息
    * @author: ChenLipeng
    * @date: 2022/7/15 17:27
    * @param: code
    * @return: com.tiangong.domain.auth.AuthRole
    **/
    AuthRole getRoleByCode(String code);

}
