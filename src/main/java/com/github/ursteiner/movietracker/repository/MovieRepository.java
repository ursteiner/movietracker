package com.github.ursteiner.movietracker.repository;

import com.github.ursteiner.movietracker.model.Movie;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MovieRepository extends JpaRepository<Movie, Long> {


}
