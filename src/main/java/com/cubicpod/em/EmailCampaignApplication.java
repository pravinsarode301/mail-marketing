package com.cubicpod.em;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@Slf4j
@EnableScheduling
public class EmailCampaignApplication {


    public static void main(String[] args) {
        SpringApplication.run(EmailCampaignApplication.class, args);

    }

//    @PostConstruct
//    void testEmail() {
//
//        //    System.out.println("checking" + System.getenv());
//        scheduler.scheduleTaskUsingCronExpression();
//    }

}
