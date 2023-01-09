package com.cubicpod.em.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Configuration
public class EmailDetails {

    @Value("${subject}")
    private String subject;
    @Value("${cc}")
    private String cc;
    @Value("${bcc}")
    private String bcc;
    @Value("${attachments}")
    private String attachments;
    @Value("${recipientsFilePath}")
    private String recipientsFile;
    @Value("${from}")
    private String from;


    private List<String> recipientsList;
}