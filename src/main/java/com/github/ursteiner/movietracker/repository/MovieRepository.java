package com.github.ursteiner.movietracker.repository;

import com.github.ursteiner.movietracker.model.Movie;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MovieRepository extends JpaRepository<Movie, Long> {
    List<Movie> findByNameStartingWithIgnoreCaseOrderByDateWatchedDesc(String name);
    List<Movie> findByInWatchlistFalseOrderByDateWatchedDesc();
    List<Movie> findByInWatchlistTrueOrderByNameAsc();

}
