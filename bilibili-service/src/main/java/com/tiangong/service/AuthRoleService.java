package com.tiangong.service;

import com.tiangong.dao.AuthRoleDao;
import com.tiangong.domain.auth.AuthRole;
import com.tiangong.domain.auth.AuthRoleElementOperation;
import com.tiangong.domain.auth.AuthRoleMenu;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

/**
 * @BelongsProject: bilibili
 * @BelongsPackage: com.tiangong.service
 * @Author: ChenLipeng
 * @CreateTime: 2022-07-12  19:13
 * @Description: 用户角色及其权限的业务层
 * @Version: 1.0
 */
@Service
public class AuthRoleService {

    @Autowired
    private AuthRoleDao authRoleDao;

    @Autowired
    private AuthRoleElementOperationService authRoleElementOperationService;

    @Autowired
    private AuthRoleMenuService authRoleMenuService;

    /**
    * @description: 通过角色组查询该角色组拥有的所有页面元素权限
    * @author: ChenLipeng
    * @date: 2022/7/15 17:44
    * @param: roleIdSet
    * @return: java.util.List<com.tiangong.domain.auth.AuthRoleElementOperation>
    **/
    public List<AuthRoleElementOperation> getRoleElementOperationsByRoleIds(Set<Long> roleIdSet) {
        return authRoleElementOperationService.getRoleElementOperationsByRoleIds(roleIdSet);
    }

    /**
    * @description: 通过角色组查询该角色组所拥有的所有页面权限
    * @author: ChenLipeng
    * @date: 2022/7/15 17:44
    * @param: roleIdSet
    * @return: java.util.List<com.tiangong.domain.auth.AuthRoleMenu>
    **/
    public List<AuthRoleMenu> getAuthRoleMeansByRoleIds(Set<Long> roleIdSet) {
        return authRoleMenuService.getAuthRoleMeansByRoleIds(roleIdSet);
    }

    /**
    * @description: 通过角色代码查询角色信息
    * @author: ChenLipeng
    * @date: 2022/7/15 17:45
    * @param: code
    * @return: com.tiangong.domain.auth.AuthRole
    **/
    public AuthRole getRoleByCode(String code) {
        return authRoleDao.getRoleByCode(code);
    }
}
