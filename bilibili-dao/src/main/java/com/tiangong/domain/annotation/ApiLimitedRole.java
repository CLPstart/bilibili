package com.tiangong.domain.annotation;

import org.springframework.stereotype.Component;

import java.lang.annotation.*;

/**
 * @BelongsProject: bilibili
 * @BelongsPackage: com.tiangong.domain.annotation
 * @Author: ChenLipeng
 * @CreateTime: 2022-07-12  16:44
 * @Description: 接口限制注解
 * @Version: 1.0
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
@Documented
@Component
public @interface ApiLimitedRole {

    /**
    * @description: 参数为需要限制的等级，在接口方法上标注该注解，并设置参数，参数组成为对应等级的LvCode，只要注解中标注了对应的等级，则该等级不能操控被标注接口
    * @author: ChenLipeng
    * @date: 2022/7/15 17:23
    * @return: java.lang.String[]
    **/
    String[] limitedRoleCodeList() default {};

}
