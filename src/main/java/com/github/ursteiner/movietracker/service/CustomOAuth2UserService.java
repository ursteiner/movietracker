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
import java.util.HashMap;
import java.util.Map;

@Service
public class CustomOAuth2UserService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {

    private final UserRepository userRepository;

    @Autowired
    public CustomOAuth2UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2UserService<OAuth2UserRequest, OAuth2User> delegate = new DefaultOAuth2UserService();
        OAuth2User oAuth2User = delegate.loadUser(userRequest);

        Integer providerUserId = oAuth2User.getAttribute("id");
        String userName = oAuth2User.getAttribute("login");
        String email = oAuth2User.getAttribute("email");

        // Lookup or create user
        AppUser appUser = processOAuthLogin(providerUserId, email, userName);

        Map<String, Object> attributes = new HashMap<>(oAuth2User.getAttributes());
        attributes.put("appUserId", appUser.getId());

        return new DefaultOAuth2User(
                oAuth2User.getAuthorities(),
                attributes,
                "login"
        );
    }

    private AppUser processOAuthLogin(Integer providerUserId, String email, String userName) {
        AppUser user = userRepository.findUserByGithubId(providerUserId);

        if (user == null) {
            AppUser newUser = new AppUser();
            newUser.setGithubId(providerUserId);
            newUser.setUsername(userName);
            newUser.setEmail(email);
            newUser.setRegistrationDate(LocalDate.now());
            return userRepository.save(newUser);
        }

        return user;
    }
}