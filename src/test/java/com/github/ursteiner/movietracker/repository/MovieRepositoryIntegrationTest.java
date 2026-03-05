package com.github.ursteiner.movietracker.repository;

import static org.assertj.core.api.Assertions.*;
import com.github.ursteiner.movietracker.model.Movie;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;

@DataJpaTest
public class MovieRepositoryIntegrationTest {

    @Autowired
    private MovieRepository movieRepository;
    private final SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");

    @BeforeEach
    void setUp() throws ParseException {
        movieRepository.deleteAll();

        movieRepository.save(Movie.builder().name("Star Wars").dateWatched(formatter.parse("2026-01-10")).inWatchlist(false).build());
        movieRepository.save(Movie.builder().name("Star Trek").dateWatched(formatter.parse("2026-02-15")).inWatchlist(true).build());
        movieRepository.save(Movie.builder().name("Stargate").dateWatched(formatter.parse("2026-03-20")).inWatchlist(false).build());
        movieRepository.save(Movie.builder().name("Inception").dateWatched(formatter.parse("2026-04-25")).inWatchlist(true).build());
    }

    @Test
    void testFindByNameStartingWithIgnoreCaseOrderByDateWatchedDesc() {
        List<Movie> results = movieRepository.findByNameStartingWithIgnoreCaseOrderByDateWatchedDesc("star");
        assertThat(results).extracting(Movie::getName)
                .containsExactly("Stargate", "Star Trek", "Star Wars");
    }

    @Test
    void testFindByInWatchlistFalseOrderByDateWatchedDesc() {
        List<Movie> results = movieRepository.findByInWatchlistFalseOrderByDateWatchedDesc();
        assertThat(results).extracting(Movie::getName)
                .containsExactly("Stargate", "Star Wars");
    }

    @Test
    void testFindByInWatchlistTrueOrderByNameAsc() {
        List<Movie> results = movieRepository.findByInWatchlistTrueOrderByNameAsc();
        assertThat(results).extracting(Movie::getName)
                .containsExactly("Inception", "Star Trek");
    }

}
