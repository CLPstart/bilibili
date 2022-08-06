package com.tiangong.service;

import com.tiangong.dao.AuthRoleElementOperationDao;
import com.tiangong.domain.auth.AuthRoleElementOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

/**
 * @BelongsProject: bilibili
 * @BelongsPackage: com.tiangong.service
 * @Author: ChenLipeng
 * @CreateTime: 2022-07-12  19:34
 * @Description: 页面元素与角色关联关系的业务层
 * @Version: 1.0
 */
@Service
public class AuthRoleElementOperationService {

    @Autowired
    private AuthRoleElementOperationDao authRoleElementOperationDao;

    /**
    * @description: 通过角色组查询该角色组拥有的所有页面元素权限
    * @author: ChenLipeng
    * @date: 2022/7/15 17:42
    * @param: roleIdSet
    * @return: java.util.List<com.tiangong.domain.auth.AuthRoleElementOperation>
    **/
    public List<AuthRoleElementOperation> getRoleElementOperationsByRoleIds(Set<Long> roleIdSet) {
        return authRoleElementOperationDao.getRoleElementOperationsByRoleIds(roleIdSet);
    }
}
