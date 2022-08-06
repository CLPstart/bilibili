package com.tiangong.api;

import com.tiangong.api.support.UserSupport;
import com.tiangong.domain.FollowingGroup;
import com.tiangong.domain.JsonResponse;
import com.tiangong.domain.UserFollowing;
import com.tiangong.service.UserFollowingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @BelongsProject: bilibili
 * @BelongsPackage: com.tiangong.api
 * @Author: ChenLipeng
 * @CreateTime: 2022-06-16  11:46
 * @Description:  用户关注
 * @Version: 1.0
 */
@RestController
public class UserFollowingApi {

    @Autowired
    private UserFollowingService userFollowingService;

    @Autowired
    private UserSupport userSupport;

    /**
    * @description: 添加用户关注的表述层
    * @author: ChenLipeng
    * @date: 2022/6/16 12:18
    * @param: userFollowing
    * @return: com.tiangong.domain.JsonResponse<java.lang.String>
    **/
    @PostMapping("/user-followings")
    public JsonResponse<String> addUserFollowings(@RequestBody UserFollowing userFollowing){
        Long userId = userSupport.getCurrentUserId();
        userFollowing.setUserId(userId);
        userFollowingService.addUserFollowing(userFollowing);
        return JsonResponse.success();
    }

    /**
    * @description: 查询用户关注的表述层
    * @author: ChenLipeng
    * @date: 2022/6/16 12:18
    * @return: com.tiangong.domain.JsonResponse<java.util.List<com.tiangong.domain.FollowingGroup>>
    **/
    @GetMapping("/user-followings")
    public JsonResponse<List<FollowingGroup>> getUserFollowings(){
        Long userId = userSupport.getCurrentUserId();
        List<FollowingGroup> result = userFollowingService.getUserFollowings(userId);
        return new JsonResponse<>(result);
    }

    /**
    * @description: 查询用户粉丝的表述层
    * @author: ChenLipeng
    * @date: 2022/6/16 12:18
    * @return: com.tiangong.domain.JsonResponse<java.util.List<com.tiangong.domain.UserFollowing>>
    **/
    @GetMapping("/user-fans")
    public JsonResponse<List<UserFollowing>> getUserFans(){
        Long userId = userSupport.getCurrentUserId();
        List<UserFollowing> result = userFollowingService.getUserFans(userId);
        return new JsonResponse<>(result);
    }

    /**
    * @description: 添加关注分组
    * @author: ChenLipeng
    * @date: 2022/6/18 10:49
    * @param: followingGroup
    * @return: com.tiangong.domain.JsonResponse<java.lang.Long>
    **/
    @PostMapping("/user-following-groups")
    public JsonResponse<Long> addUserFollowingGroups(@RequestBody FollowingGroup followingGroup){
        Long userId = userSupport.getCurrentUserId();
        followingGroup.setUserId(userId);
        Long groupId = userFollowingService.addUserFollowingGroups(followingGroup);
        return new JsonResponse<>(groupId);
    }

    /**
    * @description: 查询用户分组信息
    * @author: ChenLipeng
    * @date: 2022/6/18 10:53
    * @return: com.tiangong.domain.JsonResponse<java.util.List<com.tiangong.domain.FollowingGroup>>
    **/
    @GetMapping("/user-following-groups")
    public JsonResponse<List<FollowingGroup>> getUserFollowingGroups(){
        Long userId = userSupport.getCurrentUserId();
        List<FollowingGroup> list =  userFollowingService.getUserFollowingGroups(userId);
        return new JsonResponse<>(list);
    }

}
