package com.cubicpod.em.scheduler;

import com.cubicpod.em.utils.CSVUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@Slf4j
public class Scheduler {

    @Value("${recipientsFilePath}")
    String recipientsFilePath;
    @Autowired
    CSVUtils csvUtils;

    @Scheduled(fixedDelay = 1000000000000000000L)
    public void scheduleTaskUsingCronExpression() throws IOException, InterruptedException {

        long now = System.currentTimeMillis() / 1000;
        System.out.println("schedule tasks using cron jobs - " + now);
        log.info("executing..");
        
        csvUtils.processCSV(recipientsFilePath);

        //System.exit(0);
    }
}
