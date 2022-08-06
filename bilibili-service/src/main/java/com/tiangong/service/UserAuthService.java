package com.tiangong.service;

import com.tiangong.domain.auth.*;
import com.tiangong.domain.constant.AuthRoleConstant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @BelongsProject: bilibili
 * @BelongsPackage: com.tiangong.service
 * @Author: ChenLipeng
 * @CreateTime: 2022-07-12  19:01
 * @Description: 用户权限业务层
 * @Version: 1.0
 */
@Service
public class UserAuthService {

    @Autowired
    private UserRoleService userRoleService;

    @Autowired
    private AuthRoleService authRoleService;

    /**
    * @description: 得到登录用户的所有权限
    * @author: ChenLipeng
    * @date: 2022/7/15 17:46
    * @param: userId
    * @return: com.tiangong.domain.auth.UserAuthorities
    **/
    public UserAuthorities getUserAuthorities(Long userId) {
        //获取当前用户所属角色组
        List<UserRole> userRoleList = userRoleService.getUserRoleByUserId(userId);
        //将角色组转换为集合形式
        Set<Long> roleIdSet = userRoleList.stream().map(UserRole::getRoleId).collect(Collectors.toSet());
        //获取角色组对各个元素的操控权限
        List<AuthRoleElementOperation> roleElementOperationList = authRoleService.getRoleElementOperationsByRoleIds(roleIdSet);
        //获取角色组对各个页面的操控权限
        List<AuthRoleMenu> authRoleMenuList = authRoleService.getAuthRoleMeansByRoleIds(roleIdSet);
        //将角色对页面和页面元素的操作权限封装到实体类
        UserAuthorities userAuthorities = new UserAuthorities();
        userAuthorities.setRoleElementOperationList(roleElementOperationList);
        userAuthorities.setRoleMenuList(authRoleMenuList);
        //返回权限实体类
        return userAuthorities;
    }

    /**
    * @description: 添加用户默认角色
    * @author: ChenLipeng 
    * @date: 2022/7/15 18:52 
    * @param: id
    **/
    public void addUserDefaultRole(Long id) {
        //实例化用户角色关系对象
        UserRole userRole = new UserRole();
        //获取Lv0角色
        AuthRole role = authRoleService.getRoleByCode(AuthRoleConstant.ROLE_LV0);
        //设置用户id
        userRole.setUserId(id);
        //设置角色id
        userRole.setRoleId(role.getId());
        //添加角色
        userRoleService.addUserRole(userRole);
    }
}
