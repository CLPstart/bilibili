package com.tiangong.service;

import com.tiangong.dao.AuthRoleMenuDao;
import com.tiangong.domain.auth.AuthRoleMenu;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

/**
 * @BelongsProject: bilibili
 * @BelongsPackage: com.tiangong.service
 * @Author: ChenLipeng
 * @CreateTime: 2022-07-12  19:35
 * @Description: 页面与角色权限关系的业务层
 * @Version: 1.0
 */
@Service
public class AuthRoleMenuService {

    @Autowired
    private AuthRoleMenuDao authRoleMenuDao;

    /**
    * @description: 通过角色组查询该角色组所拥有的所有页面权限
    * @author: ChenLipeng
    * @date: 2022/7/15 17:43
    * @param: roleIdSet
    * @return: java.util.List<com.tiangong.domain.auth.AuthRoleMenu>
    **/
    public List<AuthRoleMenu> getAuthRoleMeansByRoleIds(Set<Long> roleIdSet) {
        return authRoleMenuDao.getAuthRoleMeansByRoleIds(roleIdSet);
    }
}
