package com.tiangong.service;

import com.tiangong.dao.UserFollowingDao;
import com.tiangong.domain.FollowingGroup;
import com.tiangong.domain.User;
import com.tiangong.domain.UserFollowing;
import com.tiangong.domain.UserInfo;
import com.tiangong.domain.constant.UserConstant;
import com.tiangong.domain.exception.ConditionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @BelongsProject: bilibili
 * @BelongsPackage: com.tiangong.service
 * @Author: ChenLipeng
 * @CreateTime: 2022-06-15  11:51
 * @Description: 用户关注业务层
 * @Version: 1.0
 */
@Service
public class UserFollowingService {

    @Autowired
    private UserFollowingDao userFollowingDao;

    @Autowired
    private FollowingGroupService followingGroupService;

    @Autowired
    private UserService userService;

    /**
    * @description: 添加用户关注
    * @author: ChenLipeng 
    * @date: 2022/6/15 14:25
    * @param: userFollowing
    **/
    @Transactional
    public void addUserFollowing(UserFollowing userFollowing){
        //获取用户定义的关注组别
        Long groupId = userFollowing.getGroupId();
        //如果用户没定义关注组别则分配为默认分组
        if (groupId == null){
            FollowingGroup followingGroup = followingGroupService.getByType(UserConstant.USER_FOLLOWING_GROUP_TYPE_DEFAULT);
            userFollowing.setGroupId(followingGroup.getId());
        }else { //如果用户定义了组别就判断一下该组别存不存在
            FollowingGroup followingGroup = followingGroupService.getById(groupId);
            if (followingGroup == null){
                throw new ConditionException("关注分组不存在！");
            }
            userFollowing.setGroupId(followingGroup.getId());
        }
        //获取关注用户id
        Long followingId = userFollowing.getFollowingId();
        //判断关注的用户是否存在
        User user = userService.getUserById(followingId);
        if (user == null){
            throw new ConditionException("关注用户不存在！");
        }
        //删除用户原本的关注信息
        userFollowingDao.deleteUserFollowing(userFollowing.getUserId(), followingId);
        //添加关注时间
        userFollowing.setCreateTime(new Date());
        //创建新的关注信息
        userFollowingDao.addUserFollowing(userFollowing);
    }

    /**
    * @description: 对关注用户进行分组
    * @author: ChenLipeng
    * @date: 2022/6/16 10:47
    * @param: userId
    * @return: java.util.List<com.tiangong.domain.FollowingGroup>
    **/
    public List<FollowingGroup> getUserFollowings(Long userId){

        //第一步，获取用户关注列表
        //第二步，根据用户id查询关注用户的基本信息
        //第三步，将关注用户按照关注分组进行分类

        //根据userId获取该用户的全部关注信息
        List<UserFollowing> list = userFollowingDao.getUserFollowings(userId);
        //将所有该用户关注的人的id放入一个集合中
        Set<Long> followingIdSet = list.stream().map(UserFollowing::getFollowingId).collect(Collectors.toSet());
        //如果该用户关注了其它用户，则查询所有被关注的用户的基本信息
        List<UserInfo> userInfoList = new ArrayList<>();
        if(followingIdSet.size() > 0){
            userInfoList = userService.getUserInfoByUserIds(followingIdSet);
        }
        //获取用户关注列表中的每一条信息
        for (UserFollowing userFollowing :
                list) {
            //获取用户基本信息表中的每一条信息
            for (UserInfo userInfo :
                    userInfoList) {
                //判断用户关注表中的关注id和用户基本信息表中的用户id是否匹配
                if (userFollowing.getFollowingId().equals(userInfo.getUserId())) {
                    //如果匹配则将用户基本信息存入用户关注对象中
                    userFollowing.setUserInfo(userInfo);
                }
            }
        }
        //根据用户Id获得其对关注用户的全部分组
        List<FollowingGroup> groupList = followingGroupService.getByUserId(userId);
        //先将所有用户都归在同一分组下
        FollowingGroup allGroup = new FollowingGroup();
        allGroup.setName(UserConstant.USER_FOLLOWING_GROUP_ALL_NAME);
        allGroup.setFollowingUserInfoList(userInfoList);
        //定义结果集
        List<FollowingGroup> result = new ArrayList<>();
        //将全部用户的分组放入结果集中
        result.add(allGroup);
        //获取每一个分组的对象
        for (FollowingGroup followingGroup :
                groupList) {
            //定义每一个分组关注的集合
            List<UserInfo> infoList = new ArrayList<>();
            //获得每一位关注者对象
            for (UserFollowing userFollowing :
                    list) {
                //判断分组id是否能与关注的id匹配
                if(followingGroup.getId().equals(userFollowing.getGroupId())){
                    //如果匹配则将该用户基本信息置入到分组关注的集合中
                    infoList.add(userFollowing.getUserInfo());
                }
            }
            //在该分组对象中存入关注者的集合
            followingGroup.setFollowingUserInfoList(infoList);
            //在结果集中放入该分组
            result.add(followingGroup);
        }
        //返回结果集
        return result;
    }

    public List<UserFollowing> getUserFans(Long userId){

        //获取当前用户的粉丝列表
        //根据粉丝的用户id查询基本信息
        //查询当前用户是否以关注该粉丝

        //获取当前所有粉丝列表
        List<UserFollowing> fanList = userFollowingDao.getUserFans(userId);
        Set<Long> fansIdSet = fanList.stream().map(UserFollowing::getUserId).collect(Collectors.toSet());
        List<UserInfo> userInfoList = new ArrayList<>();
        if (fansIdSet.size() > 0){
            userInfoList = userService.getUserInfoByUserIds(fansIdSet);
        }
        //查询粉丝的基本信息
        List<UserFollowing> followingList = userFollowingDao.getUserFollowings(userId);
        for (UserFollowing fan :
                fanList) {
            for (UserInfo userInfo :
                    userInfoList) {
                if(fan.getUserId().equals(userInfo.getUserId())){
                    userInfo.setFollowed(false);
                    fan.setUserInfo(userInfo);
                }
            }
            //判断是否互粉
            for (UserFollowing following :
                    followingList) {
                if(following.getFollowingId().equals(fan.getUserId())){
                    fan.getUserInfo().setFollowed(true);
                }
            }
        }
        return fanList;
    }

    /**
    * @description: 添加关注分组的业务层
    * @author: ChenLipeng
    * @date: 2022/6/18 10:50
    * @param: followingGroup
    * @return: java.lang.Long
    **/
    public Long addUserFollowingGroups(FollowingGroup followingGroup) {
        //设置创建日期
        followingGroup.setCreateTime(new Date());
        //设置分组编号
        followingGroup.setType(UserConstant.USER_FOLLOWING_GROUP_TYPE_USER);
        //添加分组
        followingGroupService.addUserFollowingGroups(followingGroup);
        return followingGroup.getId();
    }

    /**
    * @description: 查询用户分组信息
    * @author: ChenLipeng
    * @date: 2022/6/18 10:53
    * @param: userId
    * @return: java.util.List<com.tiangong.domain.FollowingGroup>
    **/
    public List<FollowingGroup> getUserFollowingGroups(Long userId) {
        return followingGroupService.getUserFollowingGroups(userId);
    }

    public List<UserInfo> checkFollowingStatus(List<UserInfo> userInfoList, Long userId) {
        List<UserFollowing> userFollowings = userFollowingDao.getUserFollowings(userId);
        for (UserInfo userInfo :
                userInfoList) {
            userInfo.setFollowed(false);
            for (UserFollowing userFollowing :
                    userFollowings) {
                if(userFollowing.getFollowingId().equals(userInfo.getUserId())){
                    userInfo.setFollowed(true);
                }
            }
        }
        return userInfoList;
    }
}
