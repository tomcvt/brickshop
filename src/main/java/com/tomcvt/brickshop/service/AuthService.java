package com.tomcvt.brickshop.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.tomcvt.brickshop.clients.CvtCaptchaClient;
import com.tomcvt.brickshop.dto.CaptchaVerificationResponse;
import com.tomcvt.brickshop.dto.PassPayload;
import com.tomcvt.brickshop.exception.UserAlreadyExistsException;
import com.tomcvt.brickshop.model.User;
import com.tomcvt.brickshop.repository.UserRepository;

@Service
public class AuthService {
    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(AuthService.class);
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final CvtCaptchaClient cvtCaptchaClient;
    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder, CvtCaptchaClient cvtCaptchaClient) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.cvtCaptchaClient = cvtCaptchaClient;
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
        validatePassword(rawPassword);
        User newUser = new User();
        newUser.setUsername(username);
        newUser.setPassword(passwordEncoder.encode(rawPassword));
        newUser.setRole(role);
        newUser.setEmail(email);
        newUser.setEnabled(true); // User is not activated by default
        return userRepository.save(newUser);
        //emailService.sendActivationEmail(newUser);
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

    private void validatePassword(String password) {
        String pattern = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$";
        if (!password.matches(pattern)) {
            throw new IllegalArgumentException("Password must be at least 8 characters long and include " +
                    "at least one uppercase letter, one lowercase letter, one digit, and one special character (@$!%*?&).");
        }
    }

    public void changePassword(Long userId, PassPayload passPayload) {
        changePassword(userId, passPayload.oldPassword(), passPayload.newPassword());
    }

    public boolean changePassword(Long userId, String oldRawPassword, String newRawPassword) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        if (!checkPassword(userId, oldRawPassword)) {
            return false; // Old password does not match
        }
        validatePassword(newRawPassword);
        user.setPassword(passwordEncoder.encode(newRawPassword));
        userRepository.save(user);
        return true;
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
}
