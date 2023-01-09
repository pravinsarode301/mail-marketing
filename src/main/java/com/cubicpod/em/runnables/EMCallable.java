package com.cubicpod.em.runnables;


import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;

@Slf4j
@Component
public class EMCallable implements Callable<String> {

    List<String> safeList = Collections.synchronizedList(new ArrayList<>());

    @Override
    public String call() throws Exception {
        log.info("checking the final list size : {} ", safeList.size());
        safeList.clear();
        return "task done by callable";
    }

    public void setSafeList(List<String> safeList) {
        this.safeList = safeList;
    }
}
