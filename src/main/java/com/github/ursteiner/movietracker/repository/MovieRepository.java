package com.github.ursteiner.movietracker.repository;

import com.github.ursteiner.movietracker.model.Movie;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MovieRepository extends JpaRepository<Movie, Long> {
    Page<Movie> findByInWatchlistFalseOrderByDateWatchedDesc(Pageable pageable);
    Page<Movie> findByNameContainingIgnoreCaseAndInWatchlistFalseOrderByDateWatchedDesc(String searchName, Pageable pageable);
    List<Movie> findByInWatchlistTrueOrderByNameAsc();

}
