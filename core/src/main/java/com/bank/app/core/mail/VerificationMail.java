package com.bank.app.core.mail;

import jakarta.mail.Message;
import jakarta.mail.internet.InternetAddress;

import java.util.Base64;

public class VerificationMail extends Mailable{
    private String to;
    private String verificationCode;

    public VerificationMail(String to, String verificationCode) {
        this.to = to;
        this.verificationCode = verificationCode;
    }

    @Override
    public void build(Message message) throws Exception {
        message.setRecipient(Message.RecipientType.TO, new InternetAddress(to));
        message.setSubject("Email Verification - Banking System");

        String encode = Base64.getEncoder().encodeToString(to.getBytes());
        String link = "http://localhost:8080/banking-system/verify?id="+encode+"&vc="+verificationCode;

        String htmlContent = "<!DOCTYPE html>" +
            "<html>" +
            "<head>" +
                "<meta charset=\"UTF-8\">" +
                "<title>Email Verification</title>" +
                "<style>" +
                    "body { font-family: Arial, sans-serif; line-height: 1.6; color: #333; }" +
                    ".container { max-width: 600px; margin: 0 auto; padding: 20px; }" +
                    ".header { background: linear-gradient(135deg, #667eea 0%, #764ba2 100%); color: white; padding: 20px; text-align: center; border-radius: 10px 10px 0 0; }" +
                    ".content { background: #f9f9f9; padding: 30px; border-radius: 0 0 10px 10px; }" +
                    ".verification-code { background: #e3f2fd; padding: 15px; border-radius: 5px; margin: 20px 0; text-align: center; font-size: 18px; font-weight: bold; color: #1976d2; }" +
                    ".button { display: inline-block; background: linear-gradient(135deg, #667eea 0%, #764ba2 100%); color: white; padding: 12px 24px; text-decoration: none; border-radius: 5px; margin: 20px 0; }" +
                    ".footer { margin-top: 30px; padding-top: 20px; border-top: 1px solid #ddd; font-size: 14px; color: #666; }" +
                "</style>" +
            "</head>" +
            "<body>" +
                "<div class=\"container\">" +
                    "<div class=\"header\">" +
                        "<h1>Email Verification</h1>" +
                        "<p>Welcome to Banking System</p>" +
                    "</div>" +
                    "<div class=\"content\">" +
                        "<h2>Verify Your Email Address</h2>" +
                        "<p>Thank you for registering with our Banking System. To complete your registration, please verify your email address using one of the methods below:</p>" +
                        
                        "<h3>Method 1: Click the verification link</h3>" +
                        "<p><a href=\"" + link + "\" class=\"button\">Verify Email Address</a></p>" +
                        
                        "<h3>Method 2: Enter verification code manually</h3>" +
                        "<p>If the link doesn't work, you can manually enter this verification code:</p>" +
                        "<div class=\"verification-code\">" + verificationCode + "</div>" +
                        "<p>Go to: <a href=\"http://localhost:8080/banking-system/verify_email.jsp\">Verification Page</a></p>" +
                        
                        "<div class=\"footer\">" +
                            "<p><strong>Important:</strong></p>" +
                            "<ul>" +
                                "<li>This verification code is valid for 24 hours</li>" +
                                "<li>If you didn't register for this account, please ignore this email</li>" +
                                "<li>For security reasons, never share this verification code with anyone</li>" +
                            "</ul>" +
                            "<p>If you have any questions, please contact our support team.</p>" +
                        "</div>" +
                    "</div>" +
                "</div>" +
            "</body>" +
            "</html>";

        message.setContent(htmlContent, "text/html; charset=utf-8");
    }
}
