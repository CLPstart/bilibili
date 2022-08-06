package com.tiangong.domain.annotation;

import org.springframework.stereotype.Component;

import java.lang.annotation.*;

/**
 * @BelongsProject: bilibili
 * @BelongsPackage: com.tiangong.domain.annotation
 * @Author: ChenLipeng
 * @CreateTime: 2022-07-12  16:44
 * @Description: 数据限制注解
 * @Version: 1.0
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
@Documented
@Component
public @interface DataLimited {

}
