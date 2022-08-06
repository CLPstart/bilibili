package com.tiangong;

import com.tiangong.service.websocket.WebsocketService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication
@EnableTransactionManagement
@EnableAsync
@EnableScheduling
public class BilibiliApp {

    public static void main(String[] args) {
        ApplicationContext app = SpringApplication.run(BilibiliApp.class, args);
        WebsocketService.setApplicationContext(app);
    }
}
