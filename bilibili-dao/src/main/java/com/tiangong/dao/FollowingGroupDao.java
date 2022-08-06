package com.tiangong.dao;

import com.tiangong.domain.FollowingGroup;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;


/**
 * @BelongsProject: bilibili
 * @BelongsPackage: com.tiangong.dao
 * @Author: ChenLipeng
 * @CreateTime: 2022-06-13  13:46
 * @Description: t_following_group表中的mapper映射
 * @Version: 1.0
 */
@Mapper
public interface FollowingGroupDao {

    /**
    * @description: 通过分组类型查找组别信息
    * @author: ChenLipeng
    * @date: 2022/6/15 14:21
    * @param: type
    * @return: com.tiangong.domain.FollowingGroup
    **/
    FollowingGroup getByType(String type);

    /**
    * @description: 通过组别id查找组别信息
    * @author: ChenLipeng
    * @date: 2022/6/15 14:22
    * @param: id
    * @return: com.tiangong.domain.FollowingGroup
    **/
    FollowingGroup getById(Long id);

    /**
    * @description: 查询某位用户的分组信息
    * @author: ChenLipeng
    * @date: 2022/6/16 10:57
    * @param: userId
    * @return: java.util.List<com.tiangong.domain.FollowingGroup>
    **/
    List<FollowingGroup> getByUserId(Long userId);

    /**
    * @description: 添加用户分组
    * @author: ChenLipeng
    * @date: 2022/6/18 10:52
    * @param: followingGroup
    * @return: java.lang.Integer
    **/
    Integer addUserFollowingGroups(FollowingGroup followingGroup);

    /**
    * @description: 查询用户分组信息
    * @author: ChenLipeng
    * @date: 2022/6/18 10:54
    * @param: userId
    * @return: java.util.List<com.tiangong.domain.FollowingGroup>
    **/
    List<FollowingGroup> getUserFollowingGroups(Long userId);
}
