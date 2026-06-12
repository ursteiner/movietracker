package com.github.ursteiner.movietracker.security;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

import java.util.UUID;

@Component
public class CurrentUserProvider {

    public UUID getCurrentUserId() {
        return getCurrentUser().getAttribute("appUserId");
    }

    public OAuth2User getCurrentUser() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof OAuth2User oAuth2User) {
            return oAuth2User;
        }
        throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found");
    }
}
