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

    static final int MIN_RATING = 1;
    static final int MAX_RATING = 5;
    static final int MAX_DESCRIPTION_LENGTH = 1000;

    private final MovieRepository movieRepository;
    private final UserRepository userRepository;
    private final StreamingUrlService streamingUrlService;

    public MovieService(MovieRepository movieRepository, UserRepository userRepository, StreamingUrlService streamingUrlService) {
        this.movieRepository = movieRepository;
        this.userRepository = userRepository;
        this.streamingUrlService = streamingUrlService;
    }

    public Page<Movie> getWatchedMovies(UUID userId, Pageable paging) {
        Page<Movie> moviePage = movieRepository.findByUserIdAndDateWatchedIsNotNull(userId, paging);
        fillStreamingUrl(moviePage.getContent());
        return moviePage;
    }

    public Page<Movie> getSearchedMovies(UUID userId, String searchName, Pageable paging) {
        Page<Movie> moviePage = movieRepository.findByUserIdAndNameContainingIgnoreCase(userId, searchName, paging);
        fillStreamingUrl(moviePage.getContent());
        return moviePage;
    }

    public Page<Movie> getWatchlistMovies(UUID userId, Pageable paging) {
        Page<Movie> moviePage = movieRepository.findByUserIdAndDateWatchedIsNull(userId, paging);
        fillStreamingUrl(moviePage.getContent());
        return moviePage;
    }

    public Movie addMovie(UUID userId, Movie movie) {
        movie.setId(null);
        normalizeReview(movie);
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
        movie.setRating(changes.getRating());
        movie.setDescription(changes.getDescription());
        normalizeReview(movie);
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

    /** Rating is only valid between 1 and 5; blank descriptions are stored as null and long descriptions are cut off. */
    private void normalizeReview(Movie movie) {
        Integer rating = movie.getRating();
        movie.setRating(rating == null || rating < MIN_RATING || rating > MAX_RATING ? null : rating);
        String description = movie.getDescription();
        if (description == null || description.isBlank()) {
            movie.setDescription(null);
        } else {
            String trimmed = description.strip();
            movie.setDescription(trimmed.length() > MAX_DESCRIPTION_LENGTH ? trimmed.substring(0, MAX_DESCRIPTION_LENGTH) : trimmed);
        }
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
