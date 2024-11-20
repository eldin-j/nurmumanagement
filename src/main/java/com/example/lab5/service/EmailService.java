package com.example.lab5.service;

import com.sendgrid.*;
import com.sendgrid.helpers.mail.Mail;
import com.sendgrid.helpers.mail.objects.Content;
import com.sendgrid.helpers.mail.objects.Email;
import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class EmailService {

    private final String sendGridApiKey;
    private final String fromEmail;

    public EmailService() {
        Dotenv dotenv = Dotenv.configure().load();
        this.sendGridApiKey = dotenv.get("SENDGRID_API_KEY");
        this.fromEmail = dotenv.get("SENDGRID_FROM_EMAIL");
    }

    public void sendEmail(String to, String subject, String body) throws IOException {
        Email from = new Email(fromEmail);
        Email toEmail = new Email(to);
        Content content = new Content("text/plain", body);
        Mail mail = new Mail(from, subject, toEmail, content);

        SendGrid sg = new SendGrid(sendGridApiKey);
        Request request = new Request();
        try {
            request.setMethod(Method.POST);
            request.setEndpoint("mail/send");
            request.setBody(mail.build());
            sg.api(request);
        } catch (IOException ex) {
            throw ex;
        }
    }
}