package com.github.ursteiner.movietracker.service;

import com.github.ursteiner.movietracker.model.AppUser;
import com.github.ursteiner.movietracker.repository.MovieRepository;
import com.github.ursteiner.movietracker.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;
import java.util.UUID;

import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
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
        UUID userId = UUID.randomUUID();
        AppUser testUser = AppUser.builder()
                .id(userId)
                .build();


        when(userRepository.findById(userId)).thenReturn(Optional.of(testUser));
        assertThat(userService.getUser(userId)).isEqualTo(testUser);
    }

    @Test
    void getUser_ShouldThrowException_WhenNotAvailable() {
        UUID userId = UUID.randomUUID();
        when(userRepository.findById(userId)).thenReturn(Optional.empty());
        assertThrows(ResponseStatusException.class, () -> userService.getUser(userId));
    }

    @Test
    void deleteAccount_ShouldDeleteMoviesAndUser() {
        UUID userId = UUID.randomUUID();

        userService.deleteAccount(userId);

        verify(movieRepository, times(1)).deleteByUserId(userId);
        verify(userRepository, times(1)).deleteById(userId);

        InOrder inOrder = inOrder(movieRepository, userRepository);
        inOrder.verify(movieRepository).deleteByUserId(userId);
        inOrder.verify(userRepository).deleteById(userId);
    }
}
