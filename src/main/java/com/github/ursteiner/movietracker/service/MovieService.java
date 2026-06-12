package com.github.ursteiner.movietracker.service;

import com.github.ursteiner.movietracker.model.Movie;
import com.github.ursteiner.movietracker.model.MoviesPerMonthDTO;
import com.github.ursteiner.movietracker.repository.MovieRepository;
import com.github.ursteiner.movietracker.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.UUID;

@Service
public class MovieService {

    private final MovieRepository movieRepository;
    private final UserRepository userRepository;
    private final StreamingUrlService streamingUrlService;

    public MovieService(MovieRepository movieRepository, UserRepository userRepository, StreamingUrlService streamingUrlService) {
        this.movieRepository = movieRepository;
        this.userRepository = userRepository;
        this.streamingUrlService = streamingUrlService;
    }

    public Page<Movie> getWatchedMovies(UUID userId, String searchName, Pageable paging) {
        Page<Movie> moviePage = (searchName != null)
                ? movieRepository.findByUserIdAndNameContainingIgnoreCaseAndDateWatchedIsNotNull(userId, searchName, paging)
                : movieRepository.findByUserIdAndDateWatchedIsNotNull(userId, paging);
        fillStreamingUrl(moviePage.getContent());
        return moviePage;
    }

    public Page<Movie> getWatchlistMovies(UUID userId, Pageable paging) {
        Page<Movie> moviePage = movieRepository.findByUserIdAndDateWatchedIsNull(userId, paging);
        fillStreamingUrl(moviePage.getContent());
        return moviePage;
    }

    public Movie addMovie(UUID userId, Movie movie) {
        applyStreamingInfo(movie, movie.getStreamingUrl());
        movie.setUser(userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + userId)));
        return movieRepository.save(movie);
    }

    public Movie getMovieForEdit(UUID userId, UUID id) {
        Movie movie = getOwnedMovie(userId, id);
        fillStreamingUrl(movie);
        return movie;
    }

    public Movie getOwnedMovie(UUID userId, UUID id) {
        return movieRepository.findByIdAndUserId(id, userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Movie not found"));
    }

    public Movie updateMovie(UUID userId, UUID id, Movie changes) {
        Movie movie = getOwnedMovie(userId, id);
        movie.setName(changes.getName());
        movie.setDateWatched(changes.getDateWatched());
        applyStreamingInfo(movie, changes.getStreamingUrl());
        return movieRepository.save(movie);
    }

    public Movie deleteMovie(UUID userId, UUID id) {
        Movie movie = getOwnedMovie(userId, id);
        movieRepository.delete(movie);
        return movie;
    }

    public List<MoviesPerMonthDTO> getMoviesPerMonth(UUID userId) {
        return movieRepository.countMoviesWatchedPerYearMonthNative(userId);
    }

    private void applyStreamingInfo(Movie movie, String url) {
        movie.setMovieId(streamingUrlService.getMovieId(url));
        movie.setStreamingService(streamingUrlService.getServiceName(url));
    }

    private void fillStreamingUrl(List<Movie> movies) {
        movies.forEach(this::fillStreamingUrl);
    }

    private void fillStreamingUrl(Movie movie) {
        movie.setStreamingUrl(streamingUrlService.getMovieWatchUrl(movie.getStreamingService(), movie.getMovieId()));
    }
}
