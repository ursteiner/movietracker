package com.github.ursteiner.movietracker.repository;

import static org.assertj.core.api.Assertions.*;
import com.github.ursteiner.movietracker.model.Movie;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;

@DataJpaTest
public class MovieRepositoryIntegrationTest {

    @Autowired
    private MovieRepository movieRepository;
    private final SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
    private Pageable paging;
    
    @BeforeEach
    void setUp() throws ParseException {
        movieRepository.deleteAll();

        movieRepository.save(Movie.builder().name("Movie not in Watchlist 1").dateWatched(formatter.parse("2026-01-10")).inWatchlist(false).build());
        movieRepository.save(Movie.builder().name("Movie in Watchlist 1").dateWatched(formatter.parse("2026-02-15")).inWatchlist(true).build());
        movieRepository.save(Movie.builder().name("Movie not in Watchlist 2").dateWatched(formatter.parse("2026-03-20")).inWatchlist(false).build());
        movieRepository.save(Movie.builder().name("Different movie in Watchlist 1").dateWatched(formatter.parse("2026-04-25")).inWatchlist(true).build());
        
        paging = PageRequest.of(0, 10);
    }

    @Test
    void testFindByNameStartingWithIgnoreCaseAndInWatchlistFalseOrderByDateWatchedDesc() {
        Page<Movie> results = movieRepository.findByNameStartingWithIgnoreCaseAndInWatchlistFalseOrderByDateWatchedDesc("mov", paging);
        assertThat(results).extracting(Movie::getName)
                .containsExactly("Movie not in Watchlist 2", "Movie not in Watchlist 1");
    }

    @Test
    void testFindByInWatchlistFalseOrderByDateWatchedDesc() {
        Page<Movie> results = movieRepository.findByInWatchlistFalseOrderByDateWatchedDesc(paging);
        assertThat(results).extracting(Movie::getName)
                .containsExactly("Movie not in Watchlist 2", "Movie not in Watchlist 1");
    }

    @Test
    void testFindByInWatchlistTrueOrderByNameAsc() {
        List<Movie> results = movieRepository.findByInWatchlistTrueOrderByNameAsc();
        assertThat(results).extracting(Movie::getName)
                .containsExactly("Different movie in Watchlist 1", "Movie in Watchlist 1");
    }

}
