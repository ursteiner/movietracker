package com.github.ursteiner.movietracker.repository;

import com.github.ursteiner.movietracker.model.Movie;

import com.github.ursteiner.movietracker.model.MoviesPerMonthDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface MovieRepository extends JpaRepository<Movie, Long> {
    Page<Movie> findByUserIdAndDateWatchedIsNotNull(Long appUserId, Pageable pageable);
    Page<Movie> findByUserIdAndNameContainingIgnoreCaseAndDateWatchedIsNotNull(Long appUserId,String searchName, Pageable pageable);
    Page<Movie> findByUserIdAndDateWatchedIsNull(Long appUserId, Pageable pageable);
    Optional<Movie> findByIdAndUserId(Long movieId, Long userId);

    @Query(value = """
            SELECT
                SUBSTRING(date_watched, 1, 7) AS yearMonth,
                COUNT(CASE WHEN streaming_service = 'Amazon' THEN 1 END) AS countAmazon,
                COUNT(CASE WHEN streaming_service = 'Netflix' THEN 1 END) AS countNetflix,
                COUNT(CASE WHEN streaming_service = 'Youtube' THEN 1 END) AS countYoutube,
                COUNT(*) as countTotal
            FROM movie
            WHERE date_watched IS NOT NULL
                AND user_id = :userId
            GROUP BY yearMonth
            ORDER BY yearMonth DESC;
            """,
            nativeQuery = true)
    List<MoviesPerMonthDTO> countMoviesWatchedPerYearMonthNative(@Param("userId") Long userId);
}
