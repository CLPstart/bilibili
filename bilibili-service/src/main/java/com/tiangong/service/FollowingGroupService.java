package com.tiangong.service;

import com.tiangong.dao.FollowingGroupDao;
import com.tiangong.domain.FollowingGroup;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @BelongsProject: bilibili
 * @BelongsPackage: com.tiangong.service
 * @Author: ChenLipeng
 * @CreateTime: 2022-06-15  11:52
 * @Description: 用户关注分组的业务层
 * @Version: 1.0
 */
@Service
public class FollowingGroupService {

    @Autowired
    private FollowingGroupDao followingGroupDao;

    /**
    * @description: 通过组类型获取组别信息
    * @author: ChenLipeng
    * @date: 2022/6/15 14:24
    * @param: type
    * @return: com.tiangong.domain.FollowingGroup
    **/
    public FollowingGroup getByType(String type){
        return followingGroupDao.getByType(type);
    }

    /**
    * @description: 通过组id获得组别信息
    * @author: ChenLipeng
    * @date: 2022/6/15 14:24
    * @param: id
    * @return: com.tiangong.domain.FollowingGroup
    **/
    public FollowingGroup getById(Long id){
        return followingGroupDao.getById(id);
    }

    /**
    * @description: 根据用户id查询分组信息
    * @author: ChenLipeng
    * @date: 2022/6/18 10:51
    * @param: userId
    * @return: java.util.List<com.tiangong.domain.FollowingGroup>
    **/
    public List<FollowingGroup> getByUserId(Long userId) {
        return followingGroupDao.getByUserId(userId);
    }

    /**
    * @description: 添加用户分组
    * @author: ChenLipeng 
    * @date: 2022/6/18 10:52 
    * @param: followingGroup
    **/
    public void addUserFollowingGroups(FollowingGroup followingGroup) {
        followingGroupDao.addUserFollowingGroups(followingGroup);
    }

    /**
    * @description: 查询用户分组信息
    * @author: ChenLipeng
    * @date: 2022/6/18 10:54
    * @param: userId
    * @return: java.util.List<com.tiangong.domain.FollowingGroup>
    **/
    public List<FollowingGroup> getUserFollowingGroups(Long userId) {
        return followingGroupDao.getUserFollowingGroups(userId);
    }
}
