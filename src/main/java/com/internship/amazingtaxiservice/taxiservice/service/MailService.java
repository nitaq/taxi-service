package com.internship.amazingtaxiservice.taxiservice.service;

import com.internship.amazingtaxiservice.taxiservice.model.User;
import com.internship.amazingtaxiservice.taxiservice.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.thymeleaf.spring4.SpringTemplateEngine;
import javax.mail.internet.MimeMessage;
import java.util.Locale;
import java.util.logging.Level;
import org.thymeleaf.context.Context;
import java.util.logging.Logger;


@Service
public class MailService {

    private static final String URL = "url";
    private static final String USER = "user";
    private static final String EMAIL = "email";

    @Autowired
    private JavaMailSender javaMailSender;

    @Autowired
    private SpringTemplateEngine templateEngine;

    @Autowired
    UserRepository userRepository;

    @Autowired
    PasswordEncoder passwordEncoder;


    public void constructResetTokenEmail(String contextPath, String token, User user) {
        String url = contextPath + "?id=" + user.getId() + "&token=" + token;
        sendEmailFromTemplate(user, "email-template", "Reset password email", url);
    }


    public void constructChangeEmailLink(String contextPath, String token, String email, User user) {
        String url = contextPath + "/api/auth/user/changeEmail" + "?token=" + token + "&email=" + email;
        sendEmailFromTemplate(user, "resetEmail-template", "Change email", url);
    }


    @Async
    public void sendEmailFromTemplate(User user, String templateName, String titleKey, String urlLink) {
        Locale locale = Locale.forLanguageTag("EN");
        Context context = new Context(locale);
        context.setVariable(USER, user);
        context.setVariable(URL, urlLink);
        String content = templateEngine.process(templateName, context);
        sendEmail(user.getUsername(), titleKey, content, false, true);
    }


    @Async
    public void sendEmailFromActivationTemplate(User user, String templateName, String titleKey, String urlLink) {
        Locale locale = Locale.forLanguageTag("EN");
        Context context = new Context(locale);
        context.setVariable(USER, user);
        context.setVariable(URL, urlLink);
        String content = templateEngine.process(templateName, context);
        sendEmail(user.getUsername(), titleKey, content, false, true);
    }


    @Async
    public void sendEmail(String to, String subject, String content, boolean isMultipart, boolean isHtml) {
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        try {
            MimeMessageHelper message = new MimeMessageHelper(mimeMessage, isMultipart);
            message.setTo("nitaqarri@gmail.com");
            message.setSubject(subject);
            message.setText(content, isHtml);

            javaMailSender.send(mimeMessage);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Async
    public boolean sendVerificationMail(String path,  User user) {
        try {
            String url = path + "/api/auth/activateUser?token=" + user.getToken();
            sendEmailFromActivationTemplate(user, "activation-template", "Activation email", url);
        } catch (Exception ex) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, ex.getMessage(), ex);
        }
        return true;
    }

}