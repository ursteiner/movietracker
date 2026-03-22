package com.github.ursteiner.movietracker.repository;

import com.github.ursteiner.movietracker.model.Movie;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface MovieRepository extends JpaRepository<Movie, Long> {
    Page<Movie> findByUserIdAndInWatchlistFalse(Long appUserId, Pageable pageable);
    Page<Movie> findByUserIdAndNameContainingIgnoreCaseAndInWatchlistFalse(Long appUserId,String searchName, Pageable pageable);
    List<Movie> findByUserIdAndInWatchlistTrueOrderByNameAsc(Long appUserId);

    @Query(value = """
            SELECT SUBSTRING(date_watched, 1, 7) AS year_month, COUNT(*) AS count
            FROM movie
            WHERE in_watchlist = false 
            AND date_watched IS NOT NULL
            AND user_id = :userId
            GROUP BY year_month
            ORDER BY year_month DESC
            """,
            nativeQuery = true)
    List<Object[]> countMoviesWatchedPerYearMonthNative(@Param("userId") Long userId);
}
