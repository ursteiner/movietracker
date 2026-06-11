package com.github.ursteiner.movietracker.service;

import com.github.ursteiner.movietracker.model.AppUser;
import com.github.ursteiner.movietracker.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Service
public class CustomOAuth2UserService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {

    private final UserRepository userRepository;
    private final OAuth2UserService<OAuth2UserRequest, OAuth2User> delegate;

    @Autowired
    public CustomOAuth2UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
        this.delegate = new DefaultOAuth2UserService();
    }

    CustomOAuth2UserService(UserRepository userRepository, OAuth2UserService<OAuth2UserRequest, OAuth2User> delegate) {
        this.userRepository = userRepository;
        this.delegate = delegate;
    }

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = delegate.loadUser(userRequest);

        Integer providerUserId = oAuth2User.getAttribute("id");
        String userName = oAuth2User.getAttribute("login");
        String email = oAuth2User.getAttribute("email");

        AppUser appUser = createOrGetUser(providerUserId, email, userName);

        Map<String, Object> attributes = new HashMap<>(oAuth2User.getAttributes());
        attributes.put("appUserId", appUser.getId());

        return new DefaultOAuth2User(
                oAuth2User.getAuthorities(),
                attributes,
                "login"
        );
    }

    private AppUser createOrGetUser(Integer providerUserId, String email, String userName) {
        AppUser user = userRepository.findUserByGithubId(providerUserId);

        if (user == null) {
            user = new AppUser();
            user.setGithubId(providerUserId);
            user.setUsername(userName);
            user.setEmail(email);
            user.setRegistrationDate(LocalDate.now());
        }

        user.setLastLogin(LocalDateTime.now());
        return userRepository.save(user);
    }
}