package com.tiangong.api.aspect;

import com.tiangong.api.support.UserSupport;
import com.tiangong.domain.UserMoment;
import com.tiangong.domain.annotation.ApiLimitedRole;
import com.tiangong.domain.auth.UserRole;
import com.tiangong.domain.constant.AuthRoleConstant;
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
 * @Description: 数据权限限制切面
 * @Version: 1.0
 */
@Order(1)
@Component
@Aspect
public class DataLimitedAspect {

    @Autowired
    private UserSupport userSupport;

    @Autowired
    private UserRoleService userRoleService;

    /**
    * @description: 设置切入点，当代码执行到DataLimited注解时切入
    * @author: ChenLipeng
    * @date: 2022/7/15 19:26
    **/
    @Pointcut("@annotation(com.tiangong.domain.annotation.DataLimited)")
    public void check(){
    }

    /**
    * @description: 设置前置通知
    * @author: ChenLipeng
    * @date: 2022/7/15 19:27
    * @param: joinPoint
    **/
    @Before("check()")
    public void doBefore(JoinPoint joinPoint){
        //获取用户id
        Long userId = userSupport.getCurrentUserId();
        //获取用户角色组
        List<UserRole> userRoleList = userRoleService.getUserRoleByUserId(userId);
        //从用户角色组中获取角色代码并置入集合
        Set<String> roleCodeSet = userRoleList.stream().map(UserRole::getRoleCode).collect(Collectors.toSet());
        //获取连接点的所有参数
        Object[] args = joinPoint.getArgs();
        //遍历参数
        for (Object arg : args) {
            //如果参数是用户动态的子类
            if (arg instanceof UserMoment){
                UserMoment userMoment = (UserMoment) arg;
                //获得动态类型
                String type = userMoment.getType();
                //Lv0用户不得发送类型为0的动态
                if(roleCodeSet.contains(AuthRoleConstant.ROLE_LV0) && !"0".equals(type)){
                    throw new ConditionException("参数异常");
                }
            }
        }
    }

}
