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
import static org.mockito.Mockito.when;
import static org.mockito.ArgumentMatchers.any;

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
        when(userRepository.findById(any())).thenReturn(Optional.empty());

        Movie movie = new Movie();
        movie.setName("Test Movie 1");

        assertThatThrownBy(() -> movieService.addMovie(UUID.randomUUID(), movie))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("User not found with id: ");
    }

    @Test
    void addMovie_shouldReturnMovie_whenSuccessfulAdded() {
        AppUser user = new AppUser();
        user.setId(UUID.randomUUID());

        Movie movie = new Movie();
        movie.setName("Test Movie 1");

        when(streamingUrlService.getMovieId(any())).thenReturn("12345");
        when(streamingUrlService.getServiceName(any())).thenReturn("Testflix");
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(movieRepository.save(any())).thenReturn(movie);

        Movie savedMovie = movieService.addMovie(user.getId(), movie);
        assertThat(savedMovie.getName()).isEqualTo(movie.getName());
        assertThat(savedMovie.getUser().getId()).isEqualTo(user.getId());
        assertThat(savedMovie.getStreamingService()).isEqualTo("Testflix");
    }
}
