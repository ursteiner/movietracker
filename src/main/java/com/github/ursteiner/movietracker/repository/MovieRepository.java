package com.github.ursteiner.movietracker.repository;

import com.github.ursteiner.movietracker.model.Movie;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface MovieRepository extends JpaRepository<Movie, Long> {
    Page<Movie> findByInWatchlistFalseOrderByDateWatchedDesc(Pageable pageable);
    Page<Movie> findByNameContainingIgnoreCaseAndInWatchlistFalseOrderByDateWatchedDesc(String searchName, Pageable pageable);
    List<Movie> findByInWatchlistTrueOrderByNameAsc();

    @Query(value = """
            SELECT SUBSTRING(date_watched, 1, 7) AS year_month, COUNT(*) AS count
            FROM movie
            WHERE in_watchlist = false AND date_watched IS NOT NULL
            GROUP BY year_month
            ORDER BY year_month DESC
            """,
            nativeQuery = true)
    List<Object[]> countMoviesWatchedPerYearMonthNative();
}
