package com.secondhand.common.utils.email;

import cn.hutool.core.util.RandomUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

@Component
public class EmailVerificationUtil {

    @Autowired
    private JavaMailSender sender;

    @Value("${spring.mail.username}")
    private String fromEmail;

    /**
     * Generate 6-digit random number verification code
     * @return Verification code string
     */
    public String generateVerificationCode() {
        return RandomUtil.randomNumbers(6);
    }

    /**
     * Send verification code email
     * @param toEmail Recipient email address
     * @param code Verification code
     */
    public void sendVerificationEmail(String toEmail, String code) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setSubject("[Fashion Clothes Exchange & Second-hand Trading Platform] Verification Message"); // Email subject
        message.setText("You are performing a verification operation, verification code: " + code + ". Do not disclose the verification code to others. This verification code is valid for 2 minutes."); // Email content
        message.setTo(toEmail); // Recipient's email address
        message.setFrom(fromEmail); // Sender's email address
        sender.send(message); // Call send method to send email
    }

    /**
     * Generate verification code and send email
     * @param toEmail Recipient email address
     * @return Generated verification code
     */
    public String sendVerificationCodeToEmail(String toEmail) {
        String code = generateVerificationCode();
        sendVerificationEmail(toEmail, code);
        return code;
    }
}
