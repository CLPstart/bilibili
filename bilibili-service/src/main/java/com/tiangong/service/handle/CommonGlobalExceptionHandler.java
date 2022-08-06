package com.tiangong.service.handle;

import com.tiangong.domain.JsonResponse;
import com.tiangong.domain.exception.ConditionException;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;

/**
 * @BelongsProject: bilibili
 * @BelongsPackage: com.tiangong.service.handle
 * @Author: ChenLipeng
 * @CreateTime: 2022-06-13  12:15
 * @Description: 全局异常处理
 * @Version: 1.0
 */
@ControllerAdvice
@Order(Ordered.HIGHEST_PRECEDENCE)
public class CommonGlobalExceptionHandler {

    /**
    * @description: 异常处理方法
    * @author: ChenLipeng 
    * @date: 2022/6/13 12:30
    * @param: request
    * @param: e
    * @return: com.tiangong.domain.JsonResponse<java.lang.String>
    **/
    @ExceptionHandler(value = Exception.class)
    @ResponseBody
    public JsonResponse<String> commonExceptionHandler(HttpServletRequest request, Exception e){
        //获取异常信息
        String errorMsg = e.getMessage();
        //判断拦截异常是否为定制化异常实例
        if(e instanceof ConditionException){
            //返回定制化异常状态码
            String errorCode = ((ConditionException)e).getCode();
            //返回异常信息的Json串
            return new JsonResponse<>(errorCode, errorMsg);
        }else {
            //如被拦截异常非定义化异常，则默认异常错误码为500
            return new JsonResponse<>("500", errorMsg);
        }
    }
}
