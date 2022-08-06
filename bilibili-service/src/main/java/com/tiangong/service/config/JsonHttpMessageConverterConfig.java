package com.tiangong.service.config;

import com.alibaba.fastjson.serializer.SerializerFeature;
import com.alibaba.fastjson.support.config.FastJsonConfig;
import com.alibaba.fastjson.support.spring.FastJsonHttpMessageConverter;
import org.springframework.boot.autoconfigure.http.HttpMessageConverters;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

/**
 * @BelongsProject: bilibili
 * @BelongsPackage: com.tiangong.service.config
 * @Author: ChenLipeng
 * @CreateTime: 2022-06-13  12:15
 * @Description: Json转换配置
 * @Version: 1.0
 */
@Configuration
public class JsonHttpMessageConverterConfig {

    /**
    * @description: FastJson配置
    * @author: ChenLipeng
    * @date: 2022/6/13 12:01
    * @return: org.springframework.boot.autoconfigure.http.HttpMessageConverters
    **/
    @Bean
    @Primary
    public HttpMessageConverters fastJsonHttpMessageConvertes(){
        //实例化转换类对象
        FastJsonHttpMessageConverter fastConverter = new FastJsonHttpMessageConverter();
        //实例化FastJson配置类
        FastJsonConfig fastJsonConfig = new FastJsonConfig();
        //在配置类中设置时间日期格式
        fastJsonConfig.setDateFormat("yyyy-MM-dd HH:mm:ss");
        //在配置类中设置序列化配置
        fastJsonConfig.setSerializerFeatures(
                SerializerFeature.PrettyFormat,//设置固定格式
                SerializerFeature.WriteNullStringAsEmpty,//将字符串中的空对象设置为空字符串
                SerializerFeature.WriteNullListAsEmpty,//将列表中的空对象设置为空列表
                SerializerFeature.WriteMapNullValue,//将map中的空对象设置为空map
                SerializerFeature.MapSortField,//让map对象中的元素按照键的大小排序
                SerializerFeature.DisableCircularReferenceDetect//关闭循环引用
        );
        //将FastJson配置类置入转换类中
        fastConverter.setFastJsonConfig(fastJsonConfig);
        //返回HttpMessageConverters对象,并将FastJson转换类置入
        return new HttpMessageConverters(fastConverter);
    }
}
