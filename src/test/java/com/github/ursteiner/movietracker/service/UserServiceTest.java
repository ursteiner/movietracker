package com.github.ursteiner.movietracker.service;

import com.github.ursteiner.movietracker.model.AppUser;
import com.github.ursteiner.movietracker.repository.MovieRepository;
import com.github.ursteiner.movietracker.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private MovieRepository movieRepository;

    @InjectMocks
    private UserService userService;

    @Test
    void getUser_ShouldReturnUser_WhenAvailable() {
        AppUser testUser = new AppUser();

        when(userRepository.findById(any())).thenReturn(Optional.of(testUser));
        assertThat(userService.getUser(any())).isEqualTo(testUser);
    }

    @Test
    void getUser_ShouldThrowException_WhenNotAvailable() {
        when(userRepository.findById(any())).thenReturn(Optional.empty());
        assertThrows(ResponseStatusException.class, () -> userService.getUser(any()));
    }

    @Test
    void deleteAccount_ShouldDeleteMoviesAndUser() {
        //Should not throw
        userService.deleteAccount(any());
    }
}
