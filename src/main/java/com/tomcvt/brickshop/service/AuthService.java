package com.tomcvt.brickshop.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.tomcvt.brickshop.clients.CvtCaptchaClient;
import com.tomcvt.brickshop.dto.CaptchaVerificationResponse;
import com.tomcvt.brickshop.dto.PassPayload;
import com.tomcvt.brickshop.events.EventProvider;
import com.tomcvt.brickshop.events.NotificationEvent;
import com.tomcvt.brickshop.exception.UserAlreadyExistsException;
import com.tomcvt.brickshop.model.PassRecoveryToken;
import com.tomcvt.brickshop.model.User;
import com.tomcvt.brickshop.repository.PassRecoveryTokenRepository;
import com.tomcvt.brickshop.repository.UserRepository;

@Service
public class AuthService {
    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(AuthService.class);
    private final UserRepository userRepository;
    private final EmailService emailService;
    private final PassRecoveryTokenRepository passRecoveryTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final CvtCaptchaClient cvtCaptchaClient;
    private final EventProvider eventProvider;
    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder, CvtCaptchaClient cvtCaptchaClient,
            EmailService emailService, PassRecoveryTokenRepository passRecoveryTokenRepository, EventProvider eventProvider) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.cvtCaptchaClient = cvtCaptchaClient;
        this.emailService = emailService;
        this.passRecoveryTokenRepository = passRecoveryTokenRepository;
        this.eventProvider = eventProvider;
    }

    public User findByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
    }

    public User getUserByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElse(null);
    }

    @Transactional
    public User registerActivatedUser(String username, String rawPassword, String email, String role) {
        if (userRepository.findByUsername(username).isPresent()) {
            throw new UserAlreadyExistsException("Username already exists");
        }
        // validating password is skipped for dev purposes
        User newUser = new User();
        newUser.setUsername(username);
        newUser.setPassword(passwordEncoder.encode(rawPassword));
        newUser.setRole(role);
        newUser.setEmail(email);
        newUser.setEnabled(true);
        return userRepository.save(newUser);
    }
    @Transactional
    public User registerUserWithCaptcha(String username, String rawPassword, String email, String captchaToken, String role) {
        validateCaptcha(captchaToken);
        if (userRepository.findByUsername(username).isPresent()) {
            throw new UserAlreadyExistsException("Username already exists");
        }
        if (userRepository.findByEmail(email).isPresent()) {
            throw new UserAlreadyExistsException("Email already registered");
        }
        validateUsername(username);
        validatePassword(rawPassword);
        User newUser = new User();
        newUser.setUsername(username);
        newUser.setPassword(passwordEncoder.encode(rawPassword));
        newUser.setRole(role);
        newUser.setEmail(email);
        newUser.setEnabled(true);
        newUser = userRepository.save(newUser);
        eventProvider.publishEvent(new NotificationEvent(
            "New User Registration",
            "A new user has registered with username: " + username + " and email: " + email,
            3
        ));
        return newUser;
    }

    public User registerUser(String username, String rawPassword, String email, String role) {
        if (userRepository.findByUsername(username).isPresent()) {
            throw new UserAlreadyExistsException("Username already exists");
        }
        validatePassword(rawPassword);
        User newUser = new User();
        newUser.setUsername(username);
        newUser.setPassword(passwordEncoder.encode(rawPassword));
        newUser.setRole("USER");
        newUser.setEnabled(false); // User is not activated by default
        return userRepository.save(newUser);
        //emailService.sendActivationEmail(newUser);
    }

    public boolean checkPassword(Long userId, String rawPassword) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        return passwordEncoder.matches(rawPassword, user.getPassword());
    }

    private void validateUsername(String username) {
        String pattern = "^[a-zA-Z0-9_]{3,20}$";
        if (!username.matches(pattern)) {
            throw new IllegalArgumentException("Username must be 3-20 characters long and can only contain letters, digits, and underscores.");
        }
    }

    private void validatePassword(String password) {
        String pattern = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$";
        if (!password.matches(pattern)) {
            throw new IllegalArgumentException("Password must be at least 8 characters long and include " +
                    "at least one uppercase letter, one lowercase letter, one digit, and one special character (@$!%*?&).");
        }
    }
    @Transactional
    public void changePassword(Long userId, PassPayload passPayload) {
        String oldRawPassword = passPayload.oldPassword();
        String newRawPassword = passPayload.newPassword();
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        if(!passwordEncoder.matches(oldRawPassword, user.getPassword())) {
            throw new IllegalArgumentException("Old password does not match");
        }
        validatePassword(newRawPassword);
        user.setPassword(passwordEncoder.encode(newRawPassword));
        userRepository.save(user);
    }

    public void validateCaptcha(String captchaToken) {
        CaptchaVerificationResponse res = null;
        try {
            res = cvtCaptchaClient.verifyCaptcha(captchaToken)
                .block();
        } catch (Exception e) {
            log.error("Captcha verification failed", e);
            throw new IllegalArgumentException("Captcha verification failed");
        }
        if (res == null || !res.success()) {
            throw new IllegalArgumentException("Invalid captcha token");
        }
    }
    @Transactional
    public void initiatePasswordRecovery(String email) {
        User user = userRepository.findByEmail(email)
            .orElseThrow(() -> new IllegalArgumentException("User with email not found"));
        if (user.getRole().equals("SUPERUSER")) {
            return; //Only way db admin can reset superuser password
        }
        passRecoveryTokenRepository.deleteByUser(user);
        PassRecoveryToken token = new PassRecoveryToken(user);
        passRecoveryTokenRepository.save(token);
        try {
            emailService.sendRecoveryEmail(user.getEmail(), token.getToken());
        } catch (Exception e) {
            log.error("Failed to send recovery email to " + email, e);
            throw new RuntimeException("Failed to send recovery email");
        }
    }

    @Transactional
    public void resetPasswordWithToken(String tokenStr, String newPassword, String confirmPassword) {
        PassRecoveryToken token = passRecoveryTokenRepository.findByToken(tokenStr)
            .orElseThrow(() -> new IllegalArgumentException("Invalid or expired password reset token"));
        if (token.isExpired()) {
            passRecoveryTokenRepository.delete(token);
            throw new IllegalArgumentException("Password reset token has expired");
        }
        if (!newPassword.equals(confirmPassword)) {
            throw new IllegalArgumentException("New password and confirmation do not match");
        }
        validatePassword(newPassword);
        User user = token.getUser();
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
        passRecoveryTokenRepository.delete(token);
    }
}
