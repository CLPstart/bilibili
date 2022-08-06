package com.tiangong.service;

import com.tiangong.dao.UserRoleDao;
import com.tiangong.domain.auth.UserRole;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * @BelongsProject: bilibili
 * @BelongsPackage: com.tiangong.service
 * @Author: ChenLipeng
 * @CreateTime: 2022-07-12  19:13
 * @Description: 用户角色业务层
 * @Version: 1.0
 */
@Service
public class UserRoleService {

    @Autowired
    private UserRoleDao userRoleDao;

    /**
    * @description: 根据用户id获取其对应的角色
    * @author: ChenLipeng
    * @date: 2022/7/15 18:57
    * @param: userId
    * @return: java.util.List<com.tiangong.domain.auth.UserRole>
    **/
    public List<UserRole> getUserRoleByUserId(Long userId) {
        return userRoleDao.getUserRoleByUserId(userId);
    }

    /**
    * @description: 添加用户角色对应关系
    * @author: ChenLipeng
    * @date: 2022/7/15 18:58
    * @param: userRole
    **/
    public void addUserRole(UserRole userRole) {
        userRole.setCreateTime(new Date());
        userRoleDao.addUserRole(userRole);
    }
}
