package com.tiangong.api;

import com.tiangong.api.support.UserSupport;
import com.tiangong.domain.JsonResponse;
import com.tiangong.domain.UserMoment;
import com.tiangong.domain.annotation.ApiLimitedRole;
import com.tiangong.domain.annotation.DataLimited;
import com.tiangong.domain.constant.AuthRoleConstant;
import com.tiangong.service.UserMomentsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @BelongsProject: bilibili
 * @BelongsPackage: com.tiangong.api
 * @Author: ChenLipeng
 * @CreateTime: 2022-06-19  11:20
 * @Description: 用户动态表述层
 * @Version: 1.0
 */
@RestController
public class UserMomentsApi {

    @Autowired
    private UserMomentsService userMomentsService;

    @Autowired
    private UserSupport userSupport;

    /**
    * @description: 创建用户动态
    * @author: ChenLipeng
    * @date: 2022/6/19 11:46
    * @param: userMoment
    * @return: com.tiangong.domain.JsonResponse<java.lang.String>
    **/
    @ApiLimitedRole(limitedRoleCodeList = {AuthRoleConstant.ROLE_LV0})
    @DataLimited
    @PostMapping("/user-moments")
    public JsonResponse<String> addUserMoments(@RequestBody UserMoment userMoment){
        //获取当前用户id
        Long userId = userSupport.getCurrentUserId();

        //设置当前用户id
        userMoment.setUserId(userId);
        //调用业务层方法
        try {
            userMomentsService.addUserMoments(userMoment);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
        return JsonResponse.success();
    }

    /**
    * @description: 获取用户动态表述层
    * @author: ChenLipeng
    * @date: 2022/7/15 21:02
    * @return: com.tiangong.domain.JsonResponse<java.util.List<com.tiangong.domain.UserMoment>>
    **/
    @GetMapping("/user-subscribed-moments")
    public JsonResponse<List<UserMoment>> getUserSubscribedMoments(){
        //获取用户当前id
        Long userId = userSupport.getCurrentUserId();
        //调用业务层获取动态列表
        List<UserMoment> list = userMomentsService.getUserSubscribedMoments(userId);
        return new JsonResponse<>(list);
    }

}
