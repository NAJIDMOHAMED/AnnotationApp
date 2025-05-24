package ma.najid.annotationapp.service.Email;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {
    private static final Logger logger = LoggerFactory.getLogger(EmailService.class);
    @Value("${spring.mail.username}")
    private   String FROM_EMAIL ;

    @Autowired
    private JavaMailSender mailSender;

    public void sendPasswordEmail(String toEmail, String username, String password) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(FROM_EMAIL);
            message.setTo(toEmail);
            message.setSubject("Votre mot de passe temporaire");
            message.setText("Bonjour " + username + ",\n\nVoici votre mot de passe temporaire : " + password +
                    "\n\nVeuillez le changer dès votre première connexion.\n\nCordialement,\nL'équipe de support");

            mailSender.send(message);
            logger.info("Email de mot de passe envoyé avec succès de {} à {}", FROM_EMAIL, toEmail);
        } catch (MailException e) {
            logger.error("Erreur lors de l'envoi de l'email de {} à {} : {}", FROM_EMAIL, toEmail, e.getMessage());
            throw new RuntimeException("Erreur lors de l'envoi de l'email", e);
        }
    }
}
