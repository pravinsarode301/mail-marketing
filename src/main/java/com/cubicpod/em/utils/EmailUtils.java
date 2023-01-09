package com.cubicpod.em.utils;

import com.cubicpod.em.dto.EmailDetails;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.io.IOException;
import java.util.List;

@Slf4j
@Service
@Configuration
@EnableAsync
public class EmailUtils {
    @Autowired
    EmailDetails details;
    @Autowired
    Session session;

    @Async
    public void sendMail(List<String> recipientsList) throws IOException, MessagingException {
        log.info("recipient {}", recipientsList.toString());
        log.info("recipient {}", recipientsList.size());

        var transport = session.getTransport("smtp");

        if (!transport.isConnected())
            transport.connect();
        String addresses = String.join((CharSequence) ",", recipientsList.toArray(new String[]{}));
        InternetAddress[] address = InternetAddress.parse(addresses, true);

        MimeMessage message = new MimeMessage(session);
        message.setFrom(details.getFrom());
        // message.setRecipients(Message.RecipientType.TO, address);
        message.setSubject(details.getSubject());
        message.setContent("<h1>Hello, this is for proto testing!</h1>", "text/html");
        
        log.info("checking adds {} " + addresses);
        log.info("length : {}", address.length);


        try (transport) {
            transport.sendMessage(message, address);
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }

        log.info("completed..!");
    }


}
