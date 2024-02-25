package com.kob.botrunningsystem.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class RestTemplateConfig { //一个能在两个进程间通讯的工具类
    @Bean
    public RestTemplate getRestTemplate() {
        return new RestTemplate();
    }
}
