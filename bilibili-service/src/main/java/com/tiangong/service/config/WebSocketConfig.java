package com.tiangong.service.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.server.standard.ServerEndpointExporter;

/**
 * @BelongsProject: bilibili
 * @BelongsPackage: com.tiangong.service.config
 * @Author: ChenLipeng
 * @CreateTime: 2022-07-18  16:55
 * @Description: webSocket配置类
 * @Version: 1.0
 */
@Configuration
public class WebSocketConfig {

    /**
    * @description: 服务器的端点发现及导出
    * @author: ChenLipeng
    * @date: 2022/7/18 16:58
    * @return: org.springframework.web.socket.server.standard.ServerEndpointExporter
    **/
    @Bean
    public ServerEndpointExporter serverEndpointExporter(){
        return new ServerEndpointExporter();
    }

}
