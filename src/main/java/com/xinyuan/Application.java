package com.xinyuan;

import com.xinyuan.util.SpringUtil;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableScheduling;


/**
 * @author liang
 */
@EnableRabbit
@EnableScheduling
@SpringBootApplication
@EnableAutoConfiguration
@ComponentScan("com.xinyuan")
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class);
    }

    @Bean
    public SpringUtil getSpringUtil() {
        return new SpringUtil();
    }
}
