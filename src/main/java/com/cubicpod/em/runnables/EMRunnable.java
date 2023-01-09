package com.cubicpod.em.runnables;

import com.cubicpod.em.utils.EmailUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Slf4j
@Component
public class EMRunnable implements Runnable {


    @Autowired
    EmailUtils emailUtils;

    List<String> safeList = Collections.synchronizedList(new ArrayList<>());


    @Override
    public void run() {
        log.info("Thread " + Thread.currentThread().getId() + " is running");
        log.info("checking the final list size : {} ", safeList.size());

        try {
            emailUtils.sendMail(safeList);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (javax.mail.MessagingException e) {
            throw new RuntimeException(e);
        }

        log.info(safeList.toString());
    }

    public void setSafeList(List<String> safeList) {
        this.safeList = safeList;
    }
}
