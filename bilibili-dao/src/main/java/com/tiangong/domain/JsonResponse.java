package com.tiangong.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @BelongsProject: bilibili
 * @BelongsPackage: com.tiangong.domain
 * @Author: ChenLipeng
 * @CreateTime: 2022-06-13  12:15
 * @Description: 统一格式封装
 * @Version: 1.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class JsonResponse<T> {

    private String code;

    private String msg;

    private T data;

    public JsonResponse(String code, String msg){
        this.code = code;
        this.msg = msg;
    }

    public JsonResponse(T data){
        this.data = data;
        code = "0";
        msg = "成功";
    }

    public static JsonResponse<String> success(){
        return new JsonResponse<>(null);
    }

    public static JsonResponse<String> success(String data){
        return new JsonResponse<>(data);
    }

    public static JsonResponse<String> fail(){
        return new JsonResponse<>("1", "失败");
    }

    public static JsonResponse<String> fail(String code, String msg){
        return new JsonResponse<>(code, msg);
    }
}
