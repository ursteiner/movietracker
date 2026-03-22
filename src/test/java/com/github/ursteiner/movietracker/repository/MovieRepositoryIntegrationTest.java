package com.github.ursteiner.movietracker.repository;

import static org.assertj.core.api.Assertions.*;

import com.github.ursteiner.movietracker.model.AppUser;
import com.github.ursteiner.movietracker.model.Movie;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.time.LocalDate;
import java.util.List;

@DataJpaTest
public class MovieRepositoryIntegrationTest {

    @Autowired
    private MovieRepository movieRepository;
    @Autowired
    private UserRepository userRepository;
    private Pageable paging;
    private AppUser user1;

    @BeforeEach
    void setUp() {
        movieRepository.deleteAll();
        userRepository.deleteAll();

        user1 = AppUser.builder().username("testuser1").build();
        user1 = userRepository.save(user1);

        movieRepository.save(Movie.builder().name("Movie not in Watchlist 1").dateWatched(LocalDate.parse("2026-01-10")).inWatchlist(false).user(user1).build());
        movieRepository.save(Movie.builder().name("Movie in Watchlist 1").dateWatched(LocalDate.parse("2026-02-15")).inWatchlist(true).user(user1).build());
        movieRepository.save(Movie.builder().name("Movie not in Watchlist 2").dateWatched(LocalDate.parse("2026-03-20")).inWatchlist(false).user(user1).build());
        movieRepository.save(Movie.builder().name("Different movie in Watchlist 1").dateWatched(LocalDate.parse("2026-04-25")).inWatchlist(true).user(user1).build());

        AppUser user2 = AppUser.builder().username("testuser2").build();
        user2 = userRepository.save(user2);

        movieRepository.save(Movie.builder().name("Movie not in Watchlist 1").dateWatched(LocalDate.parse("2026-01-10")).inWatchlist(false).user(user2).build());
        movieRepository.save(Movie.builder().name("Movie in Watchlist 1").dateWatched(LocalDate.parse("2026-02-15")).inWatchlist(true).user(user2).build());
        movieRepository.save(Movie.builder().name("Movie not in Watchlist 2").dateWatched(LocalDate.parse("2026-03-20")).inWatchlist(false).user(user2).build());
        movieRepository.save(Movie.builder().name("Different movie in Watchlist 1").dateWatched(LocalDate.parse("2026-04-25")).inWatchlist(true).user(user2).build());

        Sort.Order order = new Sort.Order(Sort.Direction.ASC,"name");
        paging = PageRequest.of(0, 10, Sort.by(order));
    }

    @Test
    void testFindByNameStartingWithIgnoreCaseAndInWatchlistFalseOrderByDateWatchedDesc() {
        Page<Movie> results = movieRepository.findByUserIdAndNameContainingIgnoreCaseAndInWatchlistFalse(user1.getId(), "mov", paging);
        assertThat(results).extracting(Movie::getName)
                .containsExactly("Movie not in Watchlist 1", "Movie not in Watchlist 2");
    }

    @Test
    void testFindByInWatchlistFalse() {
        Page<Movie> results = movieRepository.findByUserIdAndInWatchlistFalse(user1.getId(), paging);
        assertThat(results).extracting(Movie::getName)
                .containsExactly("Movie not in Watchlist 1", "Movie not in Watchlist 2");
    }

    @Test
    void testFindByInWatchlistTrueOrderByNameAsc() {
        List<Movie> results = movieRepository.findByUserIdAndInWatchlistTrueOrderByNameAsc(user1.getId());
        assertThat(results).extracting(Movie::getName)
                .containsExactly("Different movie in Watchlist 1", "Movie in Watchlist 1");
    }

}
