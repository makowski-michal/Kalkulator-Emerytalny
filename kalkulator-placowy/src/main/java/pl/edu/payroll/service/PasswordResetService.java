package pl.edu.payroll.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.edu.payroll.entity.PasswordResetToken;
import pl.edu.payroll.entity.User;
import pl.edu.payroll.repository.PasswordResetTokenRepository;
import pl.edu.payroll.repository.UserRepository;

import java.time.OffsetDateTime;
import java.util.UUID;

@Service
@Transactional
public class PasswordResetService {

    @Value("${app.base-url}")
    private String baseUrl;

    @Value("${app.password-reset.token-expiry-hours:1}")
    private int tokenExpiryHours;

    private final UserRepository userRepository;
    private final PasswordResetTokenRepository tokenRepository;
    private final JavaMailSender mailSender;
    private final PasswordEncoder passwordEncoder;

    public PasswordResetService(UserRepository userRepository,
                                 PasswordResetTokenRepository tokenRepository,
                                 JavaMailSender mailSender,
                                 PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.tokenRepository = tokenRepository;
        this.mailSender = mailSender;
        this.passwordEncoder = passwordEncoder;
    }

    public void requestReset(String email) {
        userRepository.findByEmail(email).ifPresent(user -> {
            String token = UUID.randomUUID().toString();

            PasswordResetToken prt = new PasswordResetToken();
            prt.setUser(user);
            prt.setToken(token);
            prt.setExpiresAt(OffsetDateTime.now().plusHours(tokenExpiryHours));
            tokenRepository.save(prt);

            String link = baseUrl + "/reset-password.html?token=" + token;
            SimpleMailMessage msg = new SimpleMailMessage();
            msg.setTo(email);
            msg.setSubject("Reset hasła – Kalkulator Płacowy");
            msg.setText("Kliknij w link, aby zresetować hasło:\n" + link +
                "\n\nLink wygasa po " + tokenExpiryHours + " godzinie(ach).");
            mailSender.send(msg);
        });
    }

    public void resetPassword(String token, String newPassword) {
        PasswordResetToken prt = tokenRepository.findByToken(token)
            .orElseThrow(() -> new IllegalArgumentException("Nieprawidłowy token"));

        if (prt.isUsed()) throw new IllegalArgumentException("Token już został użyty");
        if (prt.getExpiresAt().isBefore(OffsetDateTime.now()))
            throw new IllegalArgumentException("Token wygasł");

        User user = prt.getUser();
        user.setPasswordHash(passwordEncoder.encode(newPassword));
        userRepository.save(user);

        prt.setUsed(true);
        tokenRepository.save(prt);
    }
}
