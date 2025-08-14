package org.bankai;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@EnableAspectJAutoProxy
@SpringBootApplication(scanBasePackages = "org.bankai")
public class TestApplication {
    public static final String FRIEND_KEY = "friend:userId:%d";


    public static void main(String[] args) {
        SpringApplication.run(TestApplication.class, args);
    }


}
