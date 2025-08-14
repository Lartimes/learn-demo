package org.bankai;

import org.bankai.utils.RedisCacheUtil;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.util.Arrays;

@EnableScheduling
@MapperScan(basePackages = "org.bankai.mapper")
@EnableAspectJAutoProxy
@SpringBootApplication(scanBasePackages = "org.bankai")
public class TestApplication {
    public static final String FRIEND_KEY = "friend:userId:%d";
    private final RedisCacheUtil redisCacheUtil;

    public TestApplication(RedisCacheUtil redisCacheUtil) {
        this.redisCacheUtil = redisCacheUtil;
    }


    public static void main(String[] args) {
        SpringApplication.run(TestApplication.class, args);
    }


    //    注入自定义好友关系
    @Bean
    public CommandLineRunner commandLineRunner(ApplicationContext ctx) {
        return args -> {
            var arr = Arrays.asList(1, 2, 3);
            for (int i = 0; i < arr.size(); i++) {
                int uid = arr.get(i);
                for (int j = 0; j < arr.size(); j++) {
                    if (j != i) {
                        redisCacheUtil.addZSetWithScores(String.format(FRIEND_KEY, uid), arr.get(j), null);
                    }
                }
            }
        };
    }
}
