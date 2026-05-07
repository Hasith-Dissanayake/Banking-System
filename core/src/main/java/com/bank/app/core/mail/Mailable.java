package com.bank.app.core.mail;

import com.bank.app.core.provider.MailServiceProvider;
import com.bank.app.core.util.Env;
import jakarta.mail.Message;
import jakarta.mail.Session;
import jakarta.mail.Transport;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;

public abstract class Mailable implements  Runnable{
    private MailServiceProvider mailServiceProvider;

    public Mailable(){
        mailServiceProvider = MailServiceProvider.getInstance();
    }

    @Override
    public void run() {
        try {
            Session session = Session.getInstance(mailServiceProvider.getProperties(),
                    mailServiceProvider.getAuthenticator());
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(Env.get("app.email")));
            build(message);
            Transport.send(message);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public abstract void build(Message message) throws Exception;
}

