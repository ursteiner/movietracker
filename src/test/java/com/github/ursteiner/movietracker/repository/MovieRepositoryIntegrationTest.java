package com.github.ursteiner.movietracker.repository;

import static org.assertj.core.api.Assertions.*;

import com.github.ursteiner.movietracker.model.AppUser;
import com.github.ursteiner.movietracker.model.Movie;
import com.github.ursteiner.movietracker.model.MoviesPerMonthDTO;
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

        saveMovie("Movie not in Watchlist 1", "2026-01-10", "s1", user1);
        saveMovie("Movie in Watchlist 1", null, "s1", user1);
        saveMovie("Movie not in Watchlist 2", "2026-03-20", "s1", user1);
        saveMovie("Different movie in Watchlist 1", null, "s1", user1);
        saveMovie("One more movie", null, "s1", user1);

        AppUser user2 = AppUser.builder().username("testuser2").build();
        user2 = userRepository.save(user2);

        saveMovie("Movie not in Watchlist 1", "2026-01-10", "s1", user2);
        saveMovie("Movie in Watchlist 1", null, "s1", user2);
        saveMovie("Movie not in Watchlist 2", "2026-03-20", "s1", user2);
        saveMovie("Different movie in Watchlist 1", null, "s1", user2);

        Sort.Order order = new Sort.Order(Sort.Direction.ASC, "name");
        paging = PageRequest.of(0, 10, Sort.by(order));
    }

    @Test
    void testFindByUserIdAndNameContainingIgnoreCaseAndDateWatchedIsNotNull() {
        Page<Movie> results = movieRepository.findByUserIdAndNameContainingIgnoreCaseAndDateWatchedIsNotNull(user1.getId(), "mov", paging);
        assertThat(results).extracting(Movie::getName)
                .containsExactly("Movie not in Watchlist 1", "Movie not in Watchlist 2");
    }

    @Test
    void testFindByUserIdAndDateWatchedIsNotNull() {
        Page<Movie> results = movieRepository.findByUserIdAndDateWatchedIsNotNull(user1.getId(), paging);
        assertThat(results).extracting(Movie::getName)
                .containsExactly("Movie not in Watchlist 1", "Movie not in Watchlist 2");
    }

    @Test
    void testFindByUserIdAndInWatchlistTrueOrderByNameAsc() {
        Page<Movie> results = movieRepository.findByUserIdAndDateWatchedIsNull(user1.getId(), paging);
        assertThat(results).extracting(Movie::getName)
                .containsExactly("Different movie in Watchlist 1", "Movie in Watchlist 1", "One more movie");
    }

    @Test
    void testCountMoviesWatchedPerYearMonthNative() {
        List<MoviesPerMonthDTO> moviesPerMonth = movieRepository.countMoviesWatchedPerYearMonthNative(user1.getId());

        var first = moviesPerMonth.getFirst();
        assertThat(first.getCountTotal()).isEqualTo(1L);
        assertThat(first.getYearMonth()).isEqualTo("2026-03");

        var last = moviesPerMonth.getLast();
        assertThat(last.getCountTotal()).isEqualTo(1L);
        assertThat(last.getYearMonth()).isEqualTo("2026-01");
    }

    private void saveMovie(String name, String watchDate, String streamer, AppUser user) {
        movieRepository.save(
                Movie.builder()
                        .name(name)
                        .dateWatched(watchDate == null ? null : LocalDate.parse(watchDate))
                        .streamingService(streamer)
                        .user(user)
                        .build()
        );
    }

}
