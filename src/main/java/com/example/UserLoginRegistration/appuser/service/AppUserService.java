package com.example.UserLoginRegistration.appuser.service;

import com.example.UserLoginRegistration.appuser.domain.AppUser;
import com.example.UserLoginRegistration.appuser.repository.AppUserRepository;
import com.example.UserLoginRegistration.registration.token.ConfirmationToken;
import com.example.UserLoginRegistration.registration.token.ConfirmationTokenService;
import lombok.AllArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@AllArgsConstructor
public class AppUserService implements UserDetailsService {
    /** Find users when we try to log in*/

    private final static String USER_NOT_FOUND_MSG = "User with email %s not found";
    private final AppUserRepository appUserRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final ConfirmationTokenService confirmationTokenService;


    @Override
    public UserDetails loadUserByUsername(String email)
            throws UsernameNotFoundException {
        return appUserRepository.findByEmail(email)
                .orElseThrow(() ->
                        new UsernameNotFoundException(
                                String.format(USER_NOT_FOUND_MSG, email)));
    }

    // Send link to user to confirm email
    public String signUpUser(AppUser appUser){
        boolean userExists = appUserRepository
                .findByEmail(appUser.getEmail())
                .isPresent();
        if(userExists){
            // TODO: Check email and username are the same
            // TODO: if the email not confirmed send confirmation email
            throw new IllegalStateException("Email already taken");
        }
        String encodedPassword = bCryptPasswordEncoder.encode(appUser.getPassword());
        appUser.setPassword(encodedPassword);

        /** Add users into database*/
        appUserRepository.save(appUser);

        String token = UUID.randomUUID().toString();
        // TODO: Send confirmation token
        /** Create and save the token */
        ConfirmationToken confirmationToken = new ConfirmationToken(
                token,
                LocalDateTime.now(),
                LocalDateTime.now().plusMinutes(15),
                appUser
        );

        confirmationTokenService.saveConfirmationToken(confirmationToken);

        // TODO: Send email


        return token;
    }


    public int enableAppUser(String email) {
        return appUserRepository.enableAppUser(email);
    }
}
