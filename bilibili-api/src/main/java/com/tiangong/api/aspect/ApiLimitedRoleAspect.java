package com.tiangong.api.aspect;

import com.tiangong.api.support.UserSupport;
import com.tiangong.domain.annotation.ApiLimitedRole;
import com.tiangong.domain.auth.UserRole;
import com.tiangong.domain.exception.ConditionException;
import com.tiangong.service.UserRoleService;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @BelongsProject: bilibili
 * @BelongsPackage: com.tiangong.api.aspect
 * @Author: ChenLipeng
 * @CreateTime: 2022-07-13  19:40
 * @Description: 接口限制的切面
 * @Version: 1.0
 */
@Order(1)
@Component
@Aspect
public class ApiLimitedRoleAspect {

    @Autowired
    private UserSupport userSupport;

    @Autowired
    private UserRoleService userRoleService;

    /**
    * @description: 设置切入点，当代码执行到ApiLimitedRole注解时执行增强
    * @author: ChenLipeng 
    * @date: 2022/7/15 19:22
    **/
    @Pointcut("@annotation(com.tiangong.domain.annotation.ApiLimitedRole)")
    public void check(){
    }

    /**
    * @description: 前置通知
    * @author: ChenLipeng
    * @date: 2022/7/15 19:24
    * @param: joinPoint
    * @param: apiLimitedRole
    **/
    @Before("check() && @annotation(apiLimitedRole)")
    public void doBefore(JoinPoint joinPoint, ApiLimitedRole apiLimitedRole){
        //获取用户id
        Long userId = userSupport.getCurrentUserId();
        //获取用户角色组
        List<UserRole> userRoleList = userRoleService.getUserRoleByUserId(userId);
        //获取注解中标注的没有对应权限的角色
        String[] limitedRoleCodeList = apiLimitedRole.limitedRoleCodeList();
        //将注解参数变为集合
        Set<String> limitedRoleCodeSet = Arrays.stream(limitedRoleCodeList).collect(Collectors.toSet());
        //将用户角色组变为集合
        Set<String> roleCodeSet = userRoleList.stream().map(UserRole::getRoleCode).collect(Collectors.toSet());
        //取交集
        roleCodeSet.retainAll(limitedRoleCodeSet);
        //如果存在交集则用户不具有对应权限
        if (roleCodeSet.size() > 0){
            throw new ConditionException("权限不足！");
        }
    }

}
