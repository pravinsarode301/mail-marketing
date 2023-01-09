package com.cubicpod.em.utils;

import com.cubicpod.em.dto.Recipient;
import com.cubicpod.em.runnables.EMCallable;
import com.cubicpod.em.runnables.EMRunnable;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import org.supercsv.cellprocessor.ParseInt;
import org.supercsv.cellprocessor.constraint.StrRegEx;
import org.supercsv.cellprocessor.ift.CellProcessor;
import org.supercsv.io.CsvBeanReader;
import org.supercsv.io.ICsvBeanReader;
import org.supercsv.prefs.CsvPreference;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;


/*
 policy no.
 712500/34/22/04/00000032


 */

@Component
@Slf4j
public class CSVUtils {
    @Autowired
    EmailUtils emailUtils;

    @Autowired
    ApplicationContext applicationContext;


    @Autowired
    EMCallable emCallable;
    @Autowired
    EMRunnable emRunnable; //= new EMRunnable();

    /**
     * Sets up the processors used for the examples.
     */
    private CellProcessor[] getProcessors() {
        final String emailRegex = "[a-z0-9\\._]+@[a-z0-9\\.]+";
        StrRegEx.registerMessage(emailRegex, "must be a valid email address");

        return new CellProcessor[]{
                new ParseInt(), //No
                new StrRegEx(emailRegex) // Email
        };
    }

    public void processCSV(final String recipientsFilePath) throws IOException, InterruptedException {


        List<String> lst = Collections.synchronizedList(new ArrayList<String>());

        File file = new File(recipientsFilePath);
        var totalNumberOfRow = Files.lines(file.toPath()).count();
        totalNumberOfRow = totalNumberOfRow - 1;

        ICsvBeanReader beanReader = new CsvBeanReader(new FileReader(recipientsFilePath), CsvPreference.STANDARD_PREFERENCE);

        var noOfThread = 1;
        int startIndex = 0;
        int lastIndex = 100;
        var threshold = 100;
        var readTillRowNo = 1;

        if (totalNumberOfRow < threshold) {
            threshold = (int) totalNumberOfRow;
            lastIndex = threshold;
        }

        noOfThread = Double.valueOf(Math.ceil(Float.valueOf(totalNumberOfRow) / 100)).intValue();
        ExecutorService executorService = Executors.newFixedThreadPool(noOfThread);

        try (beanReader) {
            // the header elements are used to map the values to the bean
            String[] headers = beanReader.getHeader(true);
            final CellProcessor[] processors = getProcessors();
            Recipient email;
            while ((email = beanReader.read(Recipient.class, headers, processors)) != null) {
                lst.add(email.getEmailId());

                if (lst.size() == threshold) {
                    //execute
                    List finalLst = Collections.synchronizedList(new ArrayList<>(lst.subList(startIndex, lastIndex)));
                    //log.info("size after sublist : {}", lst.size());
                    lst.clear();
                    emRunnable.setSafeList(finalLst);

//                    Runnable runnable = () -> {
//                        log.info("Thread " + Thread.currentThread().getId() + " is running");
//                        // log.info("checking the final list size : {} ", finalLst.size());
//
//                        try {
//                            //var obj = applicationContext.getBean(EmailUtils.class);
//                            emailUtils.sendMail(finalLst);
//                        } catch (IOException e) {
//                            throw new RuntimeException(e);
//                        } catch (javax.mail.MessagingException e) {
//                            throw new RuntimeException(e);
//                        }
//                        log.info(finalLst.toString());
//                    };

                    var rep = executorService.submit(emRunnable);
                    while (!rep.isDone()) {
                        //log.info("waiting");
                        Thread.sleep(1000l);
                    }
                    if (totalNumberOfRow - readTillRowNo < threshold) {
                        threshold = (int) (totalNumberOfRow - readTillRowNo);
                        lastIndex = threshold;
                    }
                }
                readTillRowNo++;
            }

        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            executorService.shutdown();
            executorService.awaitTermination(Long.MAX_VALUE, TimeUnit.MILLISECONDS);
        }
    }
}
