package com.karan.EmployeeOnboardingV2.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class EmailServiceTests {
    @Autowired
    private EmailService emailService;

    @Test
    void testSendMail() {
        emailService.sendEmail("*********", "JavaMailSender Test", "This mail has been sent in regards to a test conducted to validate the functionality of the api send email feature.please ignore\nThankyou");
    }
}
