package com.tiangong.api;

import com.tiangong.api.support.UserSupport;
import com.tiangong.domain.JsonResponse;
import com.tiangong.domain.auth.UserAuthorities;
import com.tiangong.service.UserAuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @BelongsProject: bilibili
 * @BelongsPackage: com.tiangong.api
 * @Author: ChenLipeng
 * @CreateTime: 2022-07-12  19:00
 * @Description: 获取用户权限表述层
 * @Version: 1.0
 */
@RestController
public class UserAuthApi {

    @Autowired
    private UserSupport userSupport;

    @Autowired
    private UserAuthService userAuthService;

    /**
    * @description: 调用业务层获取当前用户的所有权限
    * @author: ChenLipeng
    * @date: 2022/7/15 19:36
    * @return: com.tiangong.domain.JsonResponse<com.tiangong.domain.auth.UserAuthorities>
    **/
    @GetMapping("/user-authorities")
    public JsonResponse<UserAuthorities> getUserAuthorities(){
        Long userId = userSupport.getCurrentUserId();
        UserAuthorities userAuthorities = userAuthService.getUserAuthorities(userId);
        return new JsonResponse<>(userAuthorities);
    }

}
