package com.example.SpringDocumentationAI.services;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Service
public class MailService {

    private final JavaMailSender javaMailSender;

    private final MimeMessageHelper helper;

    @Autowired
    public MailService(@Value("${mail.service.from}") String from, JavaMailSender javaMailSender) throws MessagingException {
        this.javaMailSender = javaMailSender;
        MimeMessage message = javaMailSender.createMimeMessage();
        helper = new MimeMessageHelper(message, true);
        helper.setFrom(from);
    }

    public boolean sendEmail(String to, String subject, String mailTemplate, String dynamicLink) throws MessagingException {
        try (var inputStream = JavaMailSender.class.getResourceAsStream(mailTemplate)) {
            assert inputStream != null;
            String htmlTemplate = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
            String emailContent = htmlTemplate.replace("${dynamicLink}", dynamicLink);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(emailContent, true);
            helper.addInline("favicon.png", new File("src/main/resources/static/images/favicon.png"));
            javaMailSender.send(helper.getMimeMessage());
            return true;
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (MessagingException e) {
            throw new MessagingException("Error while sending email: " + e);
        }
    }
}
