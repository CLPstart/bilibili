package com.tiangong.domain.exception;

import lombok.Data;

/**
 * @BelongsProject: bilibili
 * @BelongsPackage: com.tiangong.domain.exception
 * @Author: ChenLipeng
 * @CreateTime: 2022-06-13  12:20
 * @Description: 定制化异常处理类
 * @Version: 1.0
 */
@Data
public class ConditionException extends RuntimeException{

    private static final long serialVersionUID = 1L;

    private String code;

    public ConditionException(String code, String name){
        super(name);
        this.code = code;
    }

    public ConditionException(String name){
        super(name);
        this.code = "500";
    }
}
