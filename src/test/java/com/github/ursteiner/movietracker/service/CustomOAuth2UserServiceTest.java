package com.github.ursteiner.movietracker.service;

import com.github.ursteiner.movietracker.model.AppUser;
import com.github.ursteiner.movietracker.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.time.LocalDate;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CustomOAuth2UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private OAuth2UserRequest userRequest;

    @Mock
    private OAuth2UserService<OAuth2UserRequest, OAuth2User> delegateService;

    private CustomOAuth2UserService customOAuth2UserService;
    private AppUser testUser;

    @BeforeEach
    void setUp() {
        customOAuth2UserService = new CustomOAuth2UserService(userRepository, delegateService);

        testUser = new AppUser();
        testUser.setId(1L);
        testUser.setGithubId(12345);
        testUser.setUsername("testuser");
        testUser.setEmail("test@example.com");
        testUser.setRegistrationDate(LocalDate.now());
    }

    @Test
    void loadUser_ShouldReturnOAuth2User_WhenUserAlreadyExists() {
        when(userRepository.findUserByGithubId(12345)).thenReturn(testUser);
        OAuth2User oAuth2User = createMockOAuth2User(12345, "testuser", "test@example.com");
        when(delegateService.loadUser(userRequest)).thenReturn(oAuth2User);

        OAuth2User result = customOAuth2UserService.loadUser(userRequest);

        verify(userRepository).findUserByGithubId(12345);
        verify(userRepository, never()).save(any());
        assertThat(result).isNotNull();
        assertThat((Long) result.getAttribute("appUserId")).isEqualTo(1L);
    }

    @Test
    void loadUser_ShouldCreateAndSaveNewUser_WhenUserDoesNotExist() {
        when(userRepository.findUserByGithubId(12345)).thenReturn(null);
        when(userRepository.save(any(AppUser.class))).thenReturn(testUser);

        OAuth2User oAuth2User = createMockOAuth2User(12345, "testuser", "test@example.com");
        when(delegateService.loadUser(userRequest)).thenReturn(oAuth2User);

        OAuth2User result = customOAuth2UserService.loadUser(userRequest);

        ArgumentCaptor<AppUser> userCaptor = ArgumentCaptor.forClass(AppUser.class);
        verify(userRepository).save(userCaptor.capture());

        AppUser savedUser = userCaptor.getValue();
        assertThat(savedUser.getGithubId()).isEqualTo(12345);
        assertThat(savedUser.getUsername()).isEqualTo("testuser");
        assertThat(savedUser.getEmail()).isEqualTo("test@example.com");
        assertThat(savedUser.getRegistrationDate()).isNotNull();
        assertThat(result).isNotNull();
        assertThat((Long) result.getAttribute("appUserId")).isEqualTo(1L);
    }

    @Test
    void loadUser_ShouldHandleNullEmail_WhenNotProvidedByProvider() {
        when(userRepository.findUserByGithubId(12345)).thenReturn(null);
        AppUser userWithoutEmail = new AppUser();
        userWithoutEmail.setId(1L);
        userWithoutEmail.setGithubId(12345);
        userWithoutEmail.setUsername("userWithoutEmail");
        userWithoutEmail.setEmail(null);
        userWithoutEmail.setRegistrationDate(LocalDate.now());

        when(userRepository.save(any(AppUser.class))).thenReturn(userWithoutEmail);

        OAuth2User oAuth2User = createMockOAuth2User(12345, "userWithoutEmail", null);
        when(delegateService.loadUser(userRequest)).thenReturn(oAuth2User);

        OAuth2User result = customOAuth2UserService.loadUser(userRequest);

        ArgumentCaptor<AppUser> userCaptor = ArgumentCaptor.forClass(AppUser.class);
        verify(userRepository).save(userCaptor.capture());

        AppUser savedUser = userCaptor.getValue();
        assertThat(savedUser.getEmail()).isNull();
        assertThat(result).isNotNull();
    }

    @Test
    void loadUser_ShouldNotCreateDuplicate_OnMultipleLogins() {
        when(userRepository.findUserByGithubId(1234)).thenReturn(testUser);

        OAuth2User oAuth2User = createMockOAuth2User(1234, "test", "test@example.com");
        when(delegateService.loadUser(userRequest)).thenReturn(oAuth2User);

        customOAuth2UserService.loadUser(userRequest);
        customOAuth2UserService.loadUser(userRequest);
        customOAuth2UserService.loadUser(userRequest);

        verify(userRepository, times(3)).findUserByGithubId(1234);
        verify(userRepository, never()).save(any());
    }

    private OAuth2User createMockOAuth2User(Integer id, String login, String email) {
        Map<String, Object> attributes = new HashMap<>();
        attributes.put("id", id);
        attributes.put("login", login);
        attributes.put("email", email);
        return new DefaultOAuth2User(Collections.emptyList(), attributes, "login");
    }
}




