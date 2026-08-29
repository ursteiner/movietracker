package com.github.ursteiner.movietracker.service;

import com.github.ursteiner.movietracker.model.AppUser;
import com.github.ursteiner.movietracker.model.Movie;
import com.github.ursteiner.movietracker.repository.MovieRepository;
import com.github.ursteiner.movietracker.repository.UserRepository;

import static org.assertj.core.api.Assertions.assertThat;

import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

@ExtendWith(MockitoExtension.class)
public class MovieServiceTest {
    @Mock
    private MovieRepository movieRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private StreamingUrlService streamingUrlService;

    @InjectMocks
    private MovieService movieService;

    @Test
    void addMovie_shouldThrowException_whenUserIsNotFound() {
        UUID userId = UUID.randomUUID();
        String streamingUrl = "https://testflix.example/movies/12345";
        when(userRepository.findById(userId)).thenReturn(Optional.empty());
        when(streamingUrlService.getMovieId(streamingUrl)).thenReturn("12345");
        when(streamingUrlService.getServiceName(streamingUrl)).thenReturn("Testflix");

        Movie movie = Movie.builder()
                .name("Test Movie 1")
                .streamingUrl(streamingUrl)
                .build();

        assertThatThrownBy(() -> movieService.addMovie(userId, movie))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessage("User not found with id: " + userId);

        verify(userRepository).findById(userId);
        verify(streamingUrlService).getMovieId(streamingUrl);
        verify(streamingUrlService).getServiceName(streamingUrl);
        verify(movieRepository, never()).save(any(Movie.class));
    }

    @Test
    void addMovie_shouldReturnMovie_whenSuccessfulAdded() {
        AppUser user = AppUser.builder()
                .id(UUID.randomUUID())
                .build();
        String streamingUrl = "https://testflix.example/movies/12345";

        Movie movie = Movie.builder()
                .name("Test Movie 1")
                .streamingUrl(streamingUrl)
                .build();

        when(streamingUrlService.getMovieId(streamingUrl)).thenReturn("12345");
        when(streamingUrlService.getServiceName(streamingUrl)).thenReturn("Testflix");
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(movieRepository.save(any(Movie.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Movie savedMovie = movieService.addMovie(user.getId(), movie);
        assertThat(savedMovie.getName()).isEqualTo(movie.getName());
        assertThat(savedMovie.getUser().getId()).isEqualTo(user.getId());
        assertThat(savedMovie.getStreamingService()).isEqualTo("Testflix");
        assertThat(savedMovie.getMovieId()).isEqualTo("12345");

        verify(userRepository).findById(user.getId());
        verify(streamingUrlService).getMovieId(streamingUrl);
        verify(streamingUrlService).getServiceName(streamingUrl);
        verify(movieRepository).save(movie);
    }
}
