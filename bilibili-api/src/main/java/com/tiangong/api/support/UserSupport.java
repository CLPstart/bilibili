package com.tiangong.api.support;

import com.tiangong.domain.exception.ConditionException;
import com.tiangong.util.TokenUtil;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.Servlet;

/**
 * @BelongsProject: bilibili
 * @BelongsPackage: com.tiangong.api.support
 * @Author: ChenLipeng
 * @CreateTime: 2022-06-14  13:34
 * @Description: 用户表述层正常运行所需组件
 * @Version: 1.0
 */
@Component
public class UserSupport {

    /**
    * @description: 获取当前的用户id
    * @author: ChenLipeng
    * @date: 2022/6/14 14:39
    * @return: java.lang.Long
    **/
    public Long getCurrentUserId(){
        //获取请求参数
        ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        //从请求头中获取token
        String token = requestAttributes.getRequest().getHeader("token");
        //从token中获取用户id
        Long userId = TokenUtil.verifyToken(token);
        if (userId < 0){
            throw new ConditionException("非法用户！");
        }
        return userId;
    }
}
