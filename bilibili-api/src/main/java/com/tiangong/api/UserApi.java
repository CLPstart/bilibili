package com.tiangong.api;

import com.alibaba.fastjson.JSONObject;
import com.tiangong.api.support.UserSupport;
import com.tiangong.domain.*;
import com.tiangong.service.UserFollowingService;
import com.tiangong.service.UserService;
import com.tiangong.util.RSAUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;


/**
 * @BelongsProject: bilibili
 * @BelongsPackage: com.tiangong.api
 * @Author: ChenLipeng
 * @CreateTime: 2022-06-13  13:45
 * @Description: UserController，User的表述层
 * @Version: 1.0
 */
@RestController
public class UserApi {

    @Autowired
    private UserService userService;

    @Autowired
    private UserSupport userSupport;

    @Autowired
    private UserFollowingService userFollowingService;

    /**
    * @description: 查询当前用户信息的表述层
    * @author: ChenLipeng
    * @date: 2022/6/14 14:36
    * @return: com.tiangong.domain.JsonResponse<com.tiangong.domain.User>
    **/
    @GetMapping("/users")
    public JsonResponse<User> getUserInfo(){
        //根据token获取当前用户id
        Long userId = userSupport.getCurrentUserId();
        //根据用户id获得用户信息
        User user = userService.getUserInfo(userId);
        return new JsonResponse<>(user);
    }

    /**
    * @description: 获取RSA公钥
    * @author: ChenLipeng 
    * @date: 2022/6/13 13:52
    * @return: com.tiangong.domain.JsonResponse<java.lang.String>
    **/
    @GetMapping("/rsa-pks")
    public JsonResponse<String> getRsaPublicKey(){
        String pk = RSAUtil.getPublicKeyStr();
        return new JsonResponse<>(pk);
    }

    /**
    * @description: 用户注册表述层
    * @author: ChenLipeng 
    * @date: 2022/6/14 9:47
    * @param: user
    * @return: com.tiangong.domain.JsonResponse<java.lang.String>
    **/
    @PostMapping("/users")
    public JsonResponse<String> addUser(@RequestBody User user){
        //调用业务层的注册方法
        userService.addUser(user);
        //向前端返回成功信息
        return JsonResponse.success();
    }

    /**
    * @description: 用户登录表述层
    * @author: ChenLipeng
    * @date: 2022/6/14 10:43
    * @param: user
    * @return: com.tiangong.domain.JsonResponse<java.lang.String>
    **/
    @PostMapping("/user-tokens")
    public JsonResponse<String> login(@RequestBody User user) throws Exception {
        //获取token令牌
        String token = userService.login(user);
        //将token数据放入Json对象
        return new JsonResponse<>(token);
    }

    /**
    * @description: 更改用户信息的表述层
    * @author: ChenLipeng
    * @date: 2022/6/14 19:14
    * @param: user
    * @return: com.tiangong.domain.JsonResponse<java.lang.String>
    **/
    @PutMapping("/users")
    public JsonResponse<String> updateUsers(@RequestBody User user) throws Exception {
        //从token中获取用户id
        Long userId = userSupport.getCurrentUserId();
        //把用户id写入对象中
        user.setId(userId);
        //调用业务层方法
        userService.updateUsers(user);
        return JsonResponse.success();
    }

    /**
    * @description: 更改用户详细信息
    * @author: ChenLipeng
    * @date: 2022/6/14 19:15
    * @param: userInfo
    * @return: com.tiangong.domain.JsonResponse<java.lang.String>
    **/
    @PutMapping("/user-infos")
    public JsonResponse<String> updateUserInfos(@RequestBody UserInfo userInfo){
        Long userId = userSupport.getCurrentUserId();
        userInfo.setUserId(userId);
        userService.updateUserInfos(userInfo);
        return JsonResponse.success();
    }

    /**
    * @description: 获取分页查询表述层
    * @author: ChenLipeng
    * @date: 2022/6/18 10:55
    * @param: no 页码
    * @param: size 每页的内容量
    * @param: nick 根据昵称查询
    * @return: com.tiangong.domain.JsonResponse<com.tiangong.domain.PageResult<com.tiangong.domain.UserInfo>>
    **/
    @GetMapping("/user-infos")
    public JsonResponse<PageResult<UserInfo>> pageListUserInfos(@RequestParam Integer no,@RequestParam Integer size, String nick){
        //获取当前的用户id
        Long userId = userSupport.getCurrentUserId();
        //创建JSONObject对象，该对象实现了Map
        JSONObject params = new JSONObject();
        //设置参数
        params.put("no", no);
        params.put("size", size);
        params.put("nick", nick);
        params.put("userId", userId);
        //调用业务层返回查询到的用户信息
        PageResult<UserInfo> result = userService.pageListUserInfos(params);
        //判断是否查询到信息
        if (result.getTotal() > 0){
            //如果查询到就检查一下查询到的用户是否已经被关注
            List<UserInfo> checkedUserInfoList = userFollowingService.checkFollowingStatus(result.getList(), userId);
            //重新设置列表
            result.setList(checkedUserInfoList);
        }
        //返回结果
        return new JsonResponse<>(result);
    }

    /**
    * @description: 双token登录表述层
    * @author: ChenLipeng
    * @date: 2022/7/15 19:32
    * @param: user
    * @return: com.tiangong.domain.JsonResponse<java.util.Map<java.lang.String,java.lang.Object>>
    **/
    @PostMapping("/user-dts")
    public JsonResponse<Map<String,Object>> loginForDts(@RequestBody User user) throws Exception {
        Map<String,Object> map = userService.loginForDts(user);
        return new JsonResponse<>(map);
    }

    /**
    * @description: 登出表述层
    * @author: ChenLipeng
    * @date: 2022/7/15 19:32
    * @param: request
    * @return: com.tiangong.domain.JsonResponse<java.lang.String>
    **/
    @DeleteMapping("/refresh-tokens")
    public JsonResponse<String> logout(HttpServletRequest request){
        //从请求头中获取refresToken
        String refreshToken = request.getHeader("refreshToken");
        //获取当前用户id
        Long userId = userSupport.getCurrentUserId();
        //调用业务层登出
        userService.logout(userId, refreshToken);
        return JsonResponse.success();
    }

    /**
    * @description: 刷新当前可用token
    * @author: ChenLipeng
    * @date: 2022/7/15 19:34
    * @param: request
    * @return: com.tiangong.domain.JsonResponse<java.lang.String>
    **/
    @PostMapping("/access-tokens")
    public JsonResponse<String> refreshAccessToken(HttpServletRequest request) throws Exception {
        //从请求头中获取refreshToken
        String refreshToken = request.getHeader("refreshToken");
        //刷新Token
        String accessToken = userService.refreshAccessToken(refreshToken);
        return new JsonResponse<String>(accessToken);
    }

}
