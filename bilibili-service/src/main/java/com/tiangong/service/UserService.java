package com.tiangong.service;

import com.alibaba.fastjson.JSONObject;
import com.mysql.cj.util.StringUtils;
import com.tiangong.dao.UserDao;
import com.tiangong.domain.*;
import com.tiangong.domain.constant.UserConstant;
import com.tiangong.domain.exception.ConditionException;
import com.tiangong.util.MD5Util;
import com.tiangong.util.RSAUtil;
import com.tiangong.util.TokenUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PostMapping;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

/**
 * @BelongsProject: bilibili
 * @BelongsPackage: com.tiangong.service
 * @Author: ChenLipeng
 * @CreateTime: 2022-06-13  13:44
 * @Description: User类的业务逻辑层
 * @Version: 1.0
 */
@Service
public class UserService {

    @Autowired
    private UserDao userDao;

    @Autowired
    private UserAuthService userAuthService;

    /**
    * @description: 业务层用户注册方法
    * @author: ChenLipeng 
    * @date: 2022/6/14 9:49 
    * @param: user
    **/
    public void addUser(User user){
        //获取用户填写的手机号
        String phone = user.getPhone();
        //判断手机号是否为空
        if (StringUtils.isNullOrEmpty(phone)){
            throw new ConditionException("手机号不能为空！");
        }
        //判断手机号是否已被注册
        User dbUser = this.getUserByPhone(phone);
        if (dbUser != null){
            throw new ConditionException("该手机号已经注册！");
        }
        //获取当前时间
        Date now = new Date();
        //生成加密盐值
        String salt = String.valueOf(now.getTime());
        //获取用户输入的密码
        String password = user.getPassword();
        //定义解密明文
        String rawPassword;
        try {
            //对前端传来的密码进行RSA解密
            rawPassword = RSAUtil.decrypt(password);
        } catch (Exception e) {
            throw new ConditionException("密码解密失败！");
        }
        //对解密后的密码用MD5加密
        String md5Password = MD5Util.sign(rawPassword, salt, "UTF-8");
        //将MD5加密后的密码置入对象中
        user.setPassword(md5Password);
        //将创建时间置入对象中
        user.setCreateTime(now);
        //将盐值置入对象中
        user.setSalt(salt);
        //将用户注册基本信息存入数据库
        userDao.addUser(user);
        //添加用户详细信息
        UserInfo userInfo = new UserInfo();
        //获取用户注册id
        userInfo.setUserId(user.getId());
        //对用户详细信息设置默认值
        userInfo.setNick(UserConstant.DEFAULT_NICK);
        userInfo.setBirth(UserConstant.DEFAULT_BIRTH);
        userInfo.setGender(UserConstant.GENDER_MALE);
        userInfo.setCreateTime(now);
        //将用户详细信息对象存入数据库中
        userDao.addUserInfo(userInfo);
        //添加用户默认权限角色
        userAuthService.addUserDefaultRole(user.getId());
    }

    /**
    * @description: 根据用户输入手机号从数据库中查询用户对象
    * @author: ChenLipeng
    * @date: 2022/6/14 9:55
    * @param: phone
    * @return: com.tiangong.domain.User
    **/
    public User getUserByPhone(String phone){
        return userDao.getUserByPhone(phone);
    }

    /**
    * @description: 用户登录业务层
    * @author: ChenLipeng 
    * @date: 2022/6/14 10:46
    * @param: user
    * @return: java.lang.String
    **/
    public String login(User user) throws Exception {
        //获取用户输入的手机号
        String email = user.getEmail() == null ? "" : user.getEmail();
        String phone = user.getPhone() == null ? "" : user.getPhone();
        //判断用户输入手机号是否为空值
        if(StringUtils.isNullOrEmpty(phone) && StringUtils.isNullOrEmpty(email)){
            throw  new ConditionException("参数异常！");
        }
        //查询用户输入的手机号是否已经注册
        User dbUser = userDao.getUserByPhoneOrEmail(phone, email);
        if(dbUser == null){
            throw new ConditionException("当前用户不存在");
        }
        //获取用户输入的密码
        String password = user.getPassword();
        String rowPassword;
        //对用户输入的密码进行RSA解密
        try {
            rowPassword = RSAUtil.decrypt(password);
        } catch (Exception e) {
            throw new ConditionException("密码解密失败！");
        }
        //获取用户盐值
        String salt = dbUser.getSalt();
        //对用户输入的密码进行MD5加密
        String md5Password = MD5Util.sign(rowPassword, salt, "UTF-8");
        //对用户输入的密码进行校验
        if (!md5Password.equals(dbUser.getPassword())){
            throw new ConditionException("密码错误");
        }
        //返回用户令牌
        return TokenUtil.generateToken(dbUser.getId());
    }

    /**
    * @description: 根据用户id查询用户详细信息并整合的业务层
    * @author: ChenLipeng
    * @date: 2022/6/14 14:37
    * @param: userId
    * @return: com.tiangong.domain.User
    **/
    public User getUserInfo(Long userId) {
        User user = userDao.getUserById(userId);
        UserInfo userInfo = userDao.getUserInfoById(userId);
        user.setUserInfo(userInfo);
        return user;
    }

    /**
    * @description: 更新用户信息的业务层
    * @author: ChenLipeng
    * @date: 2022/6/14 19:12
    * @param: user
    **/
    public void updateUsers(User user) throws Exception {
        //获取用户id
        Long userId = user.getId();
        //判断用户id是否存在
        User dbUser = userDao.getUserById(userId);
        if (dbUser == null){
            throw new ConditionException("用户不存在！");
        }
        //更改密码
        if (!StringUtils.isNullOrEmpty(user.getPassword())){
            String rawPassword = RSAUtil.decrypt(user.getPassword());
            String md5Password = MD5Util.sign(rawPassword, dbUser.getSalt(), "UTF-8");
            user.setPassword(md5Password);
        }
        //设置修改时间
        user.setUpdateTime(new Date());
        //写入数据库
        userDao.updateUsers(user);
    }

    /**
    * @description: 更新用户详细信息的业务层
    * @author: ChenLipeng
    * @date: 2022/6/14 19:12
    * @param: userInfo
    **/
    public void updateUserInfos(UserInfo userInfo) {
        userInfo.setUpdateTime(new Date());
        userDao.updateUserInfos(userInfo);
    }

    /**
    * @description: 根据用户Id查询该用户的信息
    * @author: ChenLipeng
    * @date: 2022/6/16 10:53
    * @param: followingId
    * @return: com.tiangong.domain.User
    **/
    public User getUserById(Long followingId) {
        return userDao.getUserById(followingId);
    }

    /**
    * @description: 根据用户id集合查询全部对应的用户基本信息
    * @author: ChenLipeng
    * @date: 2022/6/16 10:52
    * @param: userIdList
    * @return: java.util.List<com.tiangong.domain.UserInfo>
    **/
    public List<UserInfo> getUserInfoByUserIds(Set<Long> userIdList) {
        return userDao.getUserInfoByUserIds(userIdList);
    }

    /**
    * @description: 分页查询业务层
    * @author: ChenLipeng
    * @date: 2022/6/18 10:59
    * @param: params
    * @return: com.tiangong.domain.PageResult<com.tiangong.domain.UserInfo>
    **/
    public PageResult<UserInfo> pageListUserInfos(JSONObject params) {
        //从表述层中获取参数
        Integer no = params.getInteger("no");
        Integer size = params.getInteger("size");
        //在params中设置分页start和limit，其中start代表当前数据从表中第几项开始查
        params.put("start", (no - 1) * size);
        params.put("limit", size);
        //获取查询到的总数
        Integer total = userDao.pageCountUserInfo(params);
        //实例化查询列表
        List<UserInfo> list = new ArrayList<>();
        if (total > 0){
            //将查询到的值置入列表中
            list = userDao.pageListUserInfos(params);
        }
        //返回查询结果
        return new PageResult<>(total, list);
    }

    /**
    * @description: 双token登录
    * @author: ChenLipeng
    * @date: 2022/7/15 18:59
    * @param: user
    * @return: java.util.Map<java.lang.String,java.lang.Object>
    **/
    public Map<String, Object> loginForDts(User user) throws Exception {
        //获取用户输入的手机号
        String email = user.getEmail() == null ? "" : user.getEmail();
        String phone = user.getPhone() == null ? "" : user.getPhone();
        //判断用户输入手机号是否为空值
        if(StringUtils.isNullOrEmpty(phone) && StringUtils.isNullOrEmpty(email)){
            throw  new ConditionException("参数异常！");
        }
        //查询用户输入的手机号是否已经注册
        User dbUser = userDao.getUserByPhoneOrEmail(phone, email);
        if(dbUser == null){
            throw new ConditionException("当前用户不存在");
        }
        //获取用户输入的密码
        String password = user.getPassword();
        String rowPassword;
        //对用户输入的密码进行RSA解密
        try {
            rowPassword = RSAUtil.decrypt(password);
        } catch (Exception e) {
            throw new ConditionException("密码解密失败！");
        }
        //获取用户盐值
        String salt = dbUser.getSalt();
        //对用户输入的密码进行MD5加密
        String md5Password = MD5Util.sign(rowPassword, salt, "UTF-8");
        //对用户输入的密码进行校验
        if (!md5Password.equals(dbUser.getPassword())){
            throw new ConditionException("密码错误");
        }
        Long userId = dbUser.getId();
        //返回用户令牌
        String accessToken = TokenUtil.generateToken(userId);
        String refreshToken = TokenUtil.generateRefreshToken(userId);
        //保存refreshToken到数据库
        userDao.deleteRefreshToken(refreshToken, userId);
        userDao.addRefreshToken(refreshToken, userId, new Date());
        //封装双token
        Map<String, Object> result = new HashMap<>();
        result.put("accessToken", accessToken);
        result.put("refreshToken", refreshToken);
        return result;
    }

    /**
    * @description: 登出业务层
    * @author: ChenLipeng
    * @date: 2022/7/15 19:00
    * @param: userId
    * @param: refreshToken
    **/
    public void logout(Long userId, String refreshToken) {
        //删除刷新token
        userDao.deleteRefreshToken(refreshToken, userId);
    }

    /**
    * @description: 更新token
    * @author: ChenLipeng
    * @date: 2022/7/15 19:00
    * @param: refreshToken
    * @return: java.lang.String
    **/
    public String refreshAccessToken(String refreshToken) throws Exception {
        //获取refreshToken的详细信息
        RefreshTokenDetail refreshTokenDetail = userDao.getRefreshTokenDetail(refreshToken);
        //判断refreshtoken是否存在
        if (refreshTokenDetail == null){
            throw new ConditionException("555", "token过期！");
        }
        //获取用户id
        Long userId = refreshTokenDetail.getUserId();
        //返回新的token
        return TokenUtil.generateToken(userId);
    }

    /**
    * @description: 批量查询用户详情
    * @author: ChenLipeng
    * @date: 2022/7/18 13:42
    * @param: userIdList
    * @return: java.util.List<com.tiangong.domain.UserInfo>
    **/
    public List<UserInfo> batchGetUserInfoByUserIds(Set<Long> userIdList) {
        return userDao.batchGetUserInfoByUserIds(userIdList);
    }
}
